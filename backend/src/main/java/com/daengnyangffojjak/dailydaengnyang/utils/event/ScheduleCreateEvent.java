package com.daengnyangffojjak.dailydaengnyang.utils.event;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScheduleCreateEvent {
	private final List<String> userNameList;
	private final String title;
	private final String userName;

}
