package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetShowResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetUpdateResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.PetException;
import com.daengnyangffojjak.dailydaengnyang.repository.PetRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PetService {

	private final PetRepository petRepository;
	private final Validator validator;

	// pet 등록
	@Transactional
	public PetAddResponse add(Long groupId, PetAddRequest petAddRequest, String userName) {

		// 해당 id의 그룹이 존재하는가
		Group group = validator.getGroupById(groupId);

		// 사용자가 그룹에 속해있는 멤버인가
		validator.getUserGroupListByUsername(group, userName);

		Pet savedPet = petRepository.save(petAddRequest.toEntity(group));

		return PetAddResponse.addFrom(savedPet);
	}


	// pet 단건 조회
	@Transactional(readOnly = true)
	public PetShowResponse show(Long groupId, Long id, String userName) {

		// 유저 확인
		validator.getUserByUserName(userName);

		// 해당 id의 그룹이 존재하는가
		Group group = validator.getGroupById(groupId);

		// 해당 id의 펫이 존재하는가
		Pet pet = validator.getPetById(id);

		// pet repository 에 저장된 group id 와 group repository 에 저장된 group id 가 같은가
		if (!pet.getGroup().getId().equals(group.getId())) {
			throw new PetException(ErrorCode.INVALID_PERMISSION);
		}

		return PetShowResponse.showFrom(pet);
	}

	// pet 수정
	@Transactional
	public PetUpdateResponse modify(Long groupId, Long id, PetAddRequest petAddRequest,
			String userName) {

		// 사용자가 그룹에 속해있는 멤버이고 해당 id의 펫이 존재하는가
		Pet pet = validator.getPetWithUsername(id, userName);

		// 해당 id의 그룹이 존재하는가
		Group group = validator.getGroupById(groupId);

		// pet repository 에 저장된 group id 와 group repository 에 저장된 group id 가 같은가
		if (!pet.getGroup().getId().equals(group.getId())) {
			throw new PetException(ErrorCode.INVALID_PERMISSION);
		}

		pet.update(petAddRequest);
		Pet savedPet = petRepository.saveAndFlush(pet);

		return PetUpdateResponse.updateFrom(savedPet);
	}

	// pet 삭제
	@Transactional
	public PetDeleteResponse delete(Long groupId, Long id, String userName) {

		// 사용자가 그룹에 속해있는 멤버이고 해당 id의 펫이 존재하는가
		Pet pet = validator.getPetWithUsername(id, userName);

		// 해당 id의 그룹이 존재하는가
		Group group = validator.getGroupById(groupId);

		// pet repository 에 저장된 group id 와 group repository 에 저장된 group id 가 같은가
		if (!pet.getGroup().getId().equals(group.getId())) {
			throw new PetException(ErrorCode.INVALID_PERMISSION);
		}

		pet.deleteSoftly();

		return PetDeleteResponse.builder()
				.message("등록이 취소되었습니다.")
				.build();
	}

}
