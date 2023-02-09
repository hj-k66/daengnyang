package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetShowResponse;
import com.daengnyangffojjak.dailydaengnyang.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
	private final PetService petService;
	@GetMapping("/pets")
	public String petCreate(Model model) {
		model.addAttribute("petAddRequest", new PetAddRequest());
		return "users/join_pet";
	}
	@PostMapping("/groups/{groupId}/pets")
	public String petRegistration(@PathVariable Long groupId, Model model) {
		return "users/join_pet";
	}

//	@GetMapping("/groups/{groupId}/pets/{petId}")
//	public String petManage(@PathVariable Long groupId, @PathVariable Long petId,
//			Model model) {
//		model.addAttribute("groupId", groupId);
//		model.addAttribute("petId", petId);
//		return "pet/pet_manage";
//	}

	@GetMapping("/groups/{groupId}/pets")
	public String petList(@PathVariable Long groupId, Model model) {
		model.addAttribute("groupId", groupId);
		return "pet/pet_list";
	}
}
