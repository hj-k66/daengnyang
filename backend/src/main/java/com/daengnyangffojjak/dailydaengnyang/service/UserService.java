package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.token.TokenInfo;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.token.TokenRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserLoginRequest;
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
import org.springframework.util.ObjectUtils;

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
        User saved = userRepository.save(userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword())));

        return UserJoinResponse.from(saved);
    }

    /* 아이디, 이메일 중복 여부 확인 */
    public boolean checkUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }
    public boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }

	public TokenInfo login(UserLoginRequest userLoginRequest) {

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

		return tokenInfo;

	}

	public TokenInfo generateNewToken(TokenRequest tokenRequest) {
		String refreshToken = tokenRequest.getRefreshToken();
		String accessToken = tokenRequest.getAccessToken();

		//1. accessToken에서 userName 가져오기 >> accessToken 유효성도 검사
		Authentication authentication = jwtTokenUtil.getAuthentication(accessToken);

		//2. refresh Token 검증
		boolean isvalidToken = jwtTokenUtil.validateToken(refreshToken);

		//Redis에서 userName이 key인 refreshToken(value)를 가져오기
		String selectedToken = (String) redisTemplate.opsForValue().get(authentication.getName());

		//2-1. 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우 처리
		if(ObjectUtils.isEmpty(refreshToken)) {
			throw new SecurityCustomException(ErrorCode.INVALID_REQUEST);
		}

		//2-2. refreshToken이 유효하지 않거나 입력받은 refreshToken과 redis 저장된게 일치하지 않으면
		// 토큰 탈취되었다고 판단
		// redis에서 refreshToken 삭제하고 예외처리
		if (!isvalidToken || !Objects.equals(selectedToken, refreshToken)) {
			redisTemplate.delete(authentication.getName());
			throw new SecurityCustomException(ErrorCode.INVALID_TOKEN, "재로그인 하세요.");
		}
		//3. 새로운 accessToken, refreshToken 생성
		TokenInfo tokenInfo = jwtTokenUtil.createToken(authentication);

		//4. redis에 새로운 refreshToken 저장
		redisTemplate.opsForValue()
				.set(authentication.getName(), tokenInfo.getRefreshToken(),
						tokenInfo.getRefreshTokenExpireTime(), TimeUnit.MILLISECONDS);
		return tokenInfo;
	}

	public MessageResponse logout(TokenRequest tokenRequest) {
		String accessToken = tokenRequest.getAccessToken();

		//1. AccessToken에서 userName 가져오기 >> accessToken 유효성도 검사
		String userName = jwtTokenUtil.getUserName(accessToken);

		//2. Redis에서 해당 userName(key)으로 저장된 refreshToken(value) 있는지 확인
		if(redisTemplate.opsForValue().get(userName) != null){
			//2-1. 있으면 삭제
			redisTemplate.delete(userName);
		}

		//3. 해당 accessToken 남은 유효시간 가지고 와서 redis BlackList에 저장
		// key : accesstoken, value: logout
		Long expiration = jwtTokenUtil.getExpiration(accessToken);
		log.info("남은 만료 시간: " + expiration);
		redisTemplate.opsForValue().set(accessToken,"logout",expiration,TimeUnit.MILLISECONDS);

		return new MessageResponse("로그아웃이 되었습니다.");

	}
}
