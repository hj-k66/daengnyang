package com.daengnyangffojjak.dailydaengnyang.domain.dto.group;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Sex;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupPetResponse {

	private Long id;
	private String name;
	private Species species;
	private String age;
	private String breed;
	private Sex sex;

	public static GroupPetResponse from(Pet pet) {
		return GroupPetResponse.builder()
				.id(pet.getId())
				.name(pet.getName())
				.species(pet.getSpecies())
				.age(pet.getAge())
				.breed(pet.getBreed())
				.sex(pet.getSex())
				.build();
	}
}
