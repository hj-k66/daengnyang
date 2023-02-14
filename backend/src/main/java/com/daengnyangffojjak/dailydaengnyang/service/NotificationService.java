package com.daengnyangffojjak.dailydaengnyang.service;



import com.daengnyangffojjak.dailydaengnyang.domain.dto.notification.NotificationDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.notification.NotificationListResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.notification.NotificationMultiUserRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.notification.NotificationOneUserRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.notification.NotificationReadResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Notification;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.NotificationUser;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.repository.NotificationRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.NotificationUserRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.SendResponse;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

	@Value("${fcm.key.path}")
	private String FCM_PRIVATE_KEY_PATH;
	private final RedisTemplate redisTemplate;
	private final Validator validator;
	private final NotificationUserRepository notificationUserRepository;
	private final NotificationRepository notificationRepository;


	// fcm 기본 설정 진행
	@PostConstruct
	public void init() { //Firebase에 Admin 계정을 인증
		try {
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(
							GoogleCredentials
									.fromStream(new ClassPathResource(
											FCM_PRIVATE_KEY_PATH).getInputStream()))
					.build();
			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(options);
				log.info("Firebase application has been initialized");
			}
		} catch (IOException e) {
			log.error(e.getMessage());
			// spring 뜰때 알림 서버가 잘 동작하지 않는 것이므로 바로 죽임
			throw new RuntimeException(e.getMessage());
		}
	}

	public void sendOneUser(NotificationOneUserRequest notificationOneUserRequest)
			throws ExecutionException, InterruptedException {
		Message message = Message.builder()
				.setToken(notificationOneUserRequest.getUserToken())
				.setWebpushConfig(WebpushConfig.builder().putHeader("ttl", "300")
						.setNotification(
								new WebpushNotification(notificationOneUserRequest.getTitle(),
										notificationOneUserRequest.getBody()))
						.build())
				.build();

		String response = FirebaseMessaging.getInstance().sendAsync(message).get();
		log.info("Sent message: " + response);

	}

	public void sendByUserTokenList(NotificationMultiUserRequest notificationMultiUserRequest) {
		//메세지
		List<Message> messages = notificationMultiUserRequest.getUserTokenList().stream()
				.map(token -> Message.builder()
						.setToken(token)
						.setWebpushConfig(WebpushConfig.builder()
								.putHeader("ttl", "300")
								.setNotification(new WebpushNotification(
										notificationMultiUserRequest.getTitle(),
										notificationMultiUserRequest.getBody()))
								.build())
						.build()).collect(Collectors.toList());
		//response
		BatchResponse response;
		try {
			//메세지 전송
			response = FirebaseMessaging.getInstance().sendAll(messages);

			//전송 실패한 invalid token log 찍기
			if (response.getFailureCount() > 0) {
				List<SendResponse> responses = response.getResponses();
				List<String> failedTokens = new ArrayList<>();

				for (int i = 0; i < responses.size(); i++) {
					if (!responses.get(i).isSuccessful()) {
						failedTokens.add(notificationMultiUserRequest.getUserTokenList().get(i));
					}
				}
				log.error("List of invalid tokens : " + failedTokens);
			}
		} catch (FirebaseMessagingException e) {
			log.error("failed to send to memberList push message. error : " + e.getMessage());
		}
	}

	public void registerFCMToken(String userName, String token) {
		//fcm 토큰 redis에 저장
		//key: fcm:userName, value: token
		redisTemplate.opsForValue()
				.set("fcm:" + userName, token);
	}

	public void deleteToken(String userName) {
		//fcm 토큰 redis에서 삭제
		redisTemplate.delete("fcm:" + userName);
	}

	@Transactional(readOnly = true)
	public NotificationListResponse getAllNotification(Long lastNotificationId, int size,
			String username) {
		//user 있는지 검증
		User user = validator.getUserByUserName(username);

		//로그인 한 유저의 알림 목록
		//최근 30일 데이터만
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime before30days = now.minusDays(30);
		List<NotificationUser> selectedNotificationUser = notificationUserRepository.findAllByUserAndCreatedAtBetween(
				user, before30days, now);


		Page<Notification> notifications = fetchPages(lastNotificationId, size,selectedNotificationUser);
		return NotificationListResponse.from(notifications.getContent());


	}

	private Page<Notification> fetchPages(Long lastNotificationId, int size, List<NotificationUser> selectedNotificationUser) {
		PageRequest pageRequest = PageRequest.of(0, size); // 페이지네이션을 위한 PageRequest, 페이지는 0으로 고정한다.
		return notificationRepository.findByIdLessThanAndNotificationUserListInOrderByIdDesc(lastNotificationId,selectedNotificationUser,
				pageRequest); // lastNotificationId보다 작은 값의 id의 알람 조회
	}

	@Transactional
	public NotificationDeleteResponse delete(Long notificationId, String username) {

		//유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(username);

		//알람이 없는 경우 예외발생
		Notification notification = validator.getNotificationById(notificationId);

		//로그인유저 != 알람 유저일 경우 예외발생
		Long loginUserId = user.getId();
		NotificationUser notificationUser = validator.validateNotificationUser(notificationId,loginUserId);

		//알람 삭제
		notificationUser.deleteSoftly();
		notification.deleteSoftly();
		String message = "알람이 삭제되었습니다.";

		return new NotificationDeleteResponse(message,notificationId);

	}

	@Transactional
	public NotificationReadResponse checkTrue(Long notificationId, String username) {
		//로그인 유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(username);

		//알람이 없는 경우 예외발생
		Notification notification = validator.getNotificationById(notificationId);

		//로그인유저 != 알람 유저일 경우 예외발생
		Long loginUserId = user.getId();
		NotificationUser notificationUser = validator.validateNotificationUser(notificationId,loginUserId);


		//수정된 일정 저장
		notification.changeCheck();
		notificationRepository.saveAndFlush(notification);

		return new NotificationReadResponse("알람을 읽었습니다.",notificationId);

	}
}
