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
import com.daengnyangffojjak.dailydaengnyang.repository.RecordFileRepository;
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
	private final RecordFileRepository recordFileRepository = mock(RecordFileRepository.class);
	private final Validator validator = mock(Validator.class);
	private final ApplicationEventPublisher applicationEventPublisher = mock(
			ApplicationEventPublisher.class);

	private RecordService recordService = new RecordService(recordRepository, recordFileRepository,
			validator,
			applicationEventPublisher);

	User user = User.builder().id(1L).userName("user").password("password").email("@.")
			.role(UserRole.ROLE_USER).build();

	Group group = new Group(1L, user, "group");

	Pet pet = Pet.builder().id(1L).birthday(LocalDate.of(2023, 2, 3)).species(Species.CAT)
			.name("pet").group(group).sex(Sex.NEUTERED_MALE).build();

	Tag tag = new Tag(1L, group, "??????");

	Record record = new Record(1L, user, pet, tag, "??????", "??????", true);

	@Nested
	@DisplayName("?????? ?????? ??????")
	class Get_OneRecord {

		@Test
		@DisplayName("??????")
		void sueccss() {

			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(1L, user.getUsername())).willReturn(pet);
			given(validator.getRecordById(1L)).willReturn(record);

			RecordResponse recordResponse = assertDoesNotThrow(
					() -> recordService.getOneRecord(1L, 1L, "user"));

			assertEquals(1L, recordResponse.getId());
			assertEquals(1L, recordResponse.getUserId());
			assertEquals(1L, recordResponse.getPetId());
			assertEquals("??????", recordResponse.getTitle());
			assertEquals("??????", recordResponse.getBody());
			assertEquals("user", recordResponse.getUserName());
			assertEquals(true, recordResponse.getIsPublic());
			assertEquals("??????", recordResponse.getTag());
		}
	}

	@Nested
	@DisplayName("?????? ??????")
	class create_record {

		RecordWorkRequest createRecordWorkRequest = new RecordWorkRequest(1L, "??????", "??????", false);

		@Test
		@DisplayName("??????")
		void success() {

			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(1L, user.getUsername())).willReturn(pet);
			given(validator.getTagById(1L)).willReturn(tag);

			given(recordRepository.save(
					createRecordWorkRequest.toEntity(user, pet, tag))).willReturn(record);

			RecordWorkResponse recordWorkResponse = assertDoesNotThrow(
					() -> recordService.createRecord(1L, createRecordWorkRequest, "user"));

			assertEquals("?????? ?????? ??????", recordWorkResponse.getMessage());
			assertEquals(1L, recordWorkResponse.getId());
		}
	}

	@Nested
	@DisplayName("?????? ??????")
	class Modify_Record {

		RecordWorkRequest modifyRecordWorkRequest = new RecordWorkRequest(1L, "?????? ??????", "?????? ??????",
				true);
		Record modifyRecord = Record.builder().id(1L).user(user).pet(pet).tag(tag).title("?????? ??????")
				.body("?????? ??????").isPublic(true).build();

		@Test
		@DisplayName("??????")
		void success() {

			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getPetWithUsername(1L, user.getUsername())).willReturn(pet);
			given(validator.getTagById(1L)).willReturn(tag);
			given(validator.getRecordById(1L)).willReturn(record);
			given(recordRepository.saveAndFlush(record)).willReturn(modifyRecord);

			RecordWorkResponse modifyRecordResponse = assertDoesNotThrow(
					() -> recordService.modifyRecord(1L, 1L, modifyRecordWorkRequest, "user"));

			assertEquals("?????? ?????? ??????", modifyRecordResponse.getMessage());
			assertEquals(1L, modifyRecordResponse.getId());
		}
	}

	@Nested
	@DisplayName("?????? ??????")
	class Delete_Record {

		@Test
		@DisplayName("??????")
		void success() {

			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getRecordById(1L)).willReturn(record);

			RecordWorkResponse deleteRecordResponse = assertDoesNotThrow(
					() -> recordService.deleteRecord(1L, "user"));

			assertEquals("?????? ?????? ??????", deleteRecordResponse.getMessage());
		}
	}

}
