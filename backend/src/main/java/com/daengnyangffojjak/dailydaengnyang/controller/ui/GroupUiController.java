package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupInviteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeRequest;
import com.daengnyangffojjak.dailydaengnyang.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/groups")
@RequiredArgsConstructor
public class GroupUiController {
	private final NotificationService notificationService;

	@GetMapping
	public String groupCreate(Model model) {
		model.addAttribute("groupMakeRequest", new GroupMakeRequest());
		return "users/join_group";
	}

	@GetMapping("/{groupId}/users")
	public String groupList(@PathVariable Long groupId, Model model) {
		model.addAttribute("groupId", groupId);
		return "group/group_list";
	}

	@PostMapping("/{groupId}/invite")
	public void inviteUser(@PathVariable Long groupId, Model model) {
		model.addAttribute("groupInviteRequest", new GroupInviteRequest());
	}

	// 그룹 선택
	@GetMapping("/mygroups")
	public String groupChoice(Model model) {
		notificationService.init();
		return "group/group_choice";
	}

	//태그 관리 페이지
	@GetMapping("/{groupId}/tags")
	public String tagManage (Model model, @PathVariable Long groupId) {
		model.addAttribute("groupId", groupId);
		return "tag/tag";
	}

}
