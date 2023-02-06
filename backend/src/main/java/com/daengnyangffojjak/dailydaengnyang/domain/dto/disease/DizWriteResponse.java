package com.daengnyangffojjak.dailydaengnyang.domain.dto.disease;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Disease;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DizWriteResponse {

	private Long id;
	private String petName;
	private String name;

	public static DizWriteResponse from(Disease saved) {
		return DizWriteResponse.builder()
				.id(saved.getId())
				.petName(saved.getPet().getName())
				.name(saved.getName())
				.build();
	}
}
