package com.daengnyangffojjak.dailydaengnyang.domain.dto.pet;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Sex;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class PetAddRequest {

	private String name;
	private Species species;
	private String breed;
	private Sex sex;
	@PastOrPresent(message = "현재이거나 현재 보다 이전으로 입력해주세요.")
	private LocalDate birthday;
	// 1)db에 2022-01-01 로 찍힘, 2)현재이후의 시간을 입력하면 에러메세지출력, 3)어노테이션 해당 dto에 사용, 4)해당 컨트롤러에 @valid 사용
	private double weight;


	public Pet toEntity(Group group) {
		return Pet.builder()
				.name(this.name)
				.species(this.species)
				.breed(this.breed)
				.sex(this.sex)
				.birthday(this.birthday)
				.weight(this.weight)
				.group(group)
				.build();
	}
}
