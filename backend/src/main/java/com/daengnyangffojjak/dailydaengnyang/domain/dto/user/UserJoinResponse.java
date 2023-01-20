package com.daengnyangffojjak.dailydaengnyang.domain.dto.user;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserJoinResponse {
    private Long id;
    private String userName;
    private String email;

    public static UserJoinResponse from(User user) {
        return UserJoinResponse.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
