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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/view")
@RequiredArgsConstructor
public class RecordController {

	private final RecordService recordService;

	@GetMapping("/list")
	public String list(Model model,
			@PageableDefault(size = 3) Pageable pageable) { // @PageableDefault(size = 3) -> 한 페이지에 보여지는 게시글 수
		Page<RecordResponse> recordList = recordService.getAllRecords(pageable);
		model.addAttribute("recordList", recordList);

		// 페이징 처리
		int startPage = Math.max(1, recordList.getPageable().getPageNumber() - 5);
		int endPage = Math.min(recordList.getTotalPages(),
				recordList.getPageable().getPageNumber() + 5);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);

		return "record/record_list";
	}

	@GetMapping("/detail")
	public String detail(Model model) {
//        model.addAttribute("petId", petId);
//        model.addAttribute("recordId", recordId);

		return "record/record_detail";
	}

	@GetMapping("/add")
	public String add(Model model) {
		model.addAttribute("recordWorkRequest", new RecordWorkRequest());

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
