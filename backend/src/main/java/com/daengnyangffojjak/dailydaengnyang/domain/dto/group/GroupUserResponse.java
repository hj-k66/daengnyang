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
public class GroupUserResponse {

	private Long id;		// 유저의 등록번호
	private String userName;
	private String roleInGroup;

	public static GroupUserResponse from(UserGroup userGroup) {
		return GroupUserResponse.builder()
				.id(userGroup.getUser().getId())
				.userName(userGroup.getUser().getUsername())
				.roleInGroup(userGroup.getRoleInGroup())
				.build();
	}
}
