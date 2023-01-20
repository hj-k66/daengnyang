package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Category;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Record extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")   //작성자
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;
    @Enumerated(EnumType.STRING)
    private Category category;
    @Column(nullable = false)
    private String title;
    private String body;
    @Column(nullable = false)
    private Boolean isPublic;
}
