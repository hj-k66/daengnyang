package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserLoginRequest;
import com.daengnyangffojjak.dailydaengnyang.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/view/users")
@RequiredArgsConstructor
@Slf4j
public class UserUiController {

	private final UserService userService;


	@GetMapping("/join")
	public String join(Model model) {
		model.addAttribute("userJoinRequest", new UserJoinRequest());
		return "users/join";
	}

	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("userLoginRequest", new UserLoginRequest());
		return "users/login";
	}

	/* 아이디, 이메일 중복 여부 확인 */
	@ResponseBody
	@GetMapping("/check-userName")
	public boolean checkUserName(@RequestParam String userName) {
		return userService.checkUserName(userName);
	}

	@ResponseBody
	@GetMapping("/check-email")
	public boolean checkEmail(@RequestParam String email) {
		return userService.checkEmail(email);
	}
}
