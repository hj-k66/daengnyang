package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Sex;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
    @Enumerated(EnumType.STRING)
    private Species species;
    private String breed;       //추후 enum으로 수정 예정
    private String name;
    @Enumerated(EnumType.STRING)
    private Sex sex;
    private LocalDateTime birthday;
    private double weight;
}
