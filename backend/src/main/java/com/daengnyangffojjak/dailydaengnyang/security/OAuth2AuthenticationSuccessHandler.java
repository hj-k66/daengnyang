package com.daengnyangffojjak.dailydaengnyang.security;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.token.TokenInfo;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserResponse;
import com.daengnyangffojjak.dailydaengnyang.utils.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
// 리다이렉션 주소 정해지면 사용
//	@Value("${app.oauth2.authorizedRedirectUri}")
//	private String redirectUri;
	private final JwtTokenUtil jwtTokenUtil;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
        if(response.isCommitted()){
            log.debug("Response has already been commited");
            return;
        }
		TokenInfo tokenInfo = jwtTokenUtil.createToken(authentication);
		ResponseCookie cookie = makeCookie(tokenInfo.getRefreshToken());
		//refresh Token은 쿠키로 전송
		response.setHeader("Set-Cookie", cookie.toString());

		ObjectMapper objectMapper = new ObjectMapper();
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getWriter(),
				Response.success(new UserResponse(tokenInfo.getAccessToken())));
	}

	private ResponseCookie makeCookie(String refreshToken) {
		ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
				.maxAge(7 * 24 * 60 * 60) //만료시간 : 7일
				.secure(true)
				.sameSite("None") //
				.httpOnly(true)
				.build();
		return cookie;
	}

}
