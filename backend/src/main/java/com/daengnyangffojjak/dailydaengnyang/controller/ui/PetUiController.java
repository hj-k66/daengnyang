package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest;
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
public class PetUiController {

	@GetMapping("/pets")
	public String petCreate(Model model) {
		model.addAttribute("petAddRequest", new PetAddRequest());
		return "/users/join_pet";
	}

	@PostMapping("/groups/{groupId}/pets")
	public String petRegistration(@PathVariable Long groupId, Model model) {
		return "users/join_pet";
	}

	@GetMapping("/pet/{petId}")
	public String petManage(@PathVariable Long petId, Model model) {
		return "pet/pet_manage";
	}
}
