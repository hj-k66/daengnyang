package com.daengnyangffojjak.dailydaengnyang.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleAssignRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleCompleteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleCompleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleCreateRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleCreateResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleModifyRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleModifyResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserRole;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Schedule;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.UserGroup;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Sex;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import com.daengnyangffojjak.dailydaengnyang.repository.ScheduleRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;


class ScheduleServiceTest {

	private final ScheduleRepository scheduleRepository = mock(ScheduleRepository.class);

	private final Validator validator = mock(Validator.class);

	private final ApplicationEventPublisher applicationEventPublisher = mock(
			ApplicationEventPublisher.class);

	private ScheduleService scheduleService = new ScheduleService(scheduleRepository, validator,
			applicationEventPublisher);

	LocalDateTime dateTime = LocalDateTime.of(2023, 1, 25, 10, 26);

	User user = User.builder().id(1L).userName("user").password("password").email("@.")
			.role(UserRole.ROLE_USER).build();

	Group group = new Group(1L, user, "group");

	Pet pet = Pet.builder().id(1L).birthday(LocalDate.of(2023, 2, 3)).species(Species.CAT)
			.name("pet").group(group).sex(Sex.NEUTERED_MALE).build();

	Tag tag = new Tag(1L, group, "??????");

	Schedule schedule = new Schedule(1L, user, pet, tag, "??????", "????????? ??????", 1L, "??????????????????",
			dateTime,
			false);

	@Nested
	@DisplayName("?????? ????????????")
	class ScheduleComplete{
		ScheduleCompleteRequest scheduleCompleteRequest = new ScheduleCompleteRequest(true);
		Schedule completeSchedule = new Schedule(1L, user, pet, tag, "??????", "????????? ??????", 2L, "??????????????????",
				dateTime,
				true);

		@Test
		@DisplayName("??????")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(pet.getId(), "user")).willReturn(pet);
			given(scheduleRepository.findById(1L)).willReturn(Optional.of(schedule));
			given(scheduleRepository.saveAndFlush(completeSchedule)).willReturn(completeSchedule);

			ScheduleCompleteResponse response = assertDoesNotThrow(
					() -> scheduleService.complete(pet.getId(), schedule.getId(),
							scheduleCompleteRequest, "user"));

			assertEquals("????????? ?????????????????????.", response.getMessage());
			assertEquals(1L, response.getId());
		}
	}

	@Nested
	@DisplayName("?????? ????????????")
	class ScheduleAssign {

		List<UserGroup> userGroupList = List.of(
				new UserGroup(1L, User.builder().userName("user").build(), group, "mom"),
				new UserGroup(2L, User.builder().userName("??????").build(), group, "dad"));
		ScheduleAssignRequest scheduleAssignRequest = new ScheduleAssignRequest("??????", "???????????? ?????????!");
		User receiver = User.builder().id(2L).userName("??????").password("password").email("@.")
				.role(UserRole.ROLE_USER).build();
		Schedule modifiedSchedule = new Schedule(1L, user, pet, tag, "??????", "????????? ??????", 2L,
				"??????????????????", dateTime, false);

		@Test
		@DisplayName("??????")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(pet.getId(), "user")).willReturn(pet);
			given(validator.getUserGroupListByUsername(group,
					scheduleAssignRequest.getReceiverName())).willReturn(userGroupList);
			given(validator.getUserByUserName("??????")).willReturn(receiver);
			given(scheduleRepository.findById(1L)).willReturn(Optional.of(schedule));
			given(scheduleRepository.saveAndFlush(modifiedSchedule)).willReturn(modifiedSchedule);

			MessageResponse response = assertDoesNotThrow(
					() -> scheduleService.assign(pet.getId(), schedule.getId(),
							scheduleAssignRequest, "user"));

			assertEquals("????????? ???????????? ?????????????????????.", response.getMsg());
		}
	}

	@Nested
	@DisplayName("????????????")
	class create_schedule {

		ScheduleCreateRequest scheduleCreateRequest = new ScheduleCreateRequest(1L,
				"??????", "????????? ??????", 1L, "??????????????????", dateTime);
		Schedule schedule = new Schedule(1L, user, pet, tag, "??????", "????????? ??????", 1L, "??????????????????",
				dateTime, false);

		@Test
		@DisplayName("??????")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(1L, user.getUsername())).willReturn(pet);
			given(validator.getTagById(1L)).willReturn(tag);
			given(scheduleRepository.save(
					scheduleCreateRequest.toEntity(pet, user, tag))).willReturn(schedule);

			ScheduleCreateResponse response = assertDoesNotThrow(
					() -> scheduleService.create(1L, scheduleCreateRequest, "user"));

			assertEquals("?????? ?????? ??????", response.getMsg());
			assertEquals(1L, response.getId());

		}

	}

	@Nested
	@DisplayName("????????????")
	class modify_schedule {

		ScheduleModifyRequest scheduleModifyRequest = new ScheduleModifyRequest(1L,
				"?????? ??????", "?????? ????????? ??????", 2L, "?????? ??????????????????", dateTime, true);
		Schedule modifySchedule = new Schedule(1L, user, pet, tag, "?????? ??????", "?????? ????????? ??????",
				2L, "?????? ??????????????????", dateTime, true);

		@Test
		@DisplayName("??????")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(1L, user.getUsername())).willReturn(pet);
			given(validator.getTagById(1L)).willReturn(tag);
			given(scheduleRepository.findById(1L)).willReturn(Optional.of(schedule));
			given(scheduleRepository.saveAndFlush(modifySchedule)).willReturn(modifySchedule);

			ScheduleModifyResponse response = assertDoesNotThrow(
					() -> scheduleService.modify(1L, 1L, scheduleModifyRequest, "user"));

			assertEquals(1L, response.getId());
			assertEquals("?????? ??????", response.getTitle());

		}

	}

	@Nested
	@DisplayName("????????????")
	class delete_schedule {

		@Test
		@DisplayName("??????")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(scheduleRepository.findById(1L)).willReturn(Optional.of(schedule));

			ScheduleDeleteResponse response = assertDoesNotThrow(
					() -> scheduleService.delete(1L, "user"));

			assertEquals("????????? ?????????????????????.", response.getMsg());

		}

	}

	@Nested
	@DisplayName("??????????????????")
	class get_schedule {

		@Test
		@DisplayName("??????")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(1L, user.getUsername())).willReturn(pet);
			given(scheduleRepository.findById(1L)).willReturn(Optional.of(schedule));

			ScheduleResponse response = assertDoesNotThrow(
					() -> scheduleService.get(1L, 1L, "user"));

			assertEquals(1L, response.getId());
			assertEquals("??????", response.getTag());
			assertEquals(1L, response.getUserId());
			assertEquals("user", response.getUserName());
			assertEquals(1L, response.getPetId());
			assertEquals("pet", response.getPetName());
			assertEquals("??????", response.getTitle());
			assertEquals("????????? ??????", response.getBody());
			assertEquals(1L, response.getAssigneeId());
			assertEquals("??????????????????", response.getPlace());
			assertEquals(dateTime, response.getDueDate());

		}


	}
}