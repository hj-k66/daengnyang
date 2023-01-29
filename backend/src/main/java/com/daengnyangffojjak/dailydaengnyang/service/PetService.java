package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetResultResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetShowResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.PetException;
import com.daengnyangffojjak.dailydaengnyang.repository.GroupRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.PetRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PetService {

	private final PetRepository petRepository;
	private final UserRepository userRepository;
	private final GroupRepository groupRepository;

	// pet 등록
	@Transactional
	public PetResultResponse add(Long groupId, PetAddRequest petAddRequest,
			Authentication authentication) {

		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new PetException(ErrorCode.GROUP_NOT_FOUND));

		User user = userRepository.findByUserName(authentication.getName())
				.orElseThrow(() -> new PetException(ErrorCode.USERNAME_NOT_FOUND));

		// pet 등록할때 같이 user 정보를 저장해야 함
		Pet savedPet = petRepository.save(petAddRequest.toEntity(group, user));

		return PetResultResponse.addFrom(savedPet);
	}

	// pet 조회
	@Transactional(readOnly = true)
	public Page<PetShowResponse> showAll(Long groupId, Pageable pageable) {

		groupRepository.findById(groupId)
				.orElseThrow(() -> new PetException(ErrorCode.GROUP_NOT_FOUND));

		return petRepository.findAllByGroup(groupId, pageable).map(PetShowResponse::from);
	}

	// pet 수정
	public PetResultResponse modify(Long groupId, Long id, PetAddRequest petAddRequest,
			Authentication authentication) {

		Pet pet = petRepository.findById(id)
				.orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));

		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new PetException(ErrorCode.GROUP_NOT_FOUND));

		User user = userRepository.findByUserName(authentication.getName())
				.orElseThrow(() -> new PetException(ErrorCode.USERNAME_NOT_FOUND));

		// 반려동물을 수정할 권한이 있는 user 인지 확인 필요
		if (!Objects.equals(pet.getUser().getId(), user.getId())) {
			throw new PetException(ErrorCode.INVALID_PERMISSION);
		}

		if (!Objects.equals(pet.getGroup().getId(), group.getId())) {
			throw new PetException(ErrorCode.INVALID_PERMISSION);
		}

		pet.update(petAddRequest);
		Pet savedPet = petRepository.save(pet);

		return PetResultResponse.updateFrom(savedPet);
	}

	// pet 삭제
	@Transactional
	public PetDeleteResponse delete(Long groupId, Long id, Authentication authentication) {

		Pet pet = petRepository.findById(id)
				.orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));

		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new PetException(ErrorCode.GROUP_NOT_FOUND));

		User user = userRepository.findByUserName(authentication.getName())
				.orElseThrow(() -> new PetException(ErrorCode.USERNAME_NOT_FOUND));

		if (!Objects.equals(pet.getUser().getId(), user.getId())) {
			throw new PetException(ErrorCode.INVALID_PERMISSION);
		}

		if (!Objects.equals(pet.getGroup().getId(), group.getId())) {
			throw new PetException(ErrorCode.INVALID_PERMISSION);
		}

		petRepository.delete(pet);

		return PetDeleteResponse.builder()
				.message("등록이 취소되었습니다.")
				.build();
	}
}
