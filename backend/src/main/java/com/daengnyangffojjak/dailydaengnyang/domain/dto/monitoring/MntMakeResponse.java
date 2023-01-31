package com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Monitoring;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MntMakeResponse {

	private Long id;
	private String petName;
	private LocalDate date;

	public static MntMakeResponse from(Monitoring monitoring) {
		return MntMakeResponse.builder()
				.id(monitoring.getId())
				.petName(monitoring.getPet().getName())
				.date(monitoring.getDate())
				.build();
	}
}
