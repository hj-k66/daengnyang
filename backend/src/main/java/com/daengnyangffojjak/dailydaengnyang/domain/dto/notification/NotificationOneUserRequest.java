package com.daengnyangffojjak.dailydaengnyang.domain.dto.notification;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NotificationOneUserRequest {
	private String userToken;
	private String title;
	private String body;

	public static NotificationOneUserRequest toRequest(String userToken,
			NotificationType notificationType,
			String body) {
		return new NotificationOneUserRequest(userToken, notificationType.getMessageTitle(),body);
	}
}
