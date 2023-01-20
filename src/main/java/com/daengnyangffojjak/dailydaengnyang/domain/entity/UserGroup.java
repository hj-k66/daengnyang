package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
    @Column(nullable = false)
    private String roleInGroup;
}
