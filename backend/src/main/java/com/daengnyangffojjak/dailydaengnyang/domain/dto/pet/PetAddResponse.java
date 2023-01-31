package com.daengnyangffojjak.dailydaengnyang.domain.dto.pet;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class PetAddResponse {

	private Long id;
	private String name;
	private String age;
	private LocalDateTime createdAt;

	public static PetAddResponse addFrom(Pet pet) {
		return PetAddResponse.builder()
				.id(pet.getId())
				.name(pet.getName())
				.age(pet.getAge()) // LocalDate -> String으로 뽑음
				.createdAt(pet.getCreatedAt())
				.build();
	}
}
