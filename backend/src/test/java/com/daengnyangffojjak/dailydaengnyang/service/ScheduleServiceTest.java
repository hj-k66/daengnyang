package com.daengnyangffojjak.dailydaengnyang.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleCreateRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleCreateResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserRole;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ScheduleServiceTest {

	private final ScheduleRepository scheduleRepository = mock(ScheduleRepository.class);

	private final Validator validator = mock(Validator.class);

	LocalDateTime dateTime = LocalDateTime.of(2023, 1, 25, 10, 26);

	User user = User.builder().id(1L).userName("user").password("password").email("@.")
			.role(UserRole.ROLE_USER).build();

	Group group = Group.builder().id(1L).name("그룹이름").user(user).build();

	Pet pet = Pet.builder().id(1L).birthday(LocalDate.of(2023, 2, 3)).species(Species.CAT)
			.name("aaa").group(group).sex(Sex.NEUTERED_MALE).build();

	private ScheduleService scheduleService = new ScheduleService(scheduleRepository, validator);

	@Nested
	@DisplayName("일정등록")
	class create_schedule {

		ScheduleCreateRequest scheduleCreateRequest = new ScheduleCreateRequest(Category.HOSPITAL,
				"병원", "초음파 재검", 3L, "멋사동물병원", dateTime);

		ScheduleCreateResponse scheduleCreateResponse = new ScheduleCreateResponse("일정 등록 완료", 1L);

		Schedule saved = Schedule.builder().id(1L).user(user).pet(pet).category(Category.HOSPITAL)
				.title("title").body("body").assigneeId(1L).place("멋사 동물병원").isCompleted(false).dueDate(dateTime).build();

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(1L, user.getUsername())).willReturn(pet);
			given(scheduleRepository.save(scheduleCreateRequest.toEntity(pet, user))).willReturn(saved);

			ScheduleCreateResponse response = assertDoesNotThrow(
					() -> scheduleService.create(1L, scheduleCreateRequest, "user"));

			assertEquals("일정 등록 완료", response.getMessage());
			assertEquals(1L, response.getId());

		}

	}

}