package com.daengnyangffojjak.dailydaengnyang.domain.dto.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
public class UserLoginRequest {

	@NotEmpty(message = "userName은 필수 입력값입니다.")
	private String userName;
	@NotEmpty(message = "비밀번호는 필수 입력값입니다.")
	private String password;

	public UsernamePasswordAuthenticationToken toAuthentication() {
		return new UsernamePasswordAuthenticationToken(userName, password);
	}
}
