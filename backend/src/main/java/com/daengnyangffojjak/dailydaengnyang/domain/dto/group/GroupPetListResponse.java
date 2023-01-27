package com.daengnyangffojjak.dailydaengnyang.domain.dto.group;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupPetListResponse {
    private List<GroupPetResponse> pets;
    private int count;

    public static GroupPetListResponse from(List<Pet> pets) {
        List<GroupPetResponse>  petResponses = pets.stream().map(GroupPetResponse::from)
                .toList();
        return new GroupPetListResponse(petResponses, petResponses.size());
    }
}
