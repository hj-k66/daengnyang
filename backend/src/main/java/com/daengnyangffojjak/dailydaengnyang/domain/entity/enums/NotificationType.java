package com.daengnyangffojjak.dailydaengnyang.domain.entity.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
	SCHEDULE_CREATE("일정등록", (title, userName) ->
			userName + "님이 " + title + " 일정을 등록했습니다."),
	SCHEDULE_COMPLETE("일정완료", ((title, userName) ->
			userName + "님이 " + title + " 일정을 완료했습니다.")),
	RECORD_CREATE("일기등록", ((title, userName) ->
			userName + "님이 " + title + " 일기를 등록했습니다.")),
	GROUP_INVITE("그룹초대", ((groupTitle, userName) ->
			userName + "님이 " + groupTitle + "에 초대했습니다.")),
	SCHEDULE_ASSIGN("일정 부탁하기", ((title, userName) ->
			userName + "님이 " + title + " 일정을 부탁했습니다.")),
	COMMENT_CREATE("댓글 등록", ((title, userName) ->
			userName + "님이 " + title + " 댓글을 달았습니다."));


	private String messageTitle;
	private NotificationMessageGenerator notificationMessageGenerator;

	NotificationType(String messageTitle,
			NotificationMessageGenerator notificationMessageGenerator) {
		this.messageTitle = messageTitle;
		this.notificationMessageGenerator = notificationMessageGenerator;
	}

	public String generateNotificationMessage(String title, String userName) {
		return notificationMessageGenerator.generateNotificationMessage(title, userName);
	}

}
