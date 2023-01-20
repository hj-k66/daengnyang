package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Bookmark extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")   //작성자
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")   //일기
    private Record record;
}
