package com.daengnyangffojjak.dailydaengnyang.domain.dto.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EqualsAndHashCode
public class TokenInfo {

	private String accessToken;
	private String refreshToken;
	private long refreshTokenExpireTime;

	public ResponseCookie makeCookie() {
		ResponseCookie cookie = ResponseCookie.from("refreshToken", this.refreshToken)
				.maxAge(7 * 24 * 60 * 60) //만료시간 : 7일
				.secure(true)
				.path("/")
				.sameSite("None") //
				.httpOnly(true)
				.build();
		return cookie;
	}
}
