package com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleAssignRequest {

	private String receiverName;
	private String message;

}
