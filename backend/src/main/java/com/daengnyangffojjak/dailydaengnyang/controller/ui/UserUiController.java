package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinRequest;
import com.daengnyangffojjak.dailydaengnyang.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/view/users")
@RequiredArgsConstructor
@Slf4j
public class UserUiController {

	private final UserService userService;

	@GetMapping("/login")
	public String login() {
		return "users/login";
	}

	@GetMapping("/join")
	public String join(Model model) {
		model.addAttribute("userJoinRequest", new UserJoinRequest());
		return "users/join";
	}

	@PostMapping("/join")
	public String join(@Valid UserJoinRequest userJoinRequest, BindingResult result) {

		if (result.hasErrors()) {
			return "users/join";
		}

		userService.join(userJoinRequest);
		return "users/join_group";
	}

	/* 아이디, 이메일 중복 여부 확인 */
	@ResponseBody
	@GetMapping("/check-userName")
	public Boolean checkUserName(@RequestParam String userName) {
		return userService.checkUserName(userName);
	}
	@ResponseBody
	@GetMapping ("/check-email")
	public Boolean checkEmail(@RequestParam String email) {
		log.info("email: ", email);
		return userService.checkEmail(email);
	}
}
