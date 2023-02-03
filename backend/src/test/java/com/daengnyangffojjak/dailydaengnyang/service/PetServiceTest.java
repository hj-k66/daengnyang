package com.daengnyangffojjak.dailydaengnyang.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetShowResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetUpdateResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserRole;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.UserGroup;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Sex;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import com.daengnyangffojjak.dailydaengnyang.repository.PetRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


class PetServiceTest {

	private PetService petService;
	private final PetRepository petRepository = mock(PetRepository.class);
	private final Validator validator = mock(Validator.class);


	@BeforeEach
	public void setUp() {
		petService = new PetService(petRepository, validator);
	}

	User user = User.builder().id(1L).userName("user").password("password").email("@.")
			.role(UserRole.ROLE_USER).build();
	Group group = Group.builder().id(1L).name("user").user(user).build();
	Pet pet = Pet.builder().id(1l).group(group).species(Species.DOG).breed("종").name("멍뭉이")
			.sex(Sex.MALE)
			.birthday(LocalDate.of(2022, 1, 1)).weight(5.5).build();
	List<UserGroup> userGroupList = List.of(
			new UserGroup(1L, User.builder().userName("user2").build(), group, "mom"),
			new UserGroup(1L, User.builder().userName("user1").build(), group, "dad"));

	@Nested
	@DisplayName("pet 등록하기")
	class addPet {

		PetAddRequest petAddRequest = new PetAddRequest("멍뭉이", Species.DOG, "종", Sex.MALE,
				LocalDate.of(2022, 1, 1), 5.5); // LocalDate 생일

		@Test
		@DisplayName("성공")
		void success() {

			given(validator.getGroupById(1l)).willReturn(group);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(userGroupList);
			given(petRepository.save(petAddRequest.toEntity(group))).willReturn(pet);

			PetAddResponse petAddResponse = assertDoesNotThrow(
					() -> petService.add(1l, petAddRequest, "user"));

			assertEquals(1L, petAddResponse.getId());
			assertEquals(petAddRequest.getName(), petAddResponse.getName());
			assertEquals("1살", petAddResponse.getAge());
			assertEquals(null, petAddResponse.getCreatedAt());
		}
	}

	@Nested
	@DisplayName("pet 단건 조회하기")
	class showPet {

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetById(1l)).willReturn(pet);
			given(validator.getGroupById(1l)).willReturn(group);

			PetShowResponse petShowResponse = assertDoesNotThrow(
					() -> petService.show(1l, 1l, "user"));

			assertEquals(1L, petShowResponse.getId());
			assertEquals("멍뭉이", petShowResponse.getName());
			assertEquals(Species.DOG, petShowResponse.getSpecies());
			assertEquals("종", petShowResponse.getBreed());
			assertEquals(Sex.MALE, petShowResponse.getSex());
			assertEquals(LocalDate.of(2022, 1, 1), petShowResponse.getBirthday());
			assertEquals(null, petShowResponse.getCreatedAt());
			assertEquals(null, petShowResponse.getLastModifiedAt());
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
			given(validator.getPetById(1l)).willReturn(pet);
			given(validator.getGroupById(1l)).willReturn(group);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(userGroupList);
			given(petRepository.saveAndFlush(pet)).willReturn(pet);

			PetUpdateResponse petUpdateResponse = assertDoesNotThrow(
					() -> petService.modify(1l, 1l, update, "user"));

			assertEquals(1L, petUpdateResponse.getId());
			assertEquals(update.getName(), petUpdateResponse.getName());
			assertEquals("3살", petUpdateResponse.getAge());
			assertEquals(null, petUpdateResponse.getLastModifiedAt());
		}
	}

	@Nested
	@DisplayName("pet 삭제하기")
	class deletePet {

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getPetById(1l)).willReturn(pet);
			given(validator.getGroupById(1l)).willReturn(group);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(userGroupList);

			PetDeleteResponse petDeleteResponse = assertDoesNotThrow(
					() -> petService.delete(1l, 1l, "user"));

			assertEquals("등록이 취소되었습니다.", petDeleteResponse.getMessage());
		}
	}
}