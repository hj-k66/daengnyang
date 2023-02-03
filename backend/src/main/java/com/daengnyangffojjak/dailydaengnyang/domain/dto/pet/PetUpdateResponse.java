package com.daengnyangffojjak.dailydaengnyang.domain.dto.pet;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PetUpdateResponse {

	private Long id;
	private String name;
	private String age;
	private LocalDateTime lastModifiedAt;

	public static PetUpdateResponse updateFrom(Pet pet) {
		return PetUpdateResponse.builder()
				.id(pet.getId())
				.name(pet.getName())
				.age(pet.getAge()) // LocalDate -> String으로 뽑음
				.lastModifiedAt(pet.getLastModifiedAt())
				.build();
	}
}
