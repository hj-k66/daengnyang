package com.daengnyangffojjak.dailydaengnyang.utils.event;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScheduleAssignEvent {
	private final User receiver;
	private final String message;
	private final String senderName;
	private final String scheduleTitle;

}
