package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
@RequiredArgsConstructor
public class ScheduleUiController {

	@GetMapping("/schedules/list")
	public String scheduleList(Model model) { // @PageableDefault(size = 3) -> 한 페이지에 보여지는 게시글 수

		return "schedule/schedule_list";
	}

//	@GetMapping("/detail")
//	public String detail(Model model) {
////        model.addAttribute("petId", petId);
////        model.addAttribute("recordId", recordId);
//
//		return "schedule/schedule_list";
//	}
//
//	@GetMapping("/add")
//	public String add(Model model) {
//		model.addAttribute("recordWorkRequest", new RecordWorkRequest());
//
//		return "schedule/schedule_list";
//	}
//
//	@PostMapping("/add")
//	public String add(Long petId, Model model) {
//		return "schedule/schedule_list";
//	}
//
//	@GetMapping("/modify")
//	public String modify() {
//		return "schedule/schedule_list";
//	}
//
//	@GetMapping("/delete")
//	public String deleteBoard() {
//		return "schedule/schedule_list";
//	}

}
