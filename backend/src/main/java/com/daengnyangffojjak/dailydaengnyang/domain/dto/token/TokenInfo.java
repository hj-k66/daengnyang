package com.daengnyangffojjak.dailydaengnyang.domain.dto.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EqualsAndHashCode
public class TokenInfo {

	private String accessToken;
	private String refreshToken;
	private long refreshTokenExpireTime;
}
