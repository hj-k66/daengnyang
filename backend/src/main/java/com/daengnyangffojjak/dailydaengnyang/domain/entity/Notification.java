package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification extends BaseEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private String body;
	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;
	private boolean checked; //알림 확인 여부
	@OneToMany(mappedBy = "notification")
	private List<NotificationUser> notificationUserList = new ArrayList<NotificationUser>();


	public static Notification from(String title, String body, NotificationType notificationType, boolean checked){
		return Notification.builder()
				.title(title)
				.body(body)
				.notificationType(notificationType)
				.checked(checked)
				.build();
	}

}
