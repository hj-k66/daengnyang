package com.daengnyangffojjak.dailydaengnyang.domain.dto;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.NotificationType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NotificationRequest {

	private List<String> userTokenList;
	private String title;
	private String body;

	public static NotificationRequest toRequest(List<String> userTokenList,
			NotificationType notificationType,
			String body) {
		return new NotificationRequest(userTokenList, notificationType.getMessageTitle(),body);
	}


}
