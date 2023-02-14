package com.daengnyangffojjak.dailydaengnyang.domain.dto.disease;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Disease;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.DiseaseCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DizWriteRequest {

	@NotBlank(message = "질병명을 입력해주세요.")
	private String name;
	private DiseaseCategory category;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	@PastOrPresent(message = "시작 날짜는 과거 또는 현재 날짜여야 합니다.")
	private LocalDate startedAt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate endedAt;

	public Disease toEntity(Pet pet) {
		return Disease.builder()
				.pet(pet)
				.name(this.name)
				.category(this.category)
				.startedAt(this.startedAt)
				.endedAt(this.endedAt)
				.build();
	}
}
