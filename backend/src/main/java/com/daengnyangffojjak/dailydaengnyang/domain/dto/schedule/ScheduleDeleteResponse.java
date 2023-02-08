package com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ScheduleDeleteResponse {

	private String msg;

	public static ScheduleDeleteResponse toResponse(String message) {
		return ScheduleDeleteResponse.builder()
				.msg(message)
				.build();
	}
}
