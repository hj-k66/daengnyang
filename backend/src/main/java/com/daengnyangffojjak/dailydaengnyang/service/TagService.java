package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.tag.TagListResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.tag.TagWorkRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.tag.TagWorkResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.TagException;
import com.daengnyangffojjak.dailydaengnyang.repository.RecordRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.ScheduleRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.TagRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagService {

	private final TagRepository tagRepository;
	private final ScheduleRepository scheduleRepository;
	private final RecordRepository recordRepository;
	private final Validator validator;

	@Transactional
	public TagWorkResponse create(Long groupId, TagWorkRequest tagWorkRequest, String username) {
		Group group = validator.getGroupById(groupId);        //유저가 그룹에 속해있는 지 확인
		validator.getUserGroupListByUsername(group, username);

		Tag saved = tagRepository.save(tagWorkRequest.toEntity(group));
		return TagWorkResponse.from(saved);
	}
	@Transactional
	public TagWorkResponse modify(Long groupId, Long tagId, TagWorkRequest tagWorkRequest,
			String username) {
		Group group = validator.getGroupById(groupId);        //유저가 그룹에 속해있는 지 확인
		validator.getUserGroupListByUsername(group, username);

		Tag tag = validator.getTagById(tagId);
		tag.modify(tagWorkRequest);
		Tag modified = tagRepository.saveAndFlush(tag);
		return TagWorkResponse.from(modified);
	}
	@Transactional
	public MessageResponse delete(Long groupId, Long tagId, String username) {
		Group group = validator.getGroupById(groupId);        //유저가 그룹에 속해있는 지 확인
		validator.getUserGroupListByUsername(group, username);

		Tag tag = validator.getTagById(tagId);
		//태그가 포함된 일기나 일정이 있으면 삭제 불가능
		if (recordRepository.existsByTagId(tagId) || scheduleRepository.existsByTagId(tagId)) {
			throw new TagException(ErrorCode.INVALID_REQUEST, "태그를 포함한 컨텐츠가 있어 삭제할 수 없습니다.");
		}
		tagRepository.delete(tag);
		return new MessageResponse("태그가 삭제되었습니다.");
	}
	@Transactional(readOnly = true)
	public TagListResponse getList(Long groupId, String username) {
		Group group = validator.getGroupById(groupId);        //유저가 그룹에 속해있는 지 확인
		validator.getUserGroupListByUsername(group, username);

		List<Tag> tags = tagRepository.findAllByGroupId(group.getId());
		return TagListResponse.from(tags);
	}
}
