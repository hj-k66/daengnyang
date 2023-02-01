package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
@RequiredArgsConstructor
public class GroupUiController {

	@PostMapping("/groups")
	public String groupCreate(Model model) {
		return "users/join_group";
	}

}
