package com.daengnyangffojjak.dailydaengnyang.domain.dto.user;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.token.TokenInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResponse {

	private TokenInfo tokenInfo;
}
