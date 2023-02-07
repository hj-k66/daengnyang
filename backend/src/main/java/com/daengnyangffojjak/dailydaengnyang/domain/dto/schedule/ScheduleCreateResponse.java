package com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Schedule;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ScheduleCreateResponse {

	private String msg;
	private Long id;

	public static ScheduleCreateResponse toResponse(String message, Schedule savedSchedule) {
		return ScheduleCreateResponse.builder()
				.msg(message)
				.id(savedSchedule.getId())
				.build();
	}
}
