package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.token.RefreshTokenDto;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.token.TokenInfo;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserLoginRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserLoginResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.SecurityCustomException;
import com.daengnyangffojjak.dailydaengnyang.exception.UserException;
import com.daengnyangffojjak.dailydaengnyang.repository.UserRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.JwtTokenUtil;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder encoder;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final JwtTokenUtil jwtTokenUtil;
	private final RedisTemplate redisTemplate;


	public UserJoinResponse join(UserJoinRequest userJoinRequest) {
		//아이디 중복 시 예외 발생
		userRepository.findByUserName(userJoinRequest.getUserName())
				.ifPresent(user -> {
					throw new UserException(ErrorCode.DUPLICATED_USER_NAME);
				});
		String email = userJoinRequest.getEmail();
		if (!email.contains("@") || !email.contains(".")) {     //이메일 형식 체크
			throw new UserException(ErrorCode.INVALID_EMAIL, "이메일 형식이 바르지 않습니다.");
		}
		//이메일 중복 시 예외 발생
		userRepository.findByEmail(email)
				.ifPresent(user -> {
					throw new UserException(ErrorCode.DUPLICATED_EMAIL);
				});

		//비밀 번호 인코딩해서 DB 저장
		String encodedPassword = encoder.encode(userJoinRequest.getPassword());
		User saved = userRepository.save(userJoinRequest.toEntity(encodedPassword));
		return UserJoinResponse.from(saved);
	}

	public UserLoginResponse login(UserLoginRequest userLoginRequest) {

		//1. 해당 user 있는지 검증
		User user = userRepository.findByUserName(userLoginRequest.getUserName())
				.orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND));

		//2. id,pw 기반으로 Authentication 객체 생성
		UsernamePasswordAuthenticationToken authenticationToken = userLoginRequest.toAuthentication();
		log.info((String) authenticationToken.getPrincipal());

		//3. 실제 인증
		//DaoAuthenticationProvider class 내 additionalAuthenticationChecks() 메소드로 비밀번호 체크
		Authentication authentication = authenticationManagerBuilder.getObject()
				.authenticate(authenticationToken);
		log.info("authentication.getName:" + authentication.getName());
		log.info("authentication getAuthorities" + authentication.getAuthorities());

		//4.인증 정보 기반으로 JWT 토큰 생성 >> refresh, access token 둘 다 생성
		TokenInfo tokenInfo = jwtTokenUtil.createToken(authentication);
		//5.RefreshToken Redis에 저장
		redisTemplate.opsForValue()
				.set(authentication.getName(), tokenInfo.getRefreshToken(),
						tokenInfo.getRefreshTokenExpireTime(), TimeUnit.MILLISECONDS);

		return new UserLoginResponse(tokenInfo);

	}

	public TokenInfo generateNewToken(RefreshTokenDto refreshTokenDto) {
		String refreshToken = refreshTokenDto.getRefreshToken();
		//1. refresh Token 검증 >> 예외처리
		jwtTokenUtil.validateToken(refreshToken);

		//2. refreshToken에서 userName 가져오기
		Authentication authentication = jwtTokenUtil.getAuthentication(refreshToken);

		//3. Redis에서 userName이 key인 refreshToken(value)를 가져오기
		//입력받은 refreshToken과 일치하는지 체크
		String selectedToken = (String) redisTemplate.opsForValue().get(authentication.getName());
		if (!Objects.equals(selectedToken, refreshToken)) {
			throw new SecurityCustomException(ErrorCode.INVALID_TOKEN, "해당 refresh token이 아닙니다.");
		}
		//4. 새로운 accessToken, refreshToken 생성
		TokenInfo tokenInfo = jwtTokenUtil.createToken(authentication);

		//5. redis에 새로운 refreshToken 저장
		redisTemplate.opsForValue()
				.set(authentication.getName(), tokenInfo.getRefreshToken(),
						tokenInfo.getRefreshTokenExpireTime(), TimeUnit.MILLISECONDS);
		return tokenInfo;
	}
}
