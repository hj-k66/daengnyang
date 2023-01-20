package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
}
