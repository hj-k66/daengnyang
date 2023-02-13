package com.daengnyangffojjak.dailydaengnyang.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupInviteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupPetListResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupUserListResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserRole;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.UserGroup;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.GroupException;
import com.daengnyangffojjak.dailydaengnyang.exception.UserException;
import com.daengnyangffojjak.dailydaengnyang.fixture.GroupFixture;
import com.daengnyangffojjak.dailydaengnyang.fixture.UserFixture;
import com.daengnyangffojjak.dailydaengnyang.repository.GroupRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.PetRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.UserGroupRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.UserRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

class GroupServiceTest {

	private final GroupRepository groupRepository = mock(GroupRepository.class);
	private final UserGroupRepository userGroupRepository = mock(UserGroupRepository.class);
	private final UserRepository userRepository = mock(UserRepository.class);
	private final PetRepository petRepository = mock(PetRepository.class);
	private final Validator validator = mock(Validator.class);
	private final ApplicationEventPublisher applicationEventPublisher = mock(
			ApplicationEventPublisher.class);

	User user = UserFixture.get();
	Group group = GroupFixture.get();
	UserGroup userGroup = UserGroup.builder().id(1L).user(user).group(group).roleInGroup("엄마")
			.build();
	List<UserGroup> userGroupList = List.of(
			new UserGroup(1L, User.builder().userName("user").build(), group, "mom"),
			new UserGroup(2L, User.builder().userName("user1").build(), group, "dad"));
	private GroupService groupService;

	@BeforeEach
	void setUp() {
		groupService = new GroupService(groupRepository, userGroupRepository, userRepository,
				petRepository, validator, applicationEventPublisher);
	}

	@Nested
	@DisplayName("그룹 만들기")
	class CreateGroup {

		GroupMakeRequest request = new GroupMakeRequest("그룹이름", "엄마");

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(groupRepository.save(request.toEntity(user))).willReturn(group);
			given(userGroupRepository.save(
					UserGroup.from(user, group, request.getRoleInGroup()))).willReturn(
					userGroup);

			GroupMakeResponse response = assertDoesNotThrow(
					() -> groupService.create(request, "user"));

			assertEquals(1L, response.getId());
			assertEquals("그룹이름", response.getName());
			assertEquals(1L, response.getOwnerId());
			assertEquals("user", response.getOwnerUserName());
		}
	}

	@Nested
	@DisplayName("그룹 내 유저 조회")
	class GetGroupUser {

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(userGroupList);

			GroupUserListResponse response = assertDoesNotThrow(
					() -> groupService.getGroupUsers(1L, "user"));

			assertEquals(2, response.getCount());
			assertEquals(2, response.getUsers().size());
		}

		@Test
		@DisplayName("그룹 내 유저가 아닌 경우")
		void fail_그룹내유저아님() {
			List<UserGroup> userNOTGroupList = List.of(
					new UserGroup(1L, User.builder().userName("user2").build(), group, "mom"),
					new UserGroup(1L, User.builder().userName("user1").build(), group, "dad"));
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserGroupListByUsername(group, "user")).willThrow(
					new UserException(ErrorCode.INVALID_PERMISSION));

			UserException e = assertThrows(UserException.class,
					() -> groupService.getGroupUsers(1L, "user"));

			assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
		}
	}

	@Nested
	@DisplayName("그룹 내 반려동물 조회")
	class GetGroupPets {

		@Test
		@DisplayName("성공")
		void success() {
			List<Pet> pets = List.of(Pet.builder().id(1L).name("hoon").species(Species.CAT)
							.birthday(LocalDate.of(2018, 3, 1)).build(),
					Pet.builder().id(2L).name("hoon2").species(Species.CAT)
							.birthday(LocalDate.of(2022, 3, 1)).build(),
					Pet.builder().id(3L).name("hoon3").species(Species.CAT)
							.birthday(LocalDate.of(2023, 1, 1)).build());
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getGroupById(1L)).willReturn(group);
			given(petRepository.findAllByGroupId(group.getId())).willReturn(pets);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(userGroupList);

			GroupPetListResponse response = assertDoesNotThrow(
					() -> groupService.getGroupPets(1L, "user"));

			assertEquals(3, response.getCount());
			assertEquals(3, response.getPets().size());
		}
	}

	@Nested
	@DisplayName("유저의 그룹 리스트 조회")
	class GetGroupList {

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(userGroupList);

			GroupUserListResponse response = assertDoesNotThrow(
					() -> groupService.getGroupUsers(1L, "user"));

			assertEquals(2, response.getCount());
			assertEquals(2, response.getUsers().size());
		}
	}

	@Nested
	@DisplayName("그룹 유저 초대")
	class GroupInviteMember {

		@Test
		@DisplayName("성공")
		void success() {
			GroupInviteRequest request = new GroupInviteRequest("gmail@gmail.com", "teacher");
			User invited = User.builder().id(3L).userName("초대받음").password("password")
					.email(request.getEmail()).role(UserRole.ROLE_USER).build();
			UserGroup invitedMem = UserGroup.builder().id(3L).user(invited).group(group)
					.roleInGroup("dad").build();

			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(userGroupList);
			given(userRepository.findByEmail("gmail@gmail.com")).willReturn(Optional.of(invited));
			given(userGroupRepository.save(
					UserGroup.from(invited, group, request.getRoleInGroup()))).willReturn(
					invitedMem);

			MessageResponse response = assertDoesNotThrow(
					() -> groupService.inviteMember(1L, "user", request));

			assertEquals(invited.getUsername() + "이(가) 그룹에 등록되었습니다.", response.getMsg());
		}

		@Test
		@DisplayName("이미 그룹 멤버인 경우")
		void fail_이미그룹멤버() {
			GroupInviteRequest request = new GroupInviteRequest("gmail@gmail.com", "아빠");
			User invited = User.builder().id(3L).userName("초대받음").password("password")
					.email(request.getEmail()).role(UserRole.ROLE_USER).build();
			List<UserGroup> userGroupList = List.of(
					new UserGroup(1L, User.builder().userName("user").build(), group, "mom"),
					new UserGroup(2L, User.builder().userName("초대받음").build(), group, "dad"));

			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(userGroupList);
			given(userRepository.findByEmail("gmail@gmail.com")).willReturn(Optional.of(invited));

			GroupException e = assertThrows(GroupException.class,
					() -> groupService.inviteMember(1L, "user", request));

			assertEquals(ErrorCode.INVALID_REQUEST, e.getErrorCode());
		}

		@Test
		@DisplayName("이미 그룹에 존재하는 역할 경우")
		void fail_이미역할존재() {
			GroupInviteRequest request = new GroupInviteRequest("gmail@gmail.com", "dad");
			User invited = User.builder().id(3L).userName("초대받음").password("password")
					.email(request.getEmail()).role(UserRole.ROLE_USER).build();
			List<UserGroup> userGroupList = List.of(
					new UserGroup(1L, User.builder().userName("user").build(), group, "mom"),
					new UserGroup(2L, User.builder().userName("user2").build(), group, "dad"));

			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(userGroupList);
			given(userRepository.findByEmail("gmail@gmail.com")).willReturn(Optional.of(invited));

			GroupException e = assertThrows(GroupException.class,
					() -> groupService.inviteMember(1L, "user", request));

			assertEquals(ErrorCode.INVALID_REQUEST, e.getErrorCode());
		}
	}

	@Nested
	@DisplayName("그룹에서 나오기")
	class LeaveGroup {

		@Test
		@DisplayName("성공 - 그룹장이 아닌 경우")
		void success_not_owner() {
			User user1 = User.builder().userName("user1").build();
			Group group = Group.builder().id(1L).name("그룹이름").user(user1).build();
			List<UserGroup> userGroupList = List.of(
					new UserGroup(1L, User.builder().userName("user").build(), group, "mom"),
					new UserGroup(2L, user1, group, "dad"));

			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(userGroupList);

			MessageResponse response = assertDoesNotThrow(
					() -> groupService.leaveGroup(1L, "user"));

			assertEquals("그룹에서 나왔습니다.", response.getMsg());
		}

		@Test
		@DisplayName("성공 - 그룹장인 경우")
		void success_owner() {
			List<UserGroup> userGroupList = List.of(
					new UserGroup(1L, User.builder().userName("user").build(), group, "mom"));
			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(userGroupList);

			MessageResponse response = assertDoesNotThrow(
					() -> groupService.leaveGroup(1L, "user"));

			assertEquals("그룹이 삭제되었습니다.", response.getMsg());
		}

		@Test
		@DisplayName("그룹장인데 그룹원이 있는 경우")
		void fail_그룹원이있는경우() {
			List<UserGroup> userGroupList = List.of(
					new UserGroup(1L, User.builder().userName("user").build(), group, "mom"),
					new UserGroup(2L, User.builder().userName("user1").build(), group, "dad"));
			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(userGroupList);

			GroupException e = assertThrows(GroupException.class,
					() -> groupService.leaveGroup(1L, "user"));

			assertEquals(ErrorCode.INVALID_REQUEST, e.getErrorCode());
			assertEquals("그룹장은 그룹을 나갈 수 없습니다.", e.getMessage());
		}

		@Test
		@DisplayName("그룹장인데 반려동물이 있는 경우")
		void fail_그룹반려동물이이있는경우() {
			List<UserGroup> userGroupList = List.of(
					new UserGroup(1L, User.builder().userName("user").build(), group, "mom"));
			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(userGroupList);
			given(petRepository.findAllByGroupId(1L)).willReturn(List.of(new Pet()));

			GroupException e = assertThrows(GroupException.class,
					() -> groupService.leaveGroup(1L, "user"));

			assertEquals(ErrorCode.INVALID_REQUEST, e.getErrorCode());
			assertEquals("그룹에 반려동물이 존재하여 나갈 수 없습니다.", e.getMessage());
		}

	}

	@Nested
	@DisplayName("그룹에서 내보내기")
	class DeleteGroupMember {

		User userToDelete = User.builder().id(2L).userName("user1").build();

		@Test
		@DisplayName("성공")
		void success() {
			List<UserGroup> userGroupList = List.of(
					new UserGroup(1L, user, group, "mom"),
					new UserGroup(2L, userToDelete, group, "dad"));

			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserByUserName("user")).willReturn(user);
			given(userGroupRepository.findAllByGroup(group)).willReturn(userGroupList);
			given(validator.getUserById(2L)).willReturn(userToDelete);

			MessageResponse response = assertDoesNotThrow(
					() -> groupService.deleteMember(1L, "user", 2L));

			assertEquals("그룹에서 내보내기를 성공하였습니다.", response.getMsg());
		}

		@Test
		@DisplayName("로그인한 유저가 그룹장이 아닌경우")
		void fail_그룹장아님() {
			User owner = User.builder().id(3L).userName("user1").build();
			Group group = Group.builder().id(1L).name("그룹이름").user(owner).build();
			List<UserGroup> userGroupList = List.of(
					new UserGroup(1L, user, group, "mom"),
					new UserGroup(2L, userToDelete, group, "dad"),
					new UserGroup(3L, owner, group, "dad"));

			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(userGroupList);
			given(validator.getUserById(2L)).willReturn(userToDelete);

			UserException e = assertThrows(UserException.class,
					() -> groupService.deleteMember(1L, "user", 2L));

			assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
		}

		@Test
		@DisplayName("그룹장을 내보내는 경우 예외발생")
		void fail_그룹장을_내보내는_경우() {
			List<UserGroup> userGroupList = List.of(
					new UserGroup(1L, user, group, "mom"),
					new UserGroup(2L, userToDelete, group, "dad"));

			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(userGroupList);
			given(validator.getUserById(2L)).willReturn(userToDelete);

			GroupException e = assertThrows(GroupException.class,
					() -> groupService.deleteMember(1L, "user", 1L));

			assertEquals(ErrorCode.INVALID_REQUEST, e.getErrorCode());
		}

		@Test
		@DisplayName("유저가 없는 경우")
		void fail_내보내는유저가_그룹에없음() {
			User notUserToDelete = User.builder().id(3L).userName("user2").build();

			List<UserGroup> userGroupList = List.of(
					new UserGroup(1L, user, group, "mom"),
					new UserGroup(2L, notUserToDelete, group, "dad"));

			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(userGroupList);
			given(validator.getUserById(2L)).willReturn(userToDelete);

			GroupException e = assertThrows(GroupException.class,
					() -> groupService.deleteMember(1L, "user", 2L));

			assertEquals(ErrorCode.GROUP_USER_NOT_FOUND, e.getErrorCode());
		}
	}
}