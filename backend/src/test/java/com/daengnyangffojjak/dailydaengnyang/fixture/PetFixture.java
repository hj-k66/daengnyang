package com.daengnyangffojjak.dailydaengnyang.fixture;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.BaseEntity;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Sex;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;

public class PetFixture {
	public static Pet get (){
		Pet pet = Pet.builder().id(1L).birthday(LocalDate.of(2018, 3, 1)).species(Species.CAT)
				.name("hoon").group(GroupFixture.get()).sex(Sex.NEUTERED_MALE).build();
		ReflectionTestUtils.setField(
				pet,
				BaseEntity.class,
				"createdAt",
				LocalDateTime.of(2022, 12, 12, 12, 12, 12),
				LocalDateTime.class
		);
		ReflectionTestUtils.setField(
				pet,
				BaseEntity.class,
				"lastModifiedAt",
				LocalDateTime.of(2022, 12, 12, 12, 12, 12),
				LocalDateTime.class
		);
		return pet;
	}
}
