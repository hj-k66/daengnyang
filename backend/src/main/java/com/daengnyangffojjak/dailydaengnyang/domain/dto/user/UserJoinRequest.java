package com.daengnyangffojjak.dailydaengnyang.domain.dto.user;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserJoinRequest {
    @NotEmpty(message = "아이디는 필수 입력값입니다.")
    private String userName;
    @NotEmpty(message = "비밀번호는 필수 입력값입니다.")
    private String password;
    @NotEmpty(message = "이메일는 필수 입력값입니다.")
    @Email
    private String email;
    public User toEntity(String password){
        return User.builder()
                .userName(this.userName)
                .password(password)
                .email(this.email)
                .role(UserRole.ROLE_USER)
                .build();
    }
}
