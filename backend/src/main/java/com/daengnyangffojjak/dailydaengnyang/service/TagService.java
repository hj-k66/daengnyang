package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.tag.TagWorkRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.tag.TagWorkResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
import com.daengnyangffojjak.dailydaengnyang.repository.TagRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {
	private final TagRepository tagRepository;
	private final Validator validator;

	public TagWorkResponse create(Long groupId, TagWorkRequest tagWorkRequest, String username) {
		Group group = validator.getGroupById(groupId);		//유저가 그룹에 속해있는 지 확인
		validator.getUserGroupListByUsername(group, username);

		Tag saved = tagRepository.save(tagWorkRequest.toEntity(group));
		return TagWorkResponse.from(saved);
	}

	public TagWorkResponse modify(Long groupId, Long tagId, TagWorkRequest tagWorkRequest, String username) {
		Group group = validator.getGroupById(groupId);		//유저가 그룹에 속해있는 지 확인
		validator.getUserGroupListByUsername(group, username);

		Tag tag = validator.getTagById(tagId);
		tag.modify(tagWorkRequest);
		Tag modified = tagRepository.saveAndFlush(tag);
		return TagWorkResponse.from(modified);
	}
}
