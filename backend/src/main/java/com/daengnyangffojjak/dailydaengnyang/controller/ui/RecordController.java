package com.daengnyangffojjak.dailydaengnyang.controller.ui;


import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordWorkRequest;
import com.daengnyangffojjak.dailydaengnyang.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

	@GetMapping("/pets/{petId}/records")
	public String petList (@PathVariable Long petId, Model model) {
		return "record/record_list_pet";
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

	@PostMapping("/add")
	public String add(Long petId, Model model) {
		return "record/record_list";
	}

	@GetMapping("/modify")
	public String modify() {
		return "record/record_modify";
	}

	@GetMapping("/delete")
	public String deleteBoard() {
		return "redirect:/record_list";
	}

	// 알람 드롭다운
	@GetMapping("/alarm")
	public String alarm() {
		return "record/alarm";
	}

}
