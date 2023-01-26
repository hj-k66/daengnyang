package com.daengnyangffojjak.dailydaengnyang.domain.dto.group;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupPetResponse {
    private Long id;
    private String name;
    private Species species;
    private String age;

    public static GroupPetResponse from(Pet pet){
        return GroupPetResponse.builder()
                .id(pet.getId())
                .name(pet.getName())
                .species(pet.getSpecies())
                .age(pet.getAge())
                .build();
    }
}
