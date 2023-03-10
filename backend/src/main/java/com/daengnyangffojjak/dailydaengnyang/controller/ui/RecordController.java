package com.daengnyangffojjak.dailydaengnyang.controller.ui;


import com.daengnyangffojjak.dailydaengnyang.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/view")
@RequiredArgsConstructor
public class RecordController {

	private final RecordService recordService;

	@GetMapping("/records/feed")
	public String list (Model model) {
		return "record/record_list";
	}

	// 반려동물별 일기 리스트 조회
	@GetMapping("/pets/{petId}/petRecords")
	public String petRecords(Model model, @PathVariable Long petId) {
		model.addAttribute("petId", petId);
		return "record/record_pet";
	}

	@GetMapping("/pets/{petId}/records/{recordId}")
	public String detail(Model model, @PathVariable Long petId, @PathVariable Long recordId) {
        model.addAttribute("petId", petId);
        model.addAttribute("recordId", recordId);
		return "record/record_detail";
	}

	@GetMapping("/pets/{petId}/records/create")
	public String add(Model model, @PathVariable Long petId) {
		model.addAttribute("petId", petId);
		return "record/record_add";
	}

	// 알람 드롭다운
	@GetMapping("/alarm")
	public String alarm() {
		return "record/alarm";
	}

}
