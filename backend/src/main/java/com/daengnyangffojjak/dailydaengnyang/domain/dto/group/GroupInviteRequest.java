package com.daengnyangffojjak.dailydaengnyang.domain.dto.group;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class GroupInviteRequest {

	private String email;
	private String roleInGroup;

}
