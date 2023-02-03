package com.daengnyangffojjak.dailydaengnyang.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntGetResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntMonthlyResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntWriteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntWriteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserRole;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Monitoring;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Sex;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import com.daengnyangffojjak.dailydaengnyang.repository.MonitoringRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MonitoringServiceTest {

	private final MonitoringRepository monitoringRepository = mock(MonitoringRepository.class);
	private final Validator validator = mock(Validator.class);
	User user = User.builder().id(1L).userName("user").password("password").email("@.")
			.role(UserRole.ROLE_USER).build();
	Group group = Group.builder().id(1L).name("그룹이름").user(user).build();
	Pet pet = Pet.builder().id(1L).birthday(LocalDate.of(2018, 3, 1)).species(Species.CAT)
			.name("hoon").group(group).sex(Sex.NEUTERED_MALE).build();
	private MonitoringService monitoringService
			= new MonitoringService(monitoringRepository, validator);

	@Nested
	@DisplayName("모니터링 등록")
	class CreateMonitoring {

		MntWriteRequest request = MntWriteRequest.builder()
				.date(LocalDate.of(2023, 1, 30)).weight(7.7).vomit(false)
				.amPill(true).pmPill(true).urination(3).defecation(2).notes("양치").build();
		Monitoring saved = Monitoring.builder()
				.id(1L).pet(pet).date(LocalDate.of(2023, 1, 30)).weight(7.7).vomit(false)
				.amPill(true).pmPill(true).urination(3).defecation(2).notes("양치").build();

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getPetWithUsername(1L, "user")).willReturn(pet);
			given(monitoringRepository.save(request.toEntity(pet))).willReturn(
					saved);

			MntWriteResponse response = assertDoesNotThrow(
					() -> monitoringService.create(1L, request, "user"));
			assertEquals(1L, response.getId());
			assertEquals("hoon", response.getPetName());
			assertEquals(LocalDate.of(2023, 1, 30), response.getDate());
		}
	}

	@Nested
	@DisplayName("모니터링 수정")
	class ModifyMonitoring {

		MntWriteRequest request = MntWriteRequest.builder()
				.date(LocalDate.of(2023, 1, 30)).weight(7.7).vomit(false)
				.amPill(true).pmPill(true).urination(3).defecation(2).notes("바꾼거").build();
		Monitoring saved = Monitoring.builder()
				.id(1L).pet(pet).date(LocalDate.of(2023, 1, 30)).weight(7.7).vomit(false)
				.amPill(true).pmPill(true).urination(3).defecation(2).notes("양치").build();
		Monitoring modified = Monitoring.builder()
				.id(1L).pet(pet).date(LocalDate.of(2023, 1, 30)).weight(7.7).vomit(false)
				.amPill(true).pmPill(true).urination(3).defecation(2).notes("바꾼거").build();

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getPetWithUsername(1L, "user")).willReturn(pet);
			given(validator.getMonitoringById(1L)).willReturn(saved);
			given(monitoringRepository.saveAndFlush(saved)).willReturn(modified);

			MntWriteResponse response = assertDoesNotThrow(
					() -> monitoringService.modify(1L, 1L, request, "user"));
			assertEquals(1L, response.getId());
			assertEquals("hoon", response.getPetName());
			assertEquals(LocalDate.of(2023, 1, 30), response.getDate());
		}

		@Test
		@DisplayName("실패 - 펫등록번호와 모니터링의 펫 정보가 다를 때")
		void fail() {
			Monitoring saved = Monitoring.builder()
					.id(1L).pet(pet).date(LocalDate.of(2023, 1, 30)).weight(7.7).vomit(false)
					.amPill(true).pmPill(true).urination(3).defecation(2).notes("양치").build();
			given(validator.getPetWithUsername(1L, "user")).willReturn(pet);
			given(validator.getMonitoringById(1L)).willReturn(saved);
			given(monitoringRepository.saveAndFlush(saved)).willReturn(modified);

			MntWriteResponse response = assertDoesNotThrow(
					() -> monitoringService.modify(1L, 1L, request, "user"));
			assertEquals(1L, response.getId());
			assertEquals("hoon", response.getPetName());
			assertEquals(LocalDate.of(2023, 1, 30), response.getDate());
		}
	}

	@Nested
	@DisplayName("모니터링 삭제")
	class DeleteMonitoring {

		Monitoring saved = Monitoring.builder()
				.id(1L).pet(pet).date(LocalDate.of(2023, 1, 30)).weight(7.7).vomit(false)
				.amPill(true).pmPill(true).urination(3).defecation(2).notes("양치").build();

		@DisplayName("성공")
		void success() {
			given(validator.getPetWithUsername(1L, "user")).willReturn(pet);
			given(validator.getMonitoringById(1L)).willReturn(saved);

			MntDeleteResponse response = assertDoesNotThrow(
					() -> monitoringService.delete(1L, 1L, "user"));
			assertEquals(1L, response.getId());
			assertEquals("모니터링 삭제 완료", response.getMessage());
		}
	}

	@Nested
	@DisplayName("모니터링 단건조회")
	class ShowMonitoring {

		Monitoring saved = Monitoring.builder()
				.id(1L).pet(pet).date(LocalDate.of(2023, 1, 30)).weight(7.7).vomit(false)
				.amPill(true).pmPill(true).urination(3).defecation(2).notes("양치").build();

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getPetWithUsername(1L, "user")).willReturn(pet);
			given(validator.getMonitoringById(1L)).willReturn(saved);

			MntGetResponse response = assertDoesNotThrow(
					() -> monitoringService.getMonitoring(1L, 1L, "user"));
			assertEquals("양치", response.getNotes());
			assertEquals(false, response.getVomit());
			assertEquals(3, response.getUrination());
		}
	}

	@Nested
	@DisplayName("모니터링 달조회")
	class ShowMonitoringMonthly {

		List<Monitoring> saved = List.of(
				Monitoring.builder()
						.id(1L).pet(pet).date(LocalDate.of(2023, 1, 25)).weight(7.7).vomit(false)
						.amPill(true).pmPill(true).urination(3).defecation(2).notes("양치").build(),
				Monitoring.builder()
						.id(2L).pet(pet).date(LocalDate.of(2023, 1, 30)).weight(7.7).vomit(false)
						.amPill(true).pmPill(true).urination(3).defecation(2).notes("양치").build()
		);

		@Test
		@DisplayName("성공")
		void success() {
			LocalDate start = LocalDate.of(2023, 1, 1);
			LocalDate end = LocalDate.of(2023, 1, 31);
			given(validator.getPetWithUsername(1L, "user")).willReturn(pet);
			given(monitoringRepository.findAllByDateBetween(start, end)).willReturn(saved);

			MntMonthlyResponse response = assertDoesNotThrow(
					() -> monitoringService.getMonthly(1L, 2023, 1, "user"));
			assertEquals(2, response.getMonthlyMonitorings().size());
		}
	}
}
