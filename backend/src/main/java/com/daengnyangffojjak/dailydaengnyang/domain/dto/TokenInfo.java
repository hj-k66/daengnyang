package com.daengnyangffojjak.dailydaengnyang.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TokenInfo {

	private String accessToken;
	private String refreshToken;
	private long refreshTokenExpireTime;
}
