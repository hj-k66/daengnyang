package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.UserException;
import com.daengnyangffojjak.dailydaengnyang.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;


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

    /* 아이디, 이메일 중복 여부 확인 */
    public boolean checkUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }
    public boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
