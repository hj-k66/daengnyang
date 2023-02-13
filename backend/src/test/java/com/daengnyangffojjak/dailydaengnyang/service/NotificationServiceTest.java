package com.daengnyangffojjak.dailydaengnyang.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.notification.NotificationDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Notification;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.NotificationUser;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.fixture.NotificationFixture;
import com.daengnyangffojjak.dailydaengnyang.fixture.UserFixture;
import com.daengnyangffojjak.dailydaengnyang.repository.NotificationRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.NotificationUserRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;

public class NotificationServiceTest {

	private final RedisTemplate redisTemplate = mock(RedisTemplate.class);
	private final Validator validator = mock(Validator.class);
	private final NotificationUserRepository notificationUserRepository = mock(
			NotificationUserRepository.class);
	private final NotificationRepository notificationRepository = mock(
			NotificationRepository.class);
	User user = UserFixture.get();
	Notification notification = NotificationFixture.get();



	private NotificationService notificationService
			= new NotificationService(redisTemplate, validator, notificationUserRepository,
			notificationRepository);

	@Nested
	@DisplayName("알람 삭제")
	class DeleteNotification {


		@Test
		@DisplayName("성공")
		void success() {
			NotificationUser notificationUser = new NotificationUser(1L,notification,user);

			given(validator.getUserByUserName("user")).willReturn(user);
			given(notificationRepository.findById(1L)).willReturn(Optional.of(notification));
			given(notificationUserRepository.findByNotificationIdAndUserId(1L,user.getId())).willReturn(Optional.of(notificationUser));

			NotificationDeleteResponse response = assertDoesNotThrow(
					() -> notificationService.delete(1L, "user"));
			assertEquals(1L, response.getId());
			assertEquals("알람이 삭제되었습니다.", response.getMessage());
		}
	}

}
