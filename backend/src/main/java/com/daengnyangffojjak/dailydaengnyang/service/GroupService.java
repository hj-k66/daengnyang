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
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import java.util.List;
import java.util.function.Predicate;
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
	private final Validator validator;

	@Transactional      //그룹 생성
	public GroupMakeResponse create(GroupMakeRequest groupMakeRequest, String username) {
		User user = validator.getUserByUserName(username);     //유저 확인

		Group savedGroup = groupRepository.save(groupMakeRequest.toEntity(user));        //그룹 저장
		UserGroup savedUserGroup = userGroupRepository.save(
				UserGroup.from(user, savedGroup, groupMakeRequest.getRoleInGroup()));   //그룹 멤버로 저장

		return GroupMakeResponse.from(savedGroup);
	}

	@Transactional      //그룹 내 유저 조회
	public GroupUserListResponse getGroupUsers(Long groupId, String username) {
		Group group = validator.getGroupById(groupId);
		List<UserGroup> userGroupList = validator.getUserGroupListByUsername(group, username);
		return GroupUserListResponse.from(userGroupList);
	}

	@Transactional      //그룹 내 반려동물 조회
	public GroupPetListResponse getGroupPets(Long groupId, String username) {
		Group group = validator.getGroupById(groupId);
		List<UserGroup> userGroupList = validator.getUserGroupListByUsername(group,
				username); //user가 그룹 내 유저인지 체크
		List<Pet> pets = petRepository.findAllByGroupId(groupId);
		return GroupPetListResponse.from(pets);
	}

	@Transactional        //그룹에 유저 초대
	public MessageResponse inviteMember(Long groupId, String username,
			GroupInviteRequest groupInviteRequest) {
		Group group = validator.getGroupById(groupId);
		List<UserGroup> userGroupList = validator.getUserGroupListByUsername(group, username);

		User invited = userRepository.findByEmail(
						groupInviteRequest.getEmail())        //email로 유저 조회
				.orElseThrow(() -> new UserException(ErrorCode.EMAIL_NOT_FOUND));

		Predicate<UserGroup> memberExist = userGroup -> invited.getUsername()
				.equals(userGroup.getUser().getUsername());        //이미 유저가 그룹에 존재
		Predicate<UserGroup> roleExist = userGroup -> groupInviteRequest.getRoleInGroup()
				.equals(userGroup.getRoleInGroup());            //이미 role이 그룹에 존재
		Predicate<UserGroup> combinedCondition = memberExist.or(roleExist);
		if (userGroupList.stream().anyMatch(combinedCondition)) {
			throw new GroupException(ErrorCode.INVALID_REQUEST, "이미 존재하는 회원 또는 역할입니다.");
		}
		UserGroup savedUserGroup = userGroupRepository.save(    //그룹에 추가
				UserGroup.from(invited, group, groupInviteRequest.getRoleInGroup()));   //그룹 멤버로 저장
		return new MessageResponse(invited.getUsername() + "이(가) 그룹에 등록되었습니다.");
	}

	@Transactional
	public MessageResponse leaveGroup(Long groupId, String username) {
		Group group = validator.getGroupById(groupId);
		List<UserGroup> userGroupList = validator.getUserGroupListByUsername(group, username);
		UserGroup loginUserGroup = userGroupList.stream()
				.filter(userGroup -> username.equals(userGroup.getUser().getUsername()))
				.findFirst().orElseThrow(() -> new UserException(ErrorCode.INVALID_PERMISSION));

		if (!username.equals(group.getUser().getUsername())) {        //그룹장이 아닌 경우 탈퇴
			loginUserGroup.deleteSoftly();
			return new MessageResponse("그룹에서 나왔습니다.");
		}

		//그룹장일 경우 1) 다른 그룹원이 있거나 2) 반려동물이 있으면 못 나감
		if (userGroupList.size() != 1) {                                  //1) 그룹원이 있는 경우
			throw new GroupException(ErrorCode.INVALID_REQUEST, "그룹장은 그룹을 나갈 수 없습니다.");
		}
		if (petRepository.findAllByGroupId(groupId).size() != 0) {        //2) 반려동물이 있는 경우
			throw new GroupException(ErrorCode.INVALID_REQUEST, "그룹에 반려동물이 존재하여 나갈 수 없습니다.");
		}
		//아무도 없으면 그룹까지 삭제
		loginUserGroup.deleteSoftly();
		group.deleteSoftly();
		return new MessageResponse("그룹이 삭제되었습니다.");
	}

	@Transactional
	public MessageResponse deleteMember(Long groupId, String username, Long userId) {
		Group group = validator.getGroupById(groupId);
		User user = validator.getUserByUserName(username);    //로그인한 유저 확인
		List<UserGroup> userGroupList = userGroupRepository.findAllByGroup(group);
		if (!user.getId().equals(group.getUser().getId())) {    //그룹장이 아니면 예외발생
			throw new UserException(ErrorCode.INVALID_PERMISSION);
		}
		if (user.getId().equals(userId)) {        //그룹장을 내보내는 경우 예외발생
			throw new GroupException(ErrorCode.INVALID_REQUEST, "그룹장을 내보낼 수 없습니다.");
		}
		User userToDelete = validator.getUserById(userId);  //퇴출할 유저 확인
		UserGroup memberToDelete = userGroupList.stream()
				.filter(userGroup -> userToDelete.getId().equals(userGroup.getUser().getId()))
				.findFirst()
				.orElseThrow(() -> new GroupException(
						ErrorCode.GROUP_USER_NOT_FOUND)); //퇴출할 유저가 그룹 멤버가 아니면 예외발생

		memberToDelete.deleteSoftly();
		return new MessageResponse("그룹에서 내보내기를 성공하였습니다.");
	}
}
