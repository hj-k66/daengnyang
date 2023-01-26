package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserLoginRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserLoginResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.UserException;
import com.daengnyangffojjak.dailydaengnyang.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    public UserJoinResponse join(UserJoinRequest userJoinRequest) {
        //아이디 중복 시 예외 발생
        userRepository.findByUserName(userJoinRequest.getUserName())
                .ifPresent(user -> {throw new UserException(ErrorCode.DUPLICATED_USER_NAME);});
        String email = userJoinRequest.getEmail();
        if(!email.contains("@") || !email.contains(".")){     //이메일 형식 체크
            throw new UserException(ErrorCode.INVALID_EMAIL, "이메일 형식이 바르지 않습니다.");
        }
        //이메일 중복 시 예외 발생
        userRepository.findByEmail(email)
                .ifPresent(user -> {throw new UserException(ErrorCode.DUPLICATED_EMAIL);});

        //비밀 번호 인코딩해서 DB 저장
        User saved = userRepository.save(userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword())));

        return UserJoinResponse.from(saved);
    }

    public UserLoginResponse login(UserLoginRequest userLoginRequest){
        //1. 해당 user 있는지 검증
        User user = userRepository.findByUserName(userLoginRequest.getUserName())
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND));

        //2. id,pw 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = userLoginRequest.toAuthentication();

        //3. 실제 인증
        //DaoAuthenticationProvider class 내 additionalAuthenticationChecks() 메소드로 비밀번호 체크
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //4.인증 정보 기반으로 JWT 토큰 생성
        //5.RefreshToken Redis에 저장

    }
    /*
     String userName = userLoginRequest.getUserName();
        //해당 userName이 없는 경우
        User user = validator.validateUser(userName);
        //해당 password가 틀린 경우
        if(!encoder.matches(userLoginRequest.getPassword(),user.getPassword())){
            throw new SNSException(ErrorCode.INVALID_PASSWORD);
        }
        return JwtTokenUtil.createToken(user.getUserName(),secretKey,expireTimeMs);

     */
}
