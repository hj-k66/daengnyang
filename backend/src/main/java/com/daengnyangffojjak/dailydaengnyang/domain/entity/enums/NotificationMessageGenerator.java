package com.daengnyangffojjak.dailydaengnyang.domain.entity.enums;

@FunctionalInterface
public interface NotificationMessageGenerator {
	String generateNotificationMessage(String title, String userName);

}
