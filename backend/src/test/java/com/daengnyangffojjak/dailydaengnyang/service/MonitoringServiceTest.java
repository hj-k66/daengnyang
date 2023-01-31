package com.daengnyangffojjak.dailydaengnyang.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntMakeRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntMakeResponse;
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

		MntMakeRequest request = MntMakeRequest.builder()
				.date(LocalDate.of(2023, 1, 30)).weight(7.7).vomit(false)
				.amPill(true).pmPill(true).urination(3).defecation(2).notes("양치").build();
		Monitoring saved = Monitoring.builder()
				.id(1L).pet(pet).date(LocalDate.of(2023, 1, 30)).weight(7.7).vomit(false)
				.amPill(true).pmPill(true).urination(3).defecation(2).notes("양치").build();

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getPetById(1L)).willReturn(pet);
			given(validator.getUserGroupListByUsername(pet.getGroup(), "user")).willReturn(any());
			given(monitoringRepository.save(request.toEntity(pet))).willReturn(saved);

			MntMakeResponse response = assertDoesNotThrow(
					() -> monitoringService.create(1L, request, "user"));
			assertEquals(1L, response.getId());
			assertEquals("hoon", response.getPetName());
			assertEquals(LocalDate.of(2023, 1, 30), response.getDate());
		}
	}


}