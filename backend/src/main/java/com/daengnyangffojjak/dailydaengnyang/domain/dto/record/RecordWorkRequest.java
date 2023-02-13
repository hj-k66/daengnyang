package com.daengnyangffojjak.dailydaengnyang.domain.dto.record;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RecordWorkRequest {

	private Long tagId;
	private String title;
	private String body;
	private Boolean isPublic;

	public Record toEntity(User user, Pet pet, Tag tag) {
		return Record.builder()
				.user(user)
				.pet(pet)
				.tag(tag)
				.title(title)
				.body(body)
				.isPublic(isPublic)
				.build();
	}
}
