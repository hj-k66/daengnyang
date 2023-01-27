package com.daengnyangffojjak.dailydaengnyang.security;

import com.daengnyangffojjak.dailydaengnyang.utils.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

	private final JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		//권한 여부 결정
		//권한 부여 X case
		//1. Token X —> Request할 때 Token을 안넣고 호출하는 경우
		//2. 만료된 Token일 경우
		//3. 적절하지 않은 Token일 경우

		final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		log.info("authorizationHeader:{}", authorizationHeader);

		String token;
		try {
			token = authorizationHeader.split(" ")[1];
		} catch (Exception e) {
			log.error("token 추출에 실패했습니다.");
			filterChain.doFilter(request, response);
			return;
		}

		if (authorizationHeader.startsWith("Bearer ")) {
			UserDetails userDetails = jwtTokenUtil.getUserDetails(token);
			//문열어주기 >> 허용
			//Role 바인딩
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities());
			authenticationToken.setDetails(
					new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		} else {
			log.error("헤더를 가져오는 과정에서 에러가 났습니다. 헤더가 null이거나 잘못되었습니다.");
		}
		filterChain.doFilter(request, response);
	}


}