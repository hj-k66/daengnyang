package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.notification.NotificationMultiUserRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.notification.NotificationOneUserRequest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

	@Value("${fcm.key.path}")
	private String FCM_PRIVATE_KEY_PATH;
	private final RedisTemplate redisTemplate;


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

}
