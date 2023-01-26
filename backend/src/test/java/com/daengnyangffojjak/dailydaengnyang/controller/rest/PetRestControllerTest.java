package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetResultResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetShowResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Sex;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.UserException;
import com.daengnyangffojjak.dailydaengnyang.service.PetService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PetRestController.class)
class PetRestControllerTest extends ControllerTest{

    @MockBean
    PetService petService;

    @Autowired
    ObjectMapper objectMapper;
    // Java 8 date/time type `java.time.LocalDateTime` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (through reference chain: com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest["birthday"])
    //com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type `java.time.LocalDateTime` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (through reference chain: com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest["birthday"])

    PetAddRequest petAddRequest = PetAddRequest.builder()
            .name("멍뭉이")
            .species(Species.DOG)
            .breed("진돗개")
            .sex(Sex.MALE)
            .birthday(LocalDateTime.of(2022,1,1,1,1))
            .weight(5.5)
            //.group()
            //.user(user) 필요 pet 등록할때 같이 user 정보를 저장해야 함
            .build();
    PetResultResponse petResultResponse = PetResultResponse.builder()
            .id(1l)
            .name("멍뭉이")
            .age(LocalDateTime.of(2022,1,1,1,1))
            .createdAt(LocalDateTime.of(2022,1,1,1,1))
            .build();

    @Test
    @DisplayName("pet 등록 성공")
    void add_Pet_success() throws Exception {
        //given


        //when
        //when(petService.add(any(), any(), any())).thenReturn(petResultResponse);

        given(petService.add(any(), any(), any())).willReturn(petResultResponse);
        //then
        mockMvc.perform(post("/api/v1/groups/1/pets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(petAddRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.result.id").value(1))
        ;
    }
    @Test
    @DisplayName("pet 등록 실패 - 그룹이 없는 경우")
    void add_Pet_fail() throws Exception {
        given(petService.add(1l, petAddRequest, "user")).willThrow(new UserException(ErrorCode.GROUP_NOT_FOUND));

        mockMvc.perform(post("/api/v1/groups/1/pets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(petAddRequest))
        )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("pet 리스트 조회 성공")
    void show_Pet_List() throws Exception {

        PetShowResponse petShowResponse = PetShowResponse.builder()
                .id(1l)
                .name("이름")
                .species(Species.DOG)
                .breed("종")
                .sex(Sex.MALE)
                .birthday(LocalDateTime.of(2022,1,1,1,1))
                .createdAt(LocalDateTime.of(2022,1,1,1,1))
                .lastModifiedAt(LocalDateTime.of(2022,1,1,1,1))
                .build();
        Page<PetShowResponse> page = new PageImpl<>(List.of(petShowResponse));
        Page<PetShowResponse> pages = new PageImpl<>(Arrays.asList(petShowResponse));

        //given(petService.showAll(any(), any())).willReturn(mock(Page.class));
        given(petService.showAll(any(), any())).willReturn(page);

        mockMvc.perform(get("/api/v1/groups/1/pets")

                        .param("page", "0")
                        .param("size", "3")
                        .param("sort", "createdAt,desc")

                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(petService).showAll(1l, pageableCaptor.capture());
        PageRequest pageable = (PageRequest) pageableCaptor.getValue();

        Assertions.assertEquals(0, pageable.getPageNumber());
        Assertions.assertEquals(3, pageable.getPageSize());
        Assertions.assertEquals(Sort.by("createdAt", "desc"), pageable.withSort(Sort.by("createdAt", "desc")).getSort());


    }


}