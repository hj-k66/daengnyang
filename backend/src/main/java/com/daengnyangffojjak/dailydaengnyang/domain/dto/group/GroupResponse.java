package com.daengnyangffojjak.dailydaengnyang.domain.dto.group;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponse {
	private Long id;
	private String name;
	private String ownerName;
	private Long ownerId;

	public static GroupResponse from(Group group) {
		return GroupResponse.builder()
				.id(group.getId())
				.name(group.getName())
				.ownerName(group.getUser().getUsername())
				.ownerId(group.getUser().getId())
				.build();
	}
}
