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

	public static ScheduleCreateResponse toResponse(Schedule savedSchedule) {
		return ScheduleCreateResponse.builder()
				.message("일정 등록 완료")
				.id(savedSchedule.getId())
				.build();
	}
}
