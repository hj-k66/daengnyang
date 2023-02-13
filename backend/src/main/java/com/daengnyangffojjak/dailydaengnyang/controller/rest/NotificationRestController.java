package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationRestController {

	private final NotificationService notificationService;

	@PostMapping
	public ResponseEntity register(@RequestBody String token, @AuthenticationPrincipal UserDetails user){
		notificationService.registerFCMToken(user.getUsername(),token);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/test")
	public String test(){
		notificationService.init();
		return "test";
	}

}
