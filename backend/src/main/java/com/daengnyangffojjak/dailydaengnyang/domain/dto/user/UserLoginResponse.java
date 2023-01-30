package com.daengnyangffojjak.dailydaengnyang.domain.dto.user;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.TokenInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResponse {

	private TokenInfo tokenInfo;
}
