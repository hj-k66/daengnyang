package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
@RequiredArgsConstructor
public class PetUiController {

	@PostMapping("/groups/{groupId}/pets")
	public String petRegistration(@PathVariable Long groupId) {
		return "users/join_pet";
	}

	@GetMapping("/pet/{petId}")
	public String petManage(@PathVariable Long petId) {
		return "pet/pet_manage";
	}
}
