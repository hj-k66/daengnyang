package com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring;

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
}
