package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleCreateRequest;
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
public class ScheduleUiController {

	@GetMapping("/pets/{petId}/schedule")
	public String scheduleList(@PathVariable Long petId, Model model) { // @PageableDefault(size = 3) -> 한 페이지에 보여지는 게시글 수

		return "schedule/schedule_list";
	}

	@GetMapping("/pets/{petId}/schedules")
	public String scheduleCreate(@PathVariable Long petId, Model model) {
		model.addAttribute("scheduleCreateRequest", new ScheduleCreateRequest());
		return "schedule/schedule_add";
	}
	@PostMapping("/create")
	public String petRegistration(@PathVariable Long petId, Model model) {
		return "schedule/schedule_list";
	}

}
