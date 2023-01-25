package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetResultResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Sex;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import com.daengnyangffojjak.dailydaengnyang.service.PetService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PetRestController.class)
class PetRestControllerTest extends ControllerTest{

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PetService petService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("pet 등록 성공")
    @WithMockUser
    void add_Pet_success() throws Exception {
        //given

        PetAddRequest petAddRequest = PetAddRequest.builder()
                .name("멍뭉이")
                .species(Species.DOG)
                .breed("진돗개")
                .sex(Sex.MALE)
                .birthday(null)
                .weight(5.5)
                //.group()
                //.user(user) 필요 pet 등록할때 같이 user 정보를 저장해야 함
                .build();
        PetResultResponse petResultResponse = PetResultResponse.builder()
                .id(1l)
                .name("멍뭉이")
                .age(null)
                .createdAt(null)
                .build();

        //when
        when(petService.add(any(), any(), any())).thenReturn(petResultResponse);

        //then
        mockMvc.perform(post("/api/v1/groups/1/pets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(petAddRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.result.id").exists())

        ;
    }
}