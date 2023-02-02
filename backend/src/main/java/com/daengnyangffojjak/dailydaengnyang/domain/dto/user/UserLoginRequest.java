package com.daengnyangffojjak.dailydaengnyang.domain.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserLoginRequest {

	@NotEmpty(message = "userName은 필수 입력값입니다.")
	@NotNull
	private String userName;
	@NotEmpty(message = "비밀번호는 필수 입력값입니다.")
	@NotNull
	private String password;

	public UsernamePasswordAuthenticationToken toAuthentication() {
		return new UsernamePasswordAuthenticationToken(userName, password);
	}
}
