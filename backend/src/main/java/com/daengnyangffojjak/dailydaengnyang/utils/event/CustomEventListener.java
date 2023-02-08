package com.daengnyangffojjak.dailydaengnyang.utils.event;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.NotificationRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.NotificationType;
import com.daengnyangffojjak.dailydaengnyang.service.NotificationService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
@Async
@Slf4j
public class CustomEventListener {

	private final NotificationService notificationService;
	private final RedisTemplate redisTemplate;

	@EventListener
	public void handleScheduleCreateEvent(ScheduleCreateEvent scheduleCreateEvent) {
		//알림 보낼 멤버 목록
		List<String> userNameList = scheduleCreateEvent.getUserNameList();
		System.out.println(userNameList.toString());

		// 로그인한 유저의 fcmToken 뽑기
		// 로그아웃한 회원들은 redis에 저장 x
		List<String> loginUserTokenList = userNameList
				.stream()
				.filter(userName -> redisTemplate.hasKey("fcm:" + userName))
				.map(userName -> (String) redisTemplate.opsForValue().get("fcm:" + userName))
				.collect(Collectors.toList());
		//로그인한 유저에게 알림 보내기
		if (loginUserTokenList.size() != 0) {
			String message = NotificationType.SCHEDULE_CREATE.generateNotificationMessage(
					scheduleCreateEvent.getTitle(), scheduleCreateEvent.getUserName());
			NotificationRequest notificationRequest = NotificationRequest.toRequest(
					loginUserTokenList, NotificationType.SCHEDULE_CREATE, message);

			notificationService.sendByUserTokenList(notificationRequest);
		}
		log.info("알람 전송");

		//알림 기록 db에 저장
		//title, body, user_id, NotificationType 저장

	}

}
