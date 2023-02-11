package com.daengnyangffojjak.dailydaengnyang.domain.dto.notification;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.NotificationType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NotificationMultiUserRequest {

	private List<String> userTokenList;
	private String title;
	private String body;

	public static NotificationMultiUserRequest toRequest(List<String> userTokenList,
			NotificationType notificationType,
			String body) {
		return new NotificationMultiUserRequest(userTokenList, notificationType.getMessageTitle(),body);
	}


}
