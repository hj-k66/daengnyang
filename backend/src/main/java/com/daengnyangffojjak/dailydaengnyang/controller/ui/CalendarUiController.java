package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinRequest;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
@RequiredArgsConstructor
@Slf4j
public class CalendarUiController {

	@GetMapping("/groups/{groupId}/calendar")
	public String calendar(Model model, @PathVariable Long groupId) {
		model.addAttribute("groupId", groupId);
		log.info("캘린더: ", model);
		return "calendar/calendar";
	}
}
