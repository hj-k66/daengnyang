package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;      //그룹 멤버
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
    @Column(nullable = false)
    private String roleInGroup;

    public static UserGroup from(User user, Group group, String roleInGroup){
        return UserGroup.builder()
                .user(user)
                .group(group)
                .roleInGroup(roleInGroup)
                .build();
    }
}
