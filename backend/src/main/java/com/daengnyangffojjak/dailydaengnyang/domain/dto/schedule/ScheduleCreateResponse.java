package com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Schedule;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ScheduleCreateResponse {

	private String message;
	private Long id;

	public static ScheduleCreateResponse toResponse(String message, Schedule savedSchedule) {
		return ScheduleCreateResponse.builder()
				.message(message)
				.id(savedSchedule.getId())
				.build();
	}
}
