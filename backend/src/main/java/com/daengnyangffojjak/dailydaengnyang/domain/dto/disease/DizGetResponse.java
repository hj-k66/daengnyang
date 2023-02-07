package com.daengnyangffojjak.dailydaengnyang.domain.dto.disease;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Disease;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.DiseaseCategory;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DizGetResponse {
	private Long id;
	private String name;
	private DiseaseCategory category;
	private LocalDate startedAt;
	private LocalDate endedAt;

	public static DizGetResponse from(Disease disease) {
		return DizGetResponse.builder()
				.id(disease.getId())
				.name(disease.getName())
				.category(disease.getCategory())
				.startedAt(disease.getStartedAt())
				.endedAt(disease.getEndedAt())
				.build();
	}
}
