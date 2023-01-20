package com.daengnyangffojjak.dailydaengnyang.domain.dto.user;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class UserJoinRequest {
    private String userName;
    private String password;
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
