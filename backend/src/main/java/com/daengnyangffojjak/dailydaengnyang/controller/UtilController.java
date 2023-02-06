package com.daengnyangffojjak.dailydaengnyang.controller;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/utils")
@Slf4j
public class UtilController {

	private final Environment env;

	@GetMapping("/profile")
	public String getProfile() {
//		System.out.println("get success");
//		String[] activeProfiles = env.getActiveProfiles();
//		log.info("activeProfiles : " + activeProfiles);
//		return Arrays.stream(activeProfiles).findFirst().orElse("");
		return "get success";
	}
}