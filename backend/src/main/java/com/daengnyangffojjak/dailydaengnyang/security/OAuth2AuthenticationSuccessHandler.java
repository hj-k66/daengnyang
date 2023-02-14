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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	@Value("${oauth2.redirect.url}")
	private String redirectUri;
	private final JwtTokenUtil jwtTokenUtil;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
        if(response.isCommitted()){
            log.debug("Response has already been commited");
            return;
        }
		TokenInfo tokenInfo = jwtTokenUtil.createToken(authentication);
		ResponseCookie cookie = tokenInfo.makeCookie();
		//refresh Token은 쿠키로 전송
		response.setHeader("Set-Cookie", cookie.toString());

		response.sendRedirect(getRedirectionURI(tokenInfo.getAccessToken()));

	}

	private String getRedirectionURI(String token) {
		return UriComponentsBuilder.fromUriString(redirectUri)
				.queryParam("token", token)
				.build()
				.toUriString();
	}
}
