package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import ch.qos.logback.core.boolex.EvaluationException;
import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Where(clause = "deleted_at is NULL")
@Table(name = "\"Group\"")
public class Group extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;      //그룹장
    private String name;    //그룹 이름
}
