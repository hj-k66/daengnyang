package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.disease.DizWriteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.disease.DizWriteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Disease;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.repository.DiseaseRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiseaseService {

	private final DiseaseRepository diseaseRepository;
	private final Validator validator;

	@Transactional
	public DizWriteResponse create(Long petId, DizWriteRequest dizWriteRequest, String username) {
		Pet pet = validator.getPetWithUsername(petId, username);
		Disease saved = diseaseRepository.save(dizWriteRequest.toEntity(pet));
		return DizWriteResponse.from(saved);
	}
}
