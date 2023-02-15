package com.daengnyangffojjak.dailydaengnyang.utils.event;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.notification.NotificationMultiUserRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.notification.NotificationOneUserRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Notification;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.NotificationUser;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.NotificationType;
import com.daengnyangffojjak.dailydaengnyang.repository.NotificationRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.NotificationUserRepository;
import com.daengnyangffojjak.dailydaengnyang.service.NotificationService;
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
	private final NotificationRepository notificationRepository;
	private final NotificationUserRepository notificationUserRepository;
	private final RedisTemplate redisTemplate;

	@EventListener
	public void handleScheduleCompleteEvent(ScheduleCompleteEvent scheduleCompleteEvent) {
		List<String> loginUserTokenList = getLoginUserFcmToken(
				scheduleCompleteEvent.getUserList());

		//receiver가 로그아웃했으면 알림 보내기 X
		if (loginUserTokenList.size() == 0) {
			return;
		}

		//로그인한 유저에게 알림 보내기
		String message = NotificationType.SCHEDULE_COMPLETE.generateNotificationMessage(
				scheduleCompleteEvent.getTitle(), scheduleCompleteEvent.getUserName());

		NotificationMultiUserRequest notificationMultiUserRequest = NotificationMultiUserRequest.toRequest(
				loginUserTokenList, NotificationType.SCHEDULE_COMPLETE, message);

		notificationService.sendByUserTokenList(notificationMultiUserRequest);

		log.info("알람 전송");

		//알림 기록 db에 저장
		Notification savedNotification = notificationRepository.saveAndFlush(
				Notification.from(NotificationType.SCHEDULE_COMPLETE.getMessageTitle(),
						message, NotificationType.SCHEDULE_COMPLETE, false));
		//알람받을 유저 저장
		scheduleCompleteEvent.getUserList().forEach(
				user -> notificationUserRepository.save(NotificationUser.from(savedNotification,
						user)));
		log.info("알람 저장");

	}


	@EventListener
	public void handleScheduleAssignEvent(ScheduleAssignEvent scheduleAssignEvent)
			throws ExecutionException, InterruptedException {

		String loginUserToken = (String) redisTemplate.opsForValue()
				.get("fcm:" + scheduleAssignEvent.getReceiver().getUsername());

		//receiver가 로그아웃했으면 알림 보내기 X
		if (ObjectUtils.isEmpty(loginUserToken)) {
			return;
		}

		String body = NotificationType.SCHEDULE_ASSIGN.generateNotificationMessage(
				scheduleAssignEvent.getScheduleTitle(), scheduleAssignEvent.getSenderName())
				+ scheduleAssignEvent.getMessage();

		NotificationOneUserRequest notificationOneUserRequest = NotificationOneUserRequest.toRequest(
				loginUserToken, NotificationType.SCHEDULE_ASSIGN,
				body);

		notificationService.sendOneUser(notificationOneUserRequest);
		log.info("알람 전송");

		//알림 기록 db에 저장
		Notification savedNotification = notificationRepository.saveAndFlush(
				Notification.from(NotificationType.SCHEDULE_ASSIGN.getMessageTitle(),
						body, NotificationType.SCHEDULE_ASSIGN, false));
		//알람받을 유저 저장 >> 로그인 안한 유저도 저장
		notificationUserRepository.save(NotificationUser.from(savedNotification,
				scheduleAssignEvent.getReceiver()));


	}

	@EventListener
	public void handleScheduleCreateEvent(ScheduleCreateEvent scheduleCreateEvent) {
		List<String> loginUserTokenList = getLoginUserFcmToken(
				scheduleCreateEvent.getUserList());

		//receiver가 로그아웃했으면 알림 보내기 X
		if (loginUserTokenList.size() == 0) {
			return;
		}

		//로그인한 유저에게 알림 보내기
		String message = NotificationType.SCHEDULE_CREATE.generateNotificationMessage(
				scheduleCreateEvent.getTitle(), scheduleCreateEvent.getUserName());

		NotificationMultiUserRequest notificationMultiUserRequest = NotificationMultiUserRequest.toRequest(
				loginUserTokenList, NotificationType.SCHEDULE_CREATE, message);

		notificationService.sendByUserTokenList(notificationMultiUserRequest);

		log.info("알람 전송");

		//알림 기록 db에 저장
		Notification savedNotification = notificationRepository.saveAndFlush(
				Notification.from(NotificationType.SCHEDULE_CREATE.getMessageTitle(),
						message, NotificationType.SCHEDULE_CREATE, false));
		//알람받을 유저 저장
		scheduleCreateEvent.getUserList().forEach(
				user -> notificationUserRepository.save(NotificationUser.from(savedNotification,
						user)));
		log.info("알람 저장");

	}

	@EventListener
	public void handleRecordCreateEvent(RecordCreateEvent recordCreateEvent) {
		List<String> loginUserTokenList = getLoginUserFcmToken(
				recordCreateEvent.getUserList());

		//receiver가 로그아웃했으면 알림 보내기 X
		if (loginUserTokenList.size() == 0) {
			return;
		}

		//로그인한 유저에게 알림 보내기
		String message = NotificationType.RECORD_CREATE.generateNotificationMessage(
				recordCreateEvent.getTitle(), recordCreateEvent.getUserName());
		NotificationMultiUserRequest notificationMultiUserRequest = NotificationMultiUserRequest.toRequest(
				loginUserTokenList, NotificationType.RECORD_CREATE, message);

		notificationService.sendByUserTokenList(notificationMultiUserRequest);

		log.info("알람 전송");

		//알림 기록 db에 저장
		Notification savedNotification = notificationRepository.saveAndFlush(
				Notification.from(NotificationType.RECORD_CREATE.getMessageTitle(),
						message, NotificationType.RECORD_CREATE, false));
		//알람받을 유저 저장
		recordCreateEvent.getUserList().forEach(
				user -> notificationUserRepository.save(NotificationUser.from(savedNotification,
						user)));
		log.info("알람 저장");

	}

	@EventListener
	public void handleGroupInviteEvent(GroupInviteEvent groupInviteEvent)
			throws ExecutionException, InterruptedException {
		String loginUserToken = (String) redisTemplate.opsForValue()
				.get("fcm:" + groupInviteEvent.getReceiver().getUsername());

		//receiver가 로그아웃했으면 알림 보내기 X
		if (ObjectUtils.isEmpty(loginUserToken)) {
			return;
		}

		String message = NotificationType.GROUP_INVITE.generateNotificationMessage(
				groupInviteEvent.getTitle(), groupInviteEvent.getSenderName());

		NotificationOneUserRequest notificationOneUserRequest = NotificationOneUserRequest.toRequest(
				loginUserToken, NotificationType.GROUP_INVITE, message);

		notificationService.sendOneUser(notificationOneUserRequest);

		log.info("알람 전송");

		//알림 기록 db에 저장
		Notification savedNotification = notificationRepository.saveAndFlush(
				Notification.from(NotificationType.GROUP_INVITE.getMessageTitle(),
						message, NotificationType.GROUP_INVITE, false));
		//알람받을 유저 저장
		notificationUserRepository.save(NotificationUser.from(savedNotification,
				groupInviteEvent.getReceiver()));

	}

	//알람 보낼 멤버 목록 중 로그인한 유저의 fcmToken 뽑기
	private List<String> getLoginUserFcmToken(List<User> userList) {
		// 로그인한 유저의 fcmToken 뽑기
		// 로그아웃한 회원들은 redis에 저장 x
		return userList
				.stream()
				.map(User::getUsername)
				.filter(userName -> redisTemplate.hasKey("fcm:" + userName))
				.map(userName -> (String) redisTemplate.opsForValue().get("fcm:" + userName))
				.collect(Collectors.toList());
	}

}
