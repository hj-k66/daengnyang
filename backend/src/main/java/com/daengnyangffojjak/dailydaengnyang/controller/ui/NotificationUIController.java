package com.daengnyangffojjak.dailydaengnyang.controller.ui;

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

	@GetMapping
	public String getNotificationList(@RequestParam Long lastNotificationId, @RequestParam int size, Model model) {
		model.addAttribute("lastNotificationId", lastNotificationId);
		model.addAttribute("size", size);
		return "notification/notification_list";
	}
}
