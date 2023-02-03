package com.daengnyangffojjak.dailydaengnyang.domain.dto.record;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Category;
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

	private String title;
	private String body;
	private Boolean isPublic;
	private Category category;

	public Record toEntity(User user, Pet pet) {
		return Record.builder()
				.user(user)
				.pet(pet)
				.title(title)
				.body(body)
				.isPublic(isPublic)
				.category(category)
				.build();
	}
}
