package com.daengnyangffojjak.dailydaengnyang.domain.dto.pet;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PetResultResponse {

	private Long id;
	private String name;
	private String age;
	private LocalDateTime createdAt;
	private LocalDateTime lastModifiedAt;

	public static PetResultResponse addFrom(Pet pet) {
		return PetResultResponse.builder()
				.id(pet.getId())
				.name(pet.getName())
				.age(pet.getAge()) // LocalDate -> String으로 뽑음
				.createdAt(pet.getCreatedAt())
				.build();
	}

	public static PetResultResponse updateFrom(Pet pet) {
		return PetResultResponse.builder()
				.id(pet.getId())
				.name(pet.getName())
				.age(pet.getAge()) // LocalDate -> String으로 뽑음
				.lastModifiedAt(pet.getLastModifiedAt())
				.build();
	}
}
