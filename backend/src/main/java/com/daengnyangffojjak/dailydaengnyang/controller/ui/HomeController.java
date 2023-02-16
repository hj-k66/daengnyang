package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

	//home
	@GetMapping
	public String home() {
		return "record/record_list";
	}
}
