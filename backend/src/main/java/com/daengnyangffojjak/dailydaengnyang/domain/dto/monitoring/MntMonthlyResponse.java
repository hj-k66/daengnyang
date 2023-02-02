package com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Monitoring;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MntMonthlyResponse {
	private List<MntGetResponse> monthlyMonitorings;

	public static MntMonthlyResponse from (List<Monitoring> monitorings) {
		List<MntGetResponse> monthlyMonitorings = monitorings.stream()
				.map(MntGetResponse::from).toList();
		return MntMonthlyResponse.builder()
				.monthlyMonitorings(monthlyMonitorings)
				.build();
	}
}
