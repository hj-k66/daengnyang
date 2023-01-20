package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import jakarta.persistence.*;

@Entity
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")   //일기 번호
    private Record record;
    @Column(nullable = false)
    private String url;
}
