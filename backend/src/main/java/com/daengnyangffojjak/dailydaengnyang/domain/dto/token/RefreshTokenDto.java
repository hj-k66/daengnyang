package com.daengnyangffojjak.dailydaengnyang.domain.dto.token;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RefreshTokenDto {

	private String refreshToken;
}
