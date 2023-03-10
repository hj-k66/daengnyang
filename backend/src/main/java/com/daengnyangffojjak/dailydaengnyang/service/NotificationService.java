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


	// fcm ?????? ?????? ??????
	@PostConstruct
	public void init() { //Firebase??? Admin ????????? ??????
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
			// spring ?????? ?????? ????????? ??? ???????????? ?????? ???????????? ?????? ??????
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
		//?????????
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
			//????????? ??????
			response = FirebaseMessaging.getInstance().sendAll(messages);

			//?????? ????????? invalid token log ??????
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
		//fcm ?????? redis??? ??????
		//key: fcm:userName, value: token
		redisTemplate.opsForValue()
				.set("fcm:" + userName, token);
	}

	public void deleteToken(String userName) {
		//fcm ?????? redis?????? ??????
		redisTemplate.delete("fcm:" + userName);
	}

	@Transactional(readOnly = true)
	public NotificationListResponse getAllNotification(Long lastNotificationId, Integer size,
			String username) {
		//user ????????? ??????
		User user = validator.getUserByUserName(username);

		//????????? ??? ????????? ?????? ??????
		//?????? 30??? ????????????
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime before30days = now.minusDays(30);
		List<NotificationUser> selectedNotificationUser = notificationUserRepository.findAllByUserAndCreatedAtBetween(
				user, before30days, now);


		Page<Notification> notifications = fetchPages(lastNotificationId, size,selectedNotificationUser);
		return NotificationListResponse.from(notifications.getContent());


	}

	private Page<Notification> fetchPages(Long lastNotificationId, Integer size, List<NotificationUser> selectedNotificationUser) {
		PageRequest pageRequest = PageRequest.of(0, size); // ????????????????????? ?????? PageRequest, ???????????? 0?????? ????????????.
		return notificationRepository.findByIdLessThanAndNotificationUserListInOrderByIdDesc(lastNotificationId,selectedNotificationUser,
				pageRequest); // lastNotificationId?????? ?????? ?????? id??? ?????? ??????
	}

	@Transactional
	public NotificationDeleteResponse delete(Long notificationId, String username) {

		//????????? ?????? ?????? ????????????
		User user = validator.getUserByUserName(username);

		//????????? ?????? ?????? ????????????
		Notification notification = validator.getNotificationById(notificationId);

		//??????????????? != ?????? ????????? ?????? ????????????
		Long loginUserId = user.getId();
		NotificationUser notificationUser = validator.validateNotificationUser(notificationId,loginUserId);

		//?????? ??????
		notificationUser.deleteSoftly();
		notification.deleteSoftly();
		String message = "????????? ?????????????????????.";

		return new NotificationDeleteResponse(message,notificationId);

	}

	@Transactional
	public NotificationReadResponse checkTrue(Long notificationId, String username) {
		//????????? ????????? ?????? ?????? ????????????
		User user = validator.getUserByUserName(username);

		//????????? ?????? ?????? ????????????
		Notification notification = validator.getNotificationById(notificationId);

		//??????????????? != ?????? ????????? ?????? ????????????
		Long loginUserId = user.getId();
		NotificationUser notificationUser = validator.validateNotificationUser(notificationId,loginUserId);


		//????????? ?????? ??????
		notification.changeCheck();
		notificationRepository.saveAndFlush(notification);

		return new NotificationReadResponse("????????? ???????????????.",notificationId);

	}
}
