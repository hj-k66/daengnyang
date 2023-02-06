package com.daengnyangffojjak.dailydaengnyang.security;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserRole;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.repository.UserRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest); //OAuth 서비스(google..)에서 가져온 유저 정보
		log.info("oAuth2User : {}", oAuth2User.toString());
		Map<String, Object> attributes = oAuth2User.getAttributes();   //유저 정보 Map에 담음
		log.info("attribue : {}", attributes.toString());

		String registrationId = userRequest.getClientRegistration().getRegistrationId(); //사용한 OAuth 서비스 이름
		//OAuth 서비스에 따라 유저정보를 공통된 class인 UserProfile 객체로 만들어 준다.
		Map<String, Object> customAttributes = OAuthAttribute.getAttribute(registrationId, attributes);
		User user = User.builder()
				.userName((String) customAttributes.get("oauthId"))
				.role(UserRole.ROLE_USER)
				.attributes(customAttributes)
				.build();
		log.info("userName : {}", user.getName());

		User savedUser = saveOrUpdate(user);      //DB에 저장

		/**리팩토링할 때 쓸 거**/
        return user;
	}
	private User saveOrUpdate(User user){
		String userName = user.getUsername();
		User updated = userRepository.findByUserName(userName)
				.map(m -> m.update(userName, (String) user.getAttributes().get("email"))) //OAuth 서비스 유저정보 변경이 있으면 업데이트
				.orElse(user);          //user가 없으면 새로운 user 생성
		return userRepository.save(updated);
	}

}
