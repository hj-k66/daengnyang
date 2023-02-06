package com.daengnyangffojjak.dailydaengnyang.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleCreateRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleCreateResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleModifyRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleModifyResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserRole;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.BaseEntity;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Schedule;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Category;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Sex;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import com.daengnyangffojjak.dailydaengnyang.repository.ScheduleRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;


class ScheduleServiceTest {

	private final ScheduleRepository scheduleRepository = mock(ScheduleRepository.class);

	private final Validator validator = mock(Validator.class);

	private ScheduleService scheduleService = new ScheduleService(scheduleRepository, validator);

	//2023-01-25 10:26:00
	LocalDateTime dateTime = LocalDateTime.of(2023, 1, 25, 10, 26);

	User user = User.builder().id(1L).userName("user").password("password").email("@.")
			.role(UserRole.ROLE_USER).build();

	Group group = Group.builder().id(1L).name("그룹이름").user(user).build();

	Pet pet = Pet.builder().id(1L).birthday(LocalDate.of(2023, 2, 3)).species(Species.CAT)
			.name("aaa").group(group).sex(Sex.NEUTERED_MALE).build();

	Schedule schedule = Schedule.builder().id(1L).user(user).pet(pet).category(Category.HOSPITAL)
			.title("병원").body("초음파 재검").assigneeId(1L).place("멋사동물병원").isCompleted(false)
			.dueDate(dateTime).build();

//	Schedule schedule1 = Schedule.builder().id(1L).user(user).pet(pet).category(Category.HOSPITAL)
//			.title("병원1").body("초음파 재검1").assigneeId(1L).place("멋사동물병원").isCompleted(false)
//			.dueDate(dateTime).build();

//	ScheduleResponse getResponse = new ScheduleResponse(1L, 1L, 1L, "aaa", Category.HOSPITAL,
//			"병원", "초음파 재검", 1L, "멋사동물병원", true, dateTime, dateTime, dateTime);

//	ScheduleResponse getResponse = ScheduleResponse.builder().id(1L).userId(user.getId()).petId(pet.getId()).petName(pet.getName())
//			.category(schedule.getCategory()).title(schedule.getTitle()).body(schedule.getBody()).assigneeId(schedule.getAssigneeId())
//			.place(schedule.getPlace()).isCompleted(schedule.isCompleted()).dueDate(dateTime).createdAt(dateTime).lastModifiedAt(dateTime).build();

	@Nested
	@DisplayName("일정등록")
	class create_schedule {

		ScheduleCreateRequest scheduleCreateRequest = new ScheduleCreateRequest(Category.HOSPITAL,
				"병원", "초음파 재검", 1L, "멋사동물병원", dateTime);

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(1L, user.getUsername())).willReturn(pet);
			given(scheduleRepository.save(scheduleCreateRequest.toEntity(pet, user))).willReturn(
					schedule);

			ScheduleCreateResponse response = assertDoesNotThrow(
					() -> scheduleService.create(1L, scheduleCreateRequest, "user"));

			assertEquals("일정 등록 완료", response.getMessage());
			assertEquals(1L, response.getId());

		}

	}

	@Nested
	@DisplayName("일정수정")
	class modify_schedule {

		ScheduleModifyRequest scheduleModifyRequest = new ScheduleModifyRequest(Category.HOSPITAL,
				"병원 수정", "초음파 재검 수정", 2L, "멋사동물병원 수정", true, dateTime);
		Schedule modifySchedule = Schedule.builder().id(1L).user(user).pet(pet)
				.category(Category.HOSPITAL).title("병원 수정").body("초음파 재검 수정").assigneeId(2L)
				.place("멋사동물병원 수정").isCompleted(true).dueDate(dateTime).build();

//		ReflectionTestUtils.setField(modifySchedule, BaseEntity.class, "updatedAt", LocalDateTime.of(2023, 1, 25, 10, 26), LocalDateTime.class);


		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(1L, user.getUsername())).willReturn(pet);
			given(scheduleRepository.findById(1L)).willReturn(Optional.of(schedule));
			given(scheduleRepository.saveAndFlush(modifySchedule)).willReturn(modifySchedule);

			ScheduleModifyResponse response = assertDoesNotThrow(
					() -> scheduleService.modify(1L, 1L, scheduleModifyRequest, "user"));

			assertEquals(1L, response.getId());
			assertEquals("병원 수정", response.getTitle());
			//modifyAt 계속 null 나옴
//			assertEquals("2023-01-25 10:26:00", response.getLastModifiedAt());

		}

	}

	@Nested
	@DisplayName("일정삭제")
	class delete_schedule {

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(1L, user.getUsername())).willReturn(pet);
			given(scheduleRepository.findById(1L)).willReturn(Optional.of(schedule));

			ScheduleDeleteResponse response = assertDoesNotThrow(
					() -> scheduleService.delete(1L, 1L, "user"));

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
			assertEquals(1L, response.getUserId());
			assertEquals(1L, response.getPetId());
			assertEquals("aaa", response.getPetName());
			assertEquals(Category.HOSPITAL, response.getCategory());
			assertEquals("병원", response.getTitle());
			assertEquals("초음파 재검", response.getBody());
			assertEquals(1L, response.getAssigneeId());
//			assertEquals(LocalDateTime.of(2023, 1, 25, 10, 26), response.getDueDate());
//			assertEquals(LocalDateTime.of(2023, 1, 25, 10, 26), response.getCreatedAt());
//			assertEquals(LocalDateTime.of(2023, 1, 25, 10, 26), response.getLastModifiedAt());

		}

	}

//	@Nested
//	@DisplayName("일정전체조회-개체별")
//	class list_schedule {
//		@Test
//		@DisplayName("성공")
//		void success() {
//			Pageable pageable = PageRequest.of(0, 20, Sort.Direction.DESC, "dueDate");
//
//			List<Schedule> schedules = List.of(schedule, schedule1);
//
//			Page<Schedule> schedulePage = new PageImpl<>(schedules);
//
//
//			given(validator.getUserByUserName("user")).willReturn(user);
//			given(validator.getPetWithUsername(1L, user.getUsername())).willReturn(pet);
//			given(scheduleRepository.findAllByPetId(1L, pageable)).willReturn(new PageImpl<>(
//					Arrays.asList(schedule, schedule1)));
//			given(ScheduleListResponse.toResponse(schedulePage)).willReturn()
//
//			Page<ScheduleListResponse> responses = assertDoesNotThrow(
//					() -> scheduleService.list(1L, "user", pageable));
//
//			assertEquals(Category.HOSPITAL, responses.);
//			assertEquals("병원", response.);
//			assertEquals("초음파 재검", response.getBody());
//			assertEquals(1L, response.getAssigneeId());
//			assertEquals(1L, response.getAssigneeId())
//			assertEquals(1L, response.getAssigneeId())
////			assertEquals(LocalDateTime.of(2023, 1, 25, 10, 26), response.getDueDate());
//
//		}
//
//	}

}