package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/view")
@RequiredArgsConstructor
public class MonitoringUiController {
	@GetMapping("/pets/{petId}/monitorings/report")
	public String showReport(@PathVariable Long petId, @RequestParam String fromDate, @RequestParam String toDate, Model model) {
		model.addAttribute("petId", petId);
		model.addAttribute("fromDate", fromDate);
		model.addAttribute("toDate", toDate);
		return "monitoring/monitoring_report";
	}

	@GetMapping("/pets/{petId}/monitorings")
	public String showMonitoringList(@PathVariable Long petId, Model model) {
		model.addAttribute("petId", petId);
		return "monitoring/monitoring_list";
	}
}
