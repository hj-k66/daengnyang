package com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ScheduleAssignRequest {

	private String receiverName;
	private String message;

}
