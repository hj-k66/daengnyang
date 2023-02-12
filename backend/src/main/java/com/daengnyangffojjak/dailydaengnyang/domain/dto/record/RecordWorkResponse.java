package com.daengnyangffojjak.dailydaengnyang.domain.dto.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RecordWorkResponse {

	private String message;
	private Long id;
}
