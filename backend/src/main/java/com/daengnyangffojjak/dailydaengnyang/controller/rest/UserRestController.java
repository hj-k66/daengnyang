package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.token.TokenInfo;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.token.TokenRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserLoginRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserLoginResponse;
import com.daengnyangffojjak.dailydaengnyang.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserRestController {

	private final UserService userService;

	@PostMapping(value = "/join")       //회원가입
	public ResponseEntity<Response<UserJoinResponse>> join(
			@RequestBody @Valid UserJoinRequest request) {
		UserJoinResponse userJoinResponse = userService.join(request);
		return ResponseEntity.created(
						URI.create("/api/v1/users/" + userJoinResponse.getId()))     //성공 시 상태코드 : 201
				.body(Response.success(userJoinResponse));
	}

	@PostMapping("/login")  //로그인
	public Response<UserLoginResponse> login(
			@RequestBody @Valid UserLoginRequest userLoginRequest) {
		UserLoginResponse userloginResponse = userService.login(userLoginRequest);
		return Response.success(userloginResponse);
	}

	@PostMapping("/new-token")  //토큰 재발급
	public Response<TokenInfo> generateNewToken(
			@RequestBody @Valid TokenRequest tokenRequest) {
		TokenInfo tokenInfo = userService.generateNewToken(tokenRequest);
		return Response.success(tokenInfo);
	}

	@GetMapping(value = "/test")
	public Map<String, String> test() {
		return new HashMap<>() {{
			put("test", "ok");
		}};
	}
}
