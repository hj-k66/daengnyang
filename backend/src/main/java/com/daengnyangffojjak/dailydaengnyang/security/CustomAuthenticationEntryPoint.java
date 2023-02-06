package com.daengnyangffojjak.dailydaengnyang.security;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorResponse;
import com.daengnyangffojjak.dailydaengnyang.exception.SecurityCustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		log.info("인증 실패");

		ObjectMapper objectMapper = new ObjectMapper();
		SecurityCustomException securityCustomException = new SecurityCustomException(
				ErrorCode.INVALID_PERMISSION);
		ErrorResponse errorResponse = new ErrorResponse(securityCustomException.getErrorCode(),
				securityCustomException.getErrorCode().getMessage());
		Response<ErrorResponse> error = Response.error(errorResponse);

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		response.getWriter()
				.write(objectMapper.writeValueAsString(error)); //Response객체를 response의 바디값으로 파싱
	}
}