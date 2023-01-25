package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinResponse;
import com.daengnyangffojjak.dailydaengnyang.service.GroupService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static com.daengnyangffojjak.dailydaengnyang.utils.RestDocsConfiguration.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupRestController.class)
class GroupRestControllerTest extends ControllerTest{
    @MockBean
    protected GroupService groupService;

    @Nested
    @DisplayName("그룹 생성")
    class GroupCreate{
        GroupMakeRequest groupMakeRequest = new GroupMakeRequest("그룹이름", "엄마");
        GroupMakeResponse groupMakeResponse = GroupMakeResponse.builder()
                .id(1L)
                .name("그룹이름")
                .ownerId(1L)
                .ownerUserName("user")
                .build();


        @Test
        @DisplayName("그룹 생성 성공")
        void success() throws Exception {
            given(groupService.create(groupMakeRequest, "user")).willReturn(groupMakeResponse);

            mockMvc.perform(
                            post("/api/v1/groups")
                                    .content(objectMapper.writeValueAsBytes(groupMakeRequest))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.ownerId").value(1L))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("name").description("그룹 이름"),
                                            fieldWithPath("roleInGroup").description("그룹 내 역할")
                                    ),
                                    responseFields(
                                            fieldWithPath("resultCode").description("결과코드"),
                                            fieldWithPath("result.id").description("그룹 번호"),
                                            fieldWithPath("result.name").description("그룹 이름"),
                                            fieldWithPath("result.ownerId").description("그룹주인 번호"),
                                            fieldWithPath("result.ownerUserName").description("그룹주인 아이디"))
                            )
                    );
            verify(groupService).create(groupMakeRequest, "user");
        }
    }
}