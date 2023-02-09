package com.daengnyangffojjak.dailydaengnyang.security;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OAuthAttribute {
	//OAuth 서비스에 따라 얻어온 유저 정보의 key값이 다르기 때문에 각각 관리한다.
	GOOGLE("google", (attributes) -> {
		return Map.of(
				"oauthId", String.valueOf(attributes.get("sub")),
				"name", (String) attributes.get("name"),
				"email", (String) attributes.get("email"));

	}),
	NAVER("naver", (attributes) -> {
		Map<String, String> specificAttributes = (Map<String, String>) attributes.get("response");
		return Map.of(
				"oauthId", specificAttributes.get("id"),
				"name", specificAttributes.get("name"),
				"email", specificAttributes.get("email"));
	});
	private final String registrationId;
	private final Function<Map<String, Object>, Map<String, Object>> setAttributes;

	//OAuth 서비스의 정보를 통해 Attribute을 얻는다.
	public static Map<String, Object> getAttribute(String registrationId,
			Map<String, Object> attributes) {
		return Arrays.stream(values())
				.filter(provider -> registrationId.equals(provider.registrationId))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new)
				.setAttributes.apply(attributes);
	}
}
