package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Sex;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@EqualsAndHashCode(callSuper = false)
@Where(clause = "deleted_at is NULL")
public class Pet extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private Group group;
	@Enumerated(EnumType.STRING)
	private Species species;
	private String breed;       //추후 enum으로 수정 예정
	private String name;
	@Enumerated(EnumType.STRING)
	private Sex sex;
	private LocalDate birthday;
	private double weight;

	public String getAge() {     //1년령 이상은 나이, 1년령 이하는 개월수로 반환
		LocalDate today = LocalDate.now();
		LocalDate birthday = this.birthday;

		long months = ChronoUnit.MONTHS.between(birthday, today); //태어난지 12개월 넘었는 지

		if (months < 12) {
			return months + "개월";
		}
		return ChronoUnit.YEARS.between(birthday, today) + "살";
	}

	public void update(PetAddRequest petAddRequest) {
		this.name = petAddRequest.getName();
		this.species = petAddRequest.getSpecies();
		this.breed = petAddRequest.getBreed();
		this.sex = petAddRequest.getSex();
		this.birthday = petAddRequest.getBirthday();
		this.weight = petAddRequest.getWeight();
	}
}
