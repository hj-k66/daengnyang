package com.daengnyangffojjak.dailydaengnyang.utils.event;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.notification.NotificationMultiUserRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.notification.NotificationOneUserRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.NotificationType;
import com.daengnyangffojjak.dailydaengnyang.service.NotificationService;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

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
		List<String> loginUserTokenList = getLoginUserFcmToken(
				scheduleCreateEvent.getUserNameList());

		//로그인한 유저에게 알림 보내기
		if (loginUserTokenList.size() != 0) {
			String message = NotificationType.SCHEDULE_CREATE.generateNotificationMessage(
					scheduleCreateEvent.getTitle(), scheduleCreateEvent.getUserName());

			NotificationMultiUserRequest notificationMultiUserRequest = NotificationMultiUserRequest.toRequest(
					loginUserTokenList, NotificationType.SCHEDULE_CREATE, message);

			notificationService.sendByUserTokenList(notificationMultiUserRequest);
		}
		log.info("알람 전송");

		//알림 기록 db에 저장
		//title, body, user_id, NotificationType 저장

	}

	@EventListener
	public void handleRecordCreateEvent(RecordCreateEvent recordCreateEvent) {
		List<String> loginUserTokenList = getLoginUserFcmToken(
				recordCreateEvent.getUserNameList());

		//로그인한 유저에게 알림 보내기
		if (loginUserTokenList.size() != 0) {
			String message = NotificationType.RECORD_CREATE.generateNotificationMessage(
					recordCreateEvent.getTitle(), recordCreateEvent.getUserName());
			NotificationMultiUserRequest notificationMultiUserRequest = NotificationMultiUserRequest.toRequest(
					loginUserTokenList, NotificationType.RECORD_CREATE, message);

			notificationService.sendByUserTokenList(notificationMultiUserRequest);
		}
		log.info("알람 전송");

		//알림 기록 db에 저장
		//title, body, user_id, NotificationType 저장

	}

	@EventListener
	public void handleGroupInviteEvent(GroupInviteEvent groupInviteEvent)
			throws ExecutionException, InterruptedException {
		String loginUserToken = (String) redisTemplate.opsForValue()
				.get("fcm:" + groupInviteEvent.getReceiverName());

		//receiver가 로그인했으면 알림 보내기
		if (!ObjectUtils.isEmpty(loginUserToken)) {
			String message = NotificationType.GROUP_INVITE.generateNotificationMessage(
					groupInviteEvent.getTitle(), groupInviteEvent.getSenderName());

			List<String> loginUserTokenList = Arrays.asList(loginUserToken);
			NotificationOneUserRequest notificationOneUserRequest = NotificationOneUserRequest.toRequest(
					loginUserToken, NotificationType.GROUP_INVITE, message);

			notificationService.sendOneUser(notificationOneUserRequest);
		}
		log.info("알람 전송");

		//알림 기록 db에 저장
		//title, body, user_id, NotificationType 저장

	}

	//알람 보낼 멤버 목록 중 로그인한 유저의 fcmToken 뽑기
	private List<String> getLoginUserFcmToken(List<String> userNameList) {
		// 로그인한 유저의 fcmToken 뽑기
		// 로그아웃한 회원들은 redis에 저장 x
		return userNameList
				.stream()
				.filter(userName -> redisTemplate.hasKey("fcm:" + userName))
				.map(userName -> (String) redisTemplate.opsForValue().get("fcm:" + userName))
				.collect(Collectors.toList());
	}

}
