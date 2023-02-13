package com.daengnyangffojjak.dailydaengnyang.domain.dto.notification;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Notification;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class NotificationResponse {

	private Long id;
	private String title;
	private String body;
	private NotificationType notificationType;
	private boolean checked;

	public static NotificationResponse from(Notification notification) {
		return NotificationResponse.builder()
				.id(notification.getId())
				.title(notification.getTitle())
				.body(notification.getBody())
				.notificationType(notification.getNotificationType())
				.checked(notification.isChecked())
				.build();
	}

}
