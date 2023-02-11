package com.daengnyangffojjak.dailydaengnyang.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordWorkRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordWorkResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserRole;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Sex;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import com.daengnyangffojjak.dailydaengnyang.repository.RecordRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

class RecordServiceTest {

	private final RecordRepository recordRepository = mock(RecordRepository.class);
	private final Validator validator = mock(Validator.class);
	private final ApplicationEventPublisher applicationEventPublisher = mock(
			ApplicationEventPublisher.class);

	private RecordService recordService = new RecordService(recordRepository, validator,
			applicationEventPublisher);

	LocalDateTime localDateTime = LocalDateTime.of(2023, 2, 22, 22, 22);

	User user = User.builder().id(1L).userName("user").password("password").email("@.")
			.role(UserRole.ROLE_USER).build();

	Group group = new Group(1L, user, "group");

	Pet pet = Pet.builder().id(1L).birthday(LocalDate.of(2023, 2, 3)).species(Species.CAT)
			.name("pet").group(group).sex(Sex.NEUTERED_MALE).build();

	Tag tag = new Tag(1L, group, "질병");

	Record record = new Record(1L, user, pet, tag, "제목", "본문", true);

	@Nested
	@DisplayName("일기 상세 조회")
	class Get_OneRecord {

		@Test
		@DisplayName("성공")
		void sueccss() {

			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(1L, user.getUsername())).willReturn(pet);
			given(validator.getRecordById(1L)).willReturn(record);

			RecordResponse recordResponse = assertDoesNotThrow(
					() -> recordService.getOneRecord(1L, 1L, "user"));

			assertEquals(1L, recordResponse.getId());
			assertEquals(1L, recordResponse.getUserId());
			assertEquals(1L, recordResponse.getPetId());
			assertEquals("제목", recordResponse.getTitle());
			assertEquals("본문", recordResponse.getBody());
			assertEquals("user", recordResponse.getUserName());
			assertEquals(true, recordResponse.getIsPublic());
			assertEquals("질병", recordResponse.getTag());
		}
	}
/*
	@Nested
	@DisplayName("일기 등록")
	class create_record {

		RecordWorkRequest createRecordWorkRequest = new RecordWorkRequest(1L, "제목", "본문", true);

		@Test
		@DisplayName("성공")
		void success() {

			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(1L, user.getUsername())).willReturn(pet);
			given(validator.getTagById(1L)).willReturn(tag);

			given(recordRepository.save(
					createRecordWorkRequest.toEntity(user, pet, tag))).willReturn(record);

			RecordWorkResponse recordWorkResponse = assertDoesNotThrow(
					() -> recordService.createRecord(1L, createRecordWorkRequest, "user"));

			assertEquals("일기 작성 완료", recordWorkResponse.getMessage());
			assertEquals(1L, recordWorkResponse.getId());
		}
	}


	@Nested
	@DisplayName("일기 삭제")
	class Delete_Record {

		@Test
		@DisplayName("성공")
		void success() {

			given(validator.getUserByUserName("user")).willReturn(user);
			given(recordRepository.findById(1L)).willReturn(Optional.of(record));

			RecordWorkResponse deleteRecordResponse = assertDoesNotThrow(
					() -> recordService.deleteRecord(1L, "user"));

			assertEquals("일기 삭제 완료", deleteRecordResponse.getMessage());
		}
	}
 */
}
