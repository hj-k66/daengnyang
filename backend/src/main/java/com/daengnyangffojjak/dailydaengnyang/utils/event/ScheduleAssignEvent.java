package com.daengnyangffojjak.dailydaengnyang.utils.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScheduleAssignEvent {
	private final String receiverName;
	private final String message;
	private final String senderName;
	private final String scheduleTitle;

}
