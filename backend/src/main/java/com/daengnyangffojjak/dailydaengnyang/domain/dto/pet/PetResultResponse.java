package com.daengnyangffojjak.dailydaengnyang.domain.dto.pet;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PetResultResponse {
    private Long id;
    private String name;
    private LocalDateTime age; // 이거 생일로 하면 안돼요?
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public static PetResultResponse addFrom(Pet pet) {
        return PetResultResponse.builder()
                .id(pet.getId())
                .name(pet.getName())
                .age(pet.getBirthday()) // 현재 연도 - 생일연도 해야함
                .createdAt(pet.getCreatedAt())
                .build();
    }

    public static PetResultResponse updateFrom(Pet pet) {
        return PetResultResponse.builder()
                .id(pet.getId())
                .name(pet.getName())
                .age(pet.getBirthday()) // 현재 연도 - 생일연도 해야함
                .lastModifiedAt(pet.getLastModifiedAt())
                .build();
    }
}
