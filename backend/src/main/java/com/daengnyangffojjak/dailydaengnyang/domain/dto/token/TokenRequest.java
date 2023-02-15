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
public class TokenRequest {
	@NotEmpty(message = "accessToken은 필수 입력값입니다.")
	private String accessToken;
	private String refreshToken;

	//TokenRequest 파싱하기
	//"refreshToken=" 제거
	public String parseRefreshToken(){
		return this.refreshToken.substring(13);
	}
}
