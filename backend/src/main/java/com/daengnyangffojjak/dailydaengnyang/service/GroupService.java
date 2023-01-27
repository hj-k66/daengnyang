package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupInviteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupPetListResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupUserListResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.UserGroup;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.GroupException;
import com.daengnyangffojjak.dailydaengnyang.exception.UserException;
import com.daengnyangffojjak.dailydaengnyang.repository.GroupRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.PetRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.UserGroupRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupService {

	private final GroupRepository groupRepository;
	private final UserGroupRepository userGroupRepository;
	private final UserRepository userRepository;
	private final PetRepository petRepository;

	@Transactional      //그룹 생성
	public GroupMakeResponse create(GroupMakeRequest groupMakeRequest, String username) {
		User user = userRepository.findByUserName(username)
				.orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND));     //유저 확인

		Group savedGroup = groupRepository.save(groupMakeRequest.toEntity(user));        //그룹 저장
		UserGroup savedUserGroup = userGroupRepository.save(
				UserGroup.from(user, savedGroup, groupMakeRequest.getRoleInGroup(),
						true));   //그룹 멤버로 저장

		return GroupMakeResponse.from(savedGroup);
	}

	@Transactional      //그룹 내 유저 조회
	public GroupUserListResponse getGroupUsers(Long groupId, String username) {
		Group group = getGroupById(groupId);
		List<UserGroup> userGroupList = getUserGroupListWithUsername(group, username);
		return GroupUserListResponse.from(userGroupList);
	}

	@Transactional      //그룹 내 반려동물 조회
	public GroupPetListResponse getGroupPets(Long groupId, String username) {
		Group group = getGroupById(groupId);
		List<UserGroup> userGroupList = getUserGroupListWithUsername(group,
				username);    //user가 그룹 내 유저인지 체크
		List<Pet> pets = petRepository.findAllByGroupId(groupId);
		return GroupPetListResponse.from(pets);
	}

	private Group getGroupById(Long groupId) {    //그룹 아이디로 그룹 조회, 없으면 예외 발생
		return groupRepository.findById(groupId)
				.orElseThrow(() -> new GroupException(ErrorCode.GROUP_NOT_FOUND));
	}

	private List<UserGroup> getUserGroupListWithUsername(Group group,
			String username) { //유저가 그룹 내 멤버이면 그룹유저리스트 반환
		User user = userRepository.findByUserName(username)
				.orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND));     //유저 확인
		//그룹으로 그룹 내 멤버 불러오기
		List<UserGroup> userGroupList = userGroupRepository.findAllByGroup(group);
		//로그인한 유저가 그룹 내 유저인지 확인 -> 그룹 내 유저가 아니면 예외 발생
		if (userGroupList.stream()
				.noneMatch(userGroup -> username.equals(userGroup.getUser().getUsername()))) {
			throw new UserException(ErrorCode.INVALID_PERMISSION);
		}
		return userGroupList;
	}

	public MessageResponse inviteMember(Long groupId, String username,
			GroupInviteRequest groupInviteRequest) {
		Group group = getGroupById(groupId);
		List<UserGroup> userGroupList = getUserGroupListWithUsername(group, username);

		User invited = userRepository.findByEmail(
						groupInviteRequest.getEmail())        //email로 유저 조회
				.orElseThrow(() -> new UserException(ErrorCode.EMAIL_NOT_FOUND));
		if (userGroupList.stream()                                //이미 존재하는 회원인 경우 예외 발생
				.anyMatch(userGroup -> invited.getUsername()
						.equals(userGroup.getUser().getUsername()))) {
			throw new UserException(ErrorCode.INVALID_REQUEST, "이미 존재하는 회원입니다.");
		}
		//그룹에 추가
		UserGroup savedUserGroup = userGroupRepository.save(
				UserGroup.from(invited, group, groupInviteRequest.getRoleInGroup(),
						false));   //그룹 멤버로 저장
		return new MessageResponse(invited.getUsername() + "이(가) 그룹에 등록되었습니다.");
	}
}
