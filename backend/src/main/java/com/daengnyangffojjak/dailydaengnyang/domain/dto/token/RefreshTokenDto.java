package com.daengnyangffojjak.dailydaengnyang.domain.dto.token;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RefreshTokenDto {

	@NotEmpty(message = "refreshToken은 필수 입력값입니다.")
	private String refreshToken;
}
