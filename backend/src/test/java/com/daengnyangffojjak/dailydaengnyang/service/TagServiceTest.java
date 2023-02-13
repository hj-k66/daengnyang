package com.daengnyangffojjak.dailydaengnyang.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.tag.TagListResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.tag.TagWorkRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.tag.TagWorkResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserRole;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.TagException;
import com.daengnyangffojjak.dailydaengnyang.fixture.GroupFixture;
import com.daengnyangffojjak.dailydaengnyang.repository.RecordRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.ScheduleRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.TagRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TagServiceTest {

	private final TagRepository tagRepository = mock(TagRepository.class);
	private final ScheduleRepository scheduleRepository = mock(ScheduleRepository.class);
	private final RecordRepository recordRepository = mock(RecordRepository.class);
	private final Validator validator = mock(Validator.class);

	private TagService tagService = new TagService(tagRepository, scheduleRepository,
			recordRepository, validator);
	Group group = GroupFixture.get();

	TagWorkRequest tagWorkRequest = new TagWorkRequest("태그이름");
	Tag tag = Tag.builder().id(1L).group(group).name("태그이름").build();
	Tag tag2 = Tag.builder().id(2L).group(group).name("태그이름2").build();


	@Nested
	@DisplayName("태그 생성")
	class CreateTag {
		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(new ArrayList<>());
			given(tagRepository.save(tagWorkRequest.toEntity(group))).willReturn(tag);

			TagWorkResponse response = assertDoesNotThrow(
					() -> tagService.create(1L, tagWorkRequest, "user"));
			assertEquals(1L, response.getId());
			assertEquals("태그이름", response.getName());
		}

		@Test
		@DisplayName("실패 - 이미 존재하는 이름")
		void fail_이름중복() {
			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(new ArrayList<>());
			given(tagRepository.save(tagWorkRequest.toEntity(group))).willReturn(tag);
			given(tagRepository.existsByGroupIdAndName(1L, "태그이름")).willReturn(true);

			TagException e = assertThrows(TagException.class,
					() -> tagService.create(1L, tagWorkRequest, "user"));
			assertEquals(ErrorCode.DUPLICATED_TAG_NAME, e.getErrorCode());
		}
	}
	@Nested
	@DisplayName("태그 수정")
	class ModifyTag {
		TagWorkRequest tagWorkRequest = new TagWorkRequest("태그이름수정");

		Tag modified = Tag.builder().id(1L).group(group).name("태그이름수정").build();

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(new ArrayList<>());
			given(validator.getTagById(1L)).willReturn(tag);
			given(tagRepository.saveAndFlush(tag)).willReturn(modified);

			TagWorkResponse response = assertDoesNotThrow(
					() -> tagService.modify(1L, 1L, tagWorkRequest, "user"));
			assertEquals(1L, response.getId());
			assertEquals("태그이름수정", response.getName());
		}
	}

	@Nested
	@DisplayName("태그 삭제")
	class DeleteTag {
		Long tagId = 1L;

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(new ArrayList<>());
			given(validator.getTagById(tagId)).willReturn(tag);
			given(recordRepository.existsByTagId(tagId)).willReturn(false);
			given(scheduleRepository.existsByTagId(tagId)).willReturn(false);

			MessageResponse response = assertDoesNotThrow(
					() -> tagService.delete(1L, tagId, "user"));
			assertEquals("태그가 삭제되었습니다.", response.getMsg());
		}

		@Test
		@DisplayName("실패 - 태그 달린 일정이 있음")
		void fail_일정이있을때() {
			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(new ArrayList<>());
			given(validator.getTagById(tagId)).willReturn(tag);
			given(recordRepository.existsByTagId(tagId)).willReturn(false);
			given(scheduleRepository.existsByTagId(tagId)).willReturn(true);

			TagException e = assertThrows(TagException.class,
					() -> tagService.delete(1L, tagId, "user"));
			assertEquals(ErrorCode.INVALID_REQUEST, e.getErrorCode());
		}
	}
	@Nested
	@DisplayName("태그 리스트 조회")
	class GetListTag {

		@Test
		@DisplayName("성공")
		void success() {
			List<Tag> tagList = List.of(tag, tag2);

			given(validator.getGroupById(1L)).willReturn(group);
			given(validator.getUserGroupListByUsername(group, "user")).willReturn(new ArrayList<>());
			given(tagRepository.findAllByGroupId(group.getId())).willReturn(tagList);
			TagListResponse response = assertDoesNotThrow(
					() -> tagService.getList(1L, "user"));
			assertEquals(2, response.getTags().size());
			assertEquals("태그이름", response.getTags().get(0));
		}
	}
}