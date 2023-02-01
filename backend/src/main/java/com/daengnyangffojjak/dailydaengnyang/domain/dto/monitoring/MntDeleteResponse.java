package com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Monitoring;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MntDeleteResponse {
	private String message;
	private Long id;

	public static MntDeleteResponse from(Monitoring monitoring) {
		return MntDeleteResponse.builder()
					.message("모니터링 삭제 완료")
					.id(monitoring.getId()).build();
	}
}
