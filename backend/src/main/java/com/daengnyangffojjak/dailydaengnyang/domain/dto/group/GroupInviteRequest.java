package com.daengnyangffojjak.dailydaengnyang.domain.dto.group;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class GroupInviteRequest {

	private String email;
	private String roleInGroup;

}
