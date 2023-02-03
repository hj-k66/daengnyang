package com.daengnyangffojjak.dailydaengnyang.domain.dto.tag;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TagWorkRequest {
	@NotBlank(message = "태그이름을 2~10자 사이로 입력해주세요")
	@Size(min = 2, max = 10, message = "태그이름을 2~10자 사이로 입력해주세요")
	private String name;

	public Tag toEntity(Group group) {
		return Tag.builder()
				.name(this.name)
				.group(group)
				.build();
	}
}
