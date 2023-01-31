package com.daengnyangffojjak.dailydaengnyang.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetUpdateResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserRole;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.UserGroup;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Sex;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import com.daengnyangffojjak.dailydaengnyang.repository.GroupRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.PetRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.UserGroupRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


class PetServiceTest {

	private PetService petService;
	private final GroupRepository groupRepository = mock(GroupRepository.class);
	private final UserRepository userRepository = mock(UserRepository.class);
	private final PetRepository petRepository = mock(PetRepository.class);
	private final UserGroupRepository userGroupRepository = mock(UserGroupRepository.class);


	@BeforeEach
	public void setUp() {
		petService = new PetService(petRepository, userRepository, groupRepository,
				userGroupRepository);
	}

	User user = User.builder().id(1L).userName("user").password("password").email("@.")
			.role(UserRole.ROLE_USER).build();
	Group group = Group.builder().id(1L).name("user").user(user).build();
	Pet pet = Pet.builder().id(1l).group(group).species(Species.DOG).breed("종").name("멍뭉이")
			.sex(Sex.MALE)
			.birthday(LocalDate.of(2022, 1, 1)).weight(5.5).build();
	List<UserGroup> userGroupList = List.of(
			new UserGroup(1L, User.builder().userName("user").build(), group, "mom", true),
			new UserGroup(1L, User.builder().userName("user1").build(), group, "mom", false)
	);

	@Nested
	@DisplayName("pet 등록하기")
	class addPet {

		PetAddRequest petAddRequest = new PetAddRequest("멍뭉이", Species.DOG, "종", Sex.MALE,
				LocalDate.of(2022, 1, 1), 5.5); // LocalDate 생일

		@Test
		@DisplayName("성공")
		void success() {
			given(userRepository.findByUserName("user")).willReturn(Optional.of(user));
			given(groupRepository.findById(1l)).willReturn(Optional.of(group));
			given(userGroupRepository.findAllByGroup(group)).willReturn(userGroupList);
			given(petRepository.save(petAddRequest.toEntity(group))).willReturn(pet);

			PetAddResponse response = assertDoesNotThrow(
					() -> petService.add(1l, petAddRequest, "user"));

			assertEquals(1L, response.getId());
			assertEquals(petAddRequest.getName(), response.getName());
			assertEquals("1살", response.getAge());
			assertEquals(null, response.getCreatedAt());
		}
	}

	@Nested
	@DisplayName("pet 수정하기")
	class modifyPet {

		PetAddRequest update = new PetAddRequest("야옹이", Species.CAT, "종", Sex.FEMALE,
				LocalDate.of(2020, 1, 1), 1.5); // LocalDate 생일

		@Test
		@DisplayName("성공")
		void success() {
			given(petRepository.findById(1l)).willReturn(Optional.of(pet));
			given(userRepository.findByUserName("user")).willReturn(Optional.of(user));
			given(groupRepository.findById(1l)).willReturn(Optional.of(group));
			given(userGroupRepository.findAllByGroup(group)).willReturn(userGroupList);
			given(petRepository.save(pet)).willReturn(pet);

			PetUpdateResponse response = assertDoesNotThrow(
					() -> petService.modify(1l, 1l, update, "user"));

			assertEquals(1L, response.getId());
			assertEquals(update.getName(), response.getName());
			assertEquals("3살", response.getAge());
			assertEquals(null, response.getLastModifiedAt());

		}

	}

	@Nested
	@DisplayName("pet 삭제하기")
	class deletePet {

		@Test
		@DisplayName("성공")
		void success() {
			given(petRepository.findById(1l)).willReturn(Optional.of(pet));
			given(userRepository.findByUserName("user")).willReturn(Optional.of(user));
			given(groupRepository.findById(1l)).willReturn(Optional.of(group));
			given(userGroupRepository.findAllByGroup(group)).willReturn(userGroupList);

			PetDeleteResponse petDeleteResponse = assertDoesNotThrow(
					() -> petService.delete(1l, 1l, "user"));

			assertEquals("등록이 취소되었습니다.", petDeleteResponse.getMessage());
		}

	}
}