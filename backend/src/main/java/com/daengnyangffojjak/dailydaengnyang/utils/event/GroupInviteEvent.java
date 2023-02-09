package com.daengnyangffojjak.dailydaengnyang.utils.event;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GroupInviteEvent {

	private final User receiver;
	private final String title;
	private final String senderName;
}
