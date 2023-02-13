package com.daengnyangffojjak.dailydaengnyang.domain.dto.group;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.UserGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupListResponse {
	private Long id;		//그룹 번호
	private String name;	//그룹 이름
	private String roleInGroup;	//나의 그룹에서 역할

	public static GroupListResponse from(UserGroup userGroup) {
		return GroupListResponse.builder()
				.id(userGroup.getGroup().getId())
				.name(userGroup.getGroup().getName())
				.roleInGroup(userGroup.getRoleInGroup())
				.build();
	}
}
