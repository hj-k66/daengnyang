package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	@JoinColumn(name="notification_id")
	private Notification notification;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public static NotificationUser from(Notification notification, User user){
		return NotificationUser.builder()
				.notification(notification)
				.user(user)
				.build();
	}

}
