package com.daengnyangffojjak.dailydaengnyang.domain.dto.tag;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagWorkResponse {
	private Long id;
	private String name;

	public static TagWorkResponse from(Tag tag){
		return TagWorkResponse.builder()
				.id(tag.getId())
				.name(tag.getName())
				.build();
	}
}
