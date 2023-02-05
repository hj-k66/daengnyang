package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.disease.DizWriteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.DiseaseCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Disease extends BaseEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pet_id")
	private Pet pet;
	@Column(nullable = false)
	private String name;
	@Enumerated(EnumType.STRING)
	private DiseaseCategory category;
	private LocalDate startedAt;
	private LocalDate endedAt;

	public void modify(DizWriteRequest request) {
		this.name = request.getName();
		this.category = request.getCategory();
		this.startedAt = request.getStartedAt();
		this.endedAt = request.getEndedAt();
	}
}
