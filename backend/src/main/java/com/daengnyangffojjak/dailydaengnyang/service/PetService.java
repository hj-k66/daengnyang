package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetShowResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetUpdateResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.UserGroup;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.GroupException;
import com.daengnyangffojjak.dailydaengnyang.exception.PetException;
import com.daengnyangffojjak.dailydaengnyang.exception.UserException;
import com.daengnyangffojjak.dailydaengnyang.repository.GroupRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.PetRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.UserGroupRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PetService {

	private final PetRepository petRepository;
	private final UserRepository userRepository;
	private final GroupRepository groupRepository;
	private final UserGroupRepository userGroupRepository;

	// pet 등록
	@Transactional
	public PetAddResponse add(Long groupId, PetAddRequest petAddRequest, String userName) {

		// 해당 id의 그룹이 존재하는가
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new PetException(ErrorCode.GROUP_NOT_FOUND));

		// 사용자가 그룹에 속해있는 멤버인가
		List<UserGroup> userGroupList = getUserGroupListWithUsername(groupId,
				userName);

		Pet savedPet = petRepository.save(petAddRequest.toEntity(group));

		return PetAddResponse.addFrom(savedPet);
	}

	// pet 조회
	@Transactional(readOnly = true)
	public Page<PetShowResponse> showAll(Long groupId, Pageable pageable) {

		// 해당 id의 그룹이 존재하는가
		groupRepository.findById(groupId)
				.orElseThrow(() -> new PetException(ErrorCode.GROUP_NOT_FOUND));

		return petRepository.findAllByGroupId(groupId, pageable).map(PetShowResponse::showFrom);
	}

	// pet 단건 조회
	public PetShowResponse show(Long groupId, Long id) {

		// 해당 id의 그룹이 존재하는가
		groupRepository.findById(groupId)
				.orElseThrow(() -> new PetException(ErrorCode.GROUP_NOT_FOUND));

		// 해당 id의 펫이 존재하는가
		Pet pet = petRepository.findById(id)
				.orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));

		return PetShowResponse.showFrom(pet);
	}

	// pet 수정
	@Transactional
	public PetUpdateResponse modify(Long groupId, Long id, PetAddRequest petAddRequest,
			String userName) {

		// 해당 id의 펫이 존재하는가
		Pet pet = petRepository.findById(id)
				.orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));

		// 해당 id의 그룹이 존재하는가
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new PetException(ErrorCode.GROUP_NOT_FOUND));

		// pet repository 에 저장된 group id 와 group repository 에 저장된 group id 가 같은가
		if (!pet.getGroup().getId().equals(group.getId())) {
			throw new PetException(ErrorCode.INVALID_PERMISSION);
		}

		// 사용자가 그룹에 속해있는 멤버인가
		List<UserGroup> userGroupList = getUserGroupListWithUsername(groupId,
				userName);

		pet.update(petAddRequest);
		Pet savedPet = petRepository.saveAndFlush(pet);

		return PetUpdateResponse.updateFrom(savedPet);
	}

	// pet 삭제
	@Transactional
	public PetDeleteResponse delete(Long groupId, Long id, String userName) {

		// 해당 id의 펫이 존재하는가
		Pet pet = petRepository.findById(id)
				.orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));

		// 해당 id의 그룹이 존재하는가
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new PetException(ErrorCode.GROUP_NOT_FOUND));

		// pet repository 에 저장된 group id 와 group repository 에 저장된 group id 가 같은가
		if (!pet.getGroup().getId().equals(group.getId())) {
			throw new PetException(ErrorCode.INVALID_PERMISSION);
		}

		// 사용자가 그룹에 속해있는 멤버인가
		List<UserGroup> userGroupList = getUserGroupListWithUsername(groupId,
				userName);

		pet.deleteSoftly();

		return PetDeleteResponse.builder()
				.message("등록이 취소되었습니다.")
				.build();
	}

	// groupService 에 있던 메서드인데
	// 서비스끼리 의존하면 안된다고 했던거 같아서 임시로 가져왔습니다.
	private List<UserGroup> getUserGroupListWithUsername(Long groupId,
			String username) { //유저가 그룹 내 멤버이면 그룹유저리스트 반환
		User user = userRepository.findByUserName(username)
				.orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND));     //유저 확인
		//그룹이 존재하는지 확인
		Group group = getGroupById(groupId);
		//그룹으로 그룹 내 멤버 불러오기
		List<UserGroup> userGroupList = userGroupRepository.findAllByGroup(group);
		//로그인한 유저가 그룹 내 유저인지 확인 -> 그룹 내 유저가 아니면 예외 발생
		if (userGroupList.stream()
				.noneMatch(userGroup -> username.equals(userGroup.getUser().getUsername()))) {
			throw new UserException(ErrorCode.INVALID_PERMISSION);
		}
		return userGroupList;
	}

	// 이거도 같이 가져왔습니다.
	private Group getGroupById(Long groupId) {    //그룹 아이디로 그룹 조회, 없으면 예외 발생
		return groupRepository.findById(groupId)
				.orElseThrow(() -> new GroupException(ErrorCode.GROUP_NOT_FOUND));
	}
}
