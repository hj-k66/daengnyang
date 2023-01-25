package com.daengnyangffojjak.dailydaengnyang.domain.dto.pet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class PetDeleteResponse {
    private String message;
}
