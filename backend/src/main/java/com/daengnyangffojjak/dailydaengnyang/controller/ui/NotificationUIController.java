package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import com.daengnyangffojjak.dailydaengnyang.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/view/notification")
@RequiredArgsConstructor
public class NotificationUIController {
	private final NotificationService notificationService;


	@GetMapping
	public String getNotificationList(@RequestParam(required = false) Long lastNotificationId, @RequestParam(required = false) Integer size, Model model) {
		model.addAttribute("lastNotificationId", lastNotificationId);
		model.addAttribute("size", size);
		return "notification/notification_list";
	}

	@GetMapping("/test")
	public String test() {
		notificationService.init();
		return "notification/test";
	}
}
