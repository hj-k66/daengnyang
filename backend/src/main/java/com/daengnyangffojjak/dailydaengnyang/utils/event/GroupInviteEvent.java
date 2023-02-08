package com.daengnyangffojjak.dailydaengnyang.utils.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GroupInviteEvent {

	private final String receiverName;
	private final String title;
	private final String senderName;
}
