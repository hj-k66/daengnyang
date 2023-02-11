package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.token.TokenInfo;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.token.TokenRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserLoginRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserResponse;
import com.daengnyangffojjak.dailydaengnyang.service.NotificationService;
import com.daengnyangffojjak.dailydaengnyang.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserRestController {

	private final UserService userService;
	private final NotificationService notificationService;

	@PostMapping(value = "/join")       //회원가입
	public ResponseEntity<Response<UserJoinResponse>> join(
			@RequestBody @Valid UserJoinRequest request) {
		UserJoinResponse userJoinResponse = userService.join(request);
		return ResponseEntity.created(
						URI.create("/api/v1/users/" + userJoinResponse.getId()))     //성공 시 상태코드 : 201
				.body(Response.success(userJoinResponse));
	}

//	@CrossOrigin("*")
	@PostMapping("/login")  //로그인
	public Response<UserResponse> login(
			@RequestBody @Valid UserLoginRequest userLoginRequest,
			HttpServletResponse httpServletResponse) {
		TokenInfo tokenInfo = userService.login(userLoginRequest);
		ResponseCookie cookie = tokenInfo.makeCookie();
		//refresh Token은 쿠키로 전송
		httpServletResponse.setHeader("Set-Cookie", cookie.toString());
		//access Token은 body로 전송
		return Response.success(new UserResponse(tokenInfo.getAccessToken()));
	}

	@PostMapping("/logout") //로그아웃
	public Response<MessageResponse> logout(@RequestBody @Valid TokenRequest tokenRequest, @AuthenticationPrincipal UserDetails user){
		MessageResponse messageResponse = userService.logout(tokenRequest);
		notificationService.deleteToken(user.getUsername());
		return Response.success(messageResponse);
	}

	@PostMapping("/new-token")  //토큰 재발급
	public Response<UserResponse> generateNewToken(
			@RequestBody @Valid TokenRequest tokenRequest, HttpServletResponse httpServletResponse) {
		TokenInfo tokenInfo = userService.generateNewToken(tokenRequest);
		ResponseCookie cookie = tokenInfo.makeCookie();
		//refresh Token은 쿠키로 전송
		httpServletResponse.setHeader("Set-Cookie", cookie.toString());
		//access Token은 body로 전송
		return Response.success(new UserResponse(tokenInfo.getAccessToken()));
	}
}
