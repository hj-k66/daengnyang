package com.daengnyangffojjak.dailydaengnyang.domain.dto.tag;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TagListResponse {

	private List<String> tags;

	public static TagListResponse from(List<Tag> tags) {
		return new TagListResponse(tags.stream().map(Tag::getName).toList());
	}
}