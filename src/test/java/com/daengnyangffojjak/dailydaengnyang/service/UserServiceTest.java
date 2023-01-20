package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.UserException;
import com.daengnyangffojjak.dailydaengnyang.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


class UserServiceTest {
    private UserService userService;
    private final UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, mock(BCryptPasswordEncoder.class));
    }

    @Nested
    @DisplayName("회원가입")
    class ServiceJoin{
        @Test
        @DisplayName("성공")
        void success(){

            given(userRepository.findByUserName("user")) .willReturn(Optional.empty());
            given(userRepository.findByEmail("g@gmail.com")) .willReturn(Optional.empty());
            given(userRepository.save(any()))
                    .willReturn(User.builder().id(1L).userName("user").email("g@gmail.com").build());

            //when
            UserJoinResponse response = assertDoesNotThrow(
                    () -> userService.join(new UserJoinRequest("user", "password", "g@gmail.com")));
            //then
            assertEquals("user", response.getUserName());
            assertEquals("g@gmail.com", response.getEmail());

        }

        @Test
        @DisplayName("잘못된 이메일 형식 체크")
        void fail_invalid_email(){
            given(userRepository.findByUserName("user")) .willReturn(Optional.empty());

            //when
            UserException e = assertThrows(UserException.class,
                    () -> userService.join(new UserJoinRequest("user", "password", "잘못된이메일")));
            //then
            assertEquals(ErrorCode.INVALID_VALUE, e.getErrorCode());
        }
    }
}