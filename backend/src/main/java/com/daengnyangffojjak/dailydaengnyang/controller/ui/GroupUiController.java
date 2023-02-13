package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeRequest;
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
}
