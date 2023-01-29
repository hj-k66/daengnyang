package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class PetTest {
    @Test
    @DisplayName("나이 구하기")
    void getAgeTest(){
        Pet pet1 = Pet.builder().birthday(LocalDate.of(2022, 1, 20)).build();
        Pet pet2 = Pet.builder().birthday(LocalDate.of(2022, 1, 30)).build();
        Pet pet3 = Pet.builder().birthday(LocalDate.of(2023, 1, 20)).build();
        Pet pet4 = Pet.builder().birthday(LocalDate.of(2020, 1, 20)).build();
        Pet pet5 = Pet.builder().birthday(LocalDate.of(2020, 1, 30)).build();

        assertEquals("1살", pet1.getAge());      //2023.1.26일 기준
        assertEquals("11개월", pet2.getAge());
        assertEquals("0개월", pet3.getAge());
        assertEquals("3살", pet4.getAge());
        assertEquals("2살", pet5.getAge());
    }
    @Test
    void age(){
        LocalDate now = LocalDate.now();
        LocalDate birthday = LocalDate.of(2022, 11, 20);

        long months = ChronoUnit.MONTHS.between(birthday, now); //태어난지 12개월 넘었는 지
        long years = ChronoUnit.YEARS.between(birthday, now); //태어난지 12개월 넘었는 지

        if(months < 12){
            System.out.println(months + "개월");
        } else {
            System.out.println(years +"살");
        }
    }
}