package com.daengnyangffojjak.dailydaengnyang.fixture;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.BaseEntity;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Notification;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.NotificationType;
import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;

public class NotificationFixture {

	public static Notification get() {
		Notification notification = Notification.builder().id(1L).title("제목").body("내용")
				.notificationType(NotificationType.SCHEDULE_CREATE)
				.checked(false).build();

		ReflectionTestUtils.setField(
				notification,
				BaseEntity.class,
				"createdAt",
				LocalDateTime.of(2022, 1, 11, 11, 11, 11),
				LocalDateTime.class
		);
		ReflectionTestUtils.setField(
				notification,
				BaseEntity.class,
				"lastModifiedAt",
				LocalDateTime.of(2022, 1, 11, 11, 11, 11),
				LocalDateTime.class
		);
		return notification;
	}

}
