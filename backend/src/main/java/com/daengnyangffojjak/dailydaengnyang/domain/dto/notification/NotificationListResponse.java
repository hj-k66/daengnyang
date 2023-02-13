package com.daengnyangffojjak.dailydaengnyang.domain.dto.notification;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Notification;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationListResponse {
	private List<NotificationResponse> notifications;

	public static NotificationListResponse from(List<Notification> notifications) {
		List<NotificationResponse>  notificationResponses = notifications.stream().map(NotificationResponse::from)
				.toList();
		return new NotificationListResponse(notificationResponses);
	}

}
