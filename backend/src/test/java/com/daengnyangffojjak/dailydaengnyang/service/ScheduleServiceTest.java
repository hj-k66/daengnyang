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

	Tag tag = new Tag(1L, group, "질병");

	Schedule schedule = new Schedule(1L, user, pet, tag, "병원", "초음파 재검", 1L, "멋사동물병원",
			dateTime,
			false);

	@Nested
	@DisplayName("일정 완료하기")
	class ScheduleComplete{
		ScheduleCompleteRequest scheduleCompleteRequest = new ScheduleCompleteRequest(true);
		Schedule completeSchedule = new Schedule(1L, user, pet, tag, "병원", "초음파 재검", 2L, "멋사동물병원",
				dateTime,
				true);

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(pet.getId(), "user")).willReturn(pet);
			given(scheduleRepository.findById(1L)).willReturn(Optional.of(schedule));
			given(scheduleRepository.saveAndFlush(completeSchedule)).willReturn(completeSchedule);

			ScheduleCompleteResponse response = assertDoesNotThrow(
					() -> scheduleService.complete(pet.getId(), schedule.getId(),
							scheduleCompleteRequest, "user"));

			assertEquals("일정이 완료되었습니다.", response.getMessage());
			assertEquals(1L, response.getId());
		}
	}

	@Nested
	@DisplayName("일정 부탁하기")
	class ScheduleAssign {

		List<UserGroup> userGroupList = List.of(
				new UserGroup(1L, User.builder().userName("user").build(), group, "mom"),
				new UserGroup(2L, User.builder().userName("희정").build(), group, "dad"));
		ScheduleAssignRequest scheduleAssignRequest = new ScheduleAssignRequest("희정", "내일까지 부탁해!");
		User receiver = User.builder().id(2L).userName("희정").password("password").email("@.")
				.role(UserRole.ROLE_USER).build();
		Schedule modifiedSchedule = new Schedule(1L, user, pet, tag, "병원", "초음파 재검", 2L,
				"멋사동물병원", dateTime, false);

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(pet.getId(), "user")).willReturn(pet);
			given(validator.getUserGroupListByUsername(group,
					scheduleAssignRequest.getReceiverName())).willReturn(userGroupList);
			given(validator.getUserByUserName("희정")).willReturn(receiver);
			given(scheduleRepository.findById(1L)).willReturn(Optional.of(schedule));
			given(scheduleRepository.saveAndFlush(modifiedSchedule)).willReturn(modifiedSchedule);

			MessageResponse response = assertDoesNotThrow(
					() -> scheduleService.assign(pet.getId(), schedule.getId(),
							scheduleAssignRequest, "user"));

			assertEquals("일정의 책임자가 변경되었습니다.", response.getMsg());
		}
	}

	@Nested
	@DisplayName("일정등록")
	class create_schedule {

		ScheduleCreateRequest scheduleCreateRequest = new ScheduleCreateRequest(1L,
				"병원", "초음파 재검", 1L, "멋사동물병원", dateTime);
		Schedule schedule = new Schedule(1L, user, pet, tag, "병원", "초음파 재검", 1L, "멋사동물병원",
				dateTime, false);

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(1L, user.getUsername())).willReturn(pet);
			given(validator.getTagById(1L)).willReturn(tag);
			given(scheduleRepository.save(
					scheduleCreateRequest.toEntity(pet, user, tag))).willReturn(schedule);

			ScheduleCreateResponse response = assertDoesNotThrow(
					() -> scheduleService.create(1L, scheduleCreateRequest, "user"));

			assertEquals("일정 등록 완료", response.getMsg());
			assertEquals(1L, response.getId());

		}

	}

	@Nested
	@DisplayName("일정수정")
	class modify_schedule {

		ScheduleModifyRequest scheduleModifyRequest = new ScheduleModifyRequest(1L,
				"수정 병원", "수정 초음파 재검", 2L, "수정 멋사동물병원", dateTime, true);
		Schedule modifySchedule = new Schedule(1L, user, pet, tag, "수정 병원", "수정 초음파 재검",
				2L, "수정 멋사동물병원", dateTime, true);

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(1L, user.getUsername())).willReturn(pet);
			given(validator.getTagById(1L)).willReturn(tag);
			given(scheduleRepository.findById(1L)).willReturn(Optional.of(schedule));
			given(scheduleRepository.saveAndFlush(modifySchedule)).willReturn(modifySchedule);

			ScheduleModifyResponse response = assertDoesNotThrow(
					() -> scheduleService.modify(1L, 1L, scheduleModifyRequest, "user"));

			assertEquals(1L, response.getId());
			assertEquals("수정 병원", response.getTitle());

		}

	}

	@Nested
	@DisplayName("일정삭제")
	class delete_schedule {

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(scheduleRepository.findById(1L)).willReturn(Optional.of(schedule));

			ScheduleDeleteResponse response = assertDoesNotThrow(
					() -> scheduleService.delete(1L, "user"));

			assertEquals("일정이 삭제되었습니다.", response.getMsg());

		}

	}

	@Nested
	@DisplayName("일정상세조회")
	class get_schedule {

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(1L, user.getUsername())).willReturn(pet);
			given(scheduleRepository.findById(1L)).willReturn(Optional.of(schedule));

			ScheduleResponse response = assertDoesNotThrow(
					() -> scheduleService.get(1L, 1L, "user"));

			assertEquals(1L, response.getId());
			assertEquals("질병", response.getTag());
			assertEquals(1L, response.getUserId());
			assertEquals("user", response.getUserName());
			assertEquals(1L, response.getPetId());
			assertEquals("pet", response.getPetName());
			assertEquals("병원", response.getTitle());
			assertEquals("초음파 재검", response.getBody());
			assertEquals(1L, response.getAssigneeId());
			assertEquals("멋사동물병원", response.getPlace());
			assertEquals(dateTime, response.getDueDate());

		}


	}
}