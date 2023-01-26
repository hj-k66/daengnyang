package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Sex;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private LocalDate birthday;
    private double weight;

    public String getAge(){     //1년령 이상은 나이, 1년령 이하는 개월수로 반환
        LocalDate today = LocalDate.now();
        LocalDate birthday = this.birthday;

        long months = ChronoUnit.MONTHS.between(birthday, today); //태어난지 12개월 넘었는 지
        long years = ChronoUnit.YEARS.between(birthday, today);

        if(months < 12){
            return months + "개월";
        } else {
            return years +"살";
        }
    }
}
