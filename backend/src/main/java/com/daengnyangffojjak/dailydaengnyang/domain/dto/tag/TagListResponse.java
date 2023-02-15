package com.daengnyangffojjak.dailydaengnyang.domain.dto.tag;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TagListResponse {

	private Long id;
	private String name;

	public static TagListResponse from(Tag tag) {
		return new TagListResponse(tag.getId(), tag.getName());
	}
}