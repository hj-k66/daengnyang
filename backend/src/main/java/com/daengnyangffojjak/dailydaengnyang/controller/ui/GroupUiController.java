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
@RequestMapping("/view")
@RequiredArgsConstructor
public class GroupUiController {

	@GetMapping("/groups")
	public String groupCreate(Model model) {
		model.addAttribute("groupMakeRequest", new GroupMakeRequest());
		return "users/join_group";
	}

	// 그룹 선택
	@GetMapping("/groups/choice")
	public String groupChoice(Model model) {
		model.addAttribute("groupMakeRequest", new GroupMakeRequest());
		return "group/group_choice";
	}

	//태그 관리 페이지
	@GetMapping("/groups/{groupId}/tags")
	public String tagManage (Model model, @PathVariable Long groupId) {
		model.addAttribute("groupId", groupId);
		return "tag/tag";
	}

}
