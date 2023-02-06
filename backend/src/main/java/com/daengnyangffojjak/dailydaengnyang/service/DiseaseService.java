package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.disease.DizGetResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.disease.DizWriteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.disease.DizWriteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Disease;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.exception.DiseaseException;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.repository.DiseaseRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	@Transactional
	public DizWriteResponse modify(Long petId, Long diseaseId, DizWriteRequest dizWriteRequest,
			String username) {
		Pet pet = validator.getPetWithUsername(petId, username);
		Disease disease = validateDiseaseWithPetId(petId, diseaseId);

		disease.modify(dizWriteRequest);
		Disease modified = diseaseRepository.saveAndFlush(disease);
		return DizWriteResponse.from(modified);
	}

	@Transactional
	public MessageResponse delete(Long petId, Long diseaseId, String username) {
		Pet pet = validator.getPetWithUsername(petId, username);
		Disease disease = validateDiseaseWithPetId(petId, diseaseId);
		disease.deleteSoftly();
		return new MessageResponse("질병 기록이 삭제되었습니다.");
	}

	@Transactional
	public DizGetResponse getDisease(Long petId, Long diseaseId, String username) {
		Pet pet = validator.getPetWithUsername(petId, username);
		Disease disease = validateDiseaseWithPetId(petId, diseaseId);
		return DizGetResponse.from(disease);
	}

	@Transactional
	public Page<DizGetResponse> getDiseaseList(Long petId, Pageable pageable, String username) {
		Pet pet = validator.getPetWithUsername(petId, username);
		return diseaseRepository.findAllByPetId(petId, pageable)
				.map(DizGetResponse::from);
	}

	private Disease validateDiseaseWithPetId(Long petId, Long diseaseId) {
		Disease disease = validator.getDiseaseById(diseaseId);
		if (!disease.getPet().getId().equals(petId)) {   //질병의 펫 정보가 PathVariable의 펫 정보와 다르면 에러
			throw new DiseaseException(ErrorCode.INVALID_REQUEST);
		}
		return disease;
	}
}
