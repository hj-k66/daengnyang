package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PetTest {
    @Test
    @DisplayName("나이 구하기")
    void getAgeTest(){
        Pet pet1 = Pet.builder().birthday(LocalDate.of(2022, 1, 20)).build();
        Pet pet2 = Pet.builder().birthday(LocalDate.of(2022, 1, 30)).build();
        Pet pet3 = Pet.builder().birthday(LocalDate.of(2023, 1, 20)).build();
        Pet pet4 = Pet.builder().birthday(LocalDate.of(2020, 1, 20)).build();

        assertEquals("1살", pet1.getAge());      //2023.1.26일 기준
        assertEquals("11개월", pet2.getAge());
        assertEquals("0개월", pet3.getAge());
        assertEquals("3살", pet4.getAge());
    }
}