package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.tag.TagWorkRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
    @Column(nullable = false)
    private String name;

    public void modify(TagWorkRequest tagWorkRequest) {
        this.name = tagWorkRequest.getName();
    }
    public static Tag from (Group group, String name) {
        return Tag.builder()
                .group(group)
                .name(name)
                .build();
    }
}
