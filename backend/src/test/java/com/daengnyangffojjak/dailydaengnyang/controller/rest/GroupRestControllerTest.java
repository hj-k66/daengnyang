package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupUserListResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupUserResponse;
import com.daengnyangffojjak.dailydaengnyang.service.GroupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    @Nested
    @DisplayName("그룹 사용자 리스트")
    class GroupUserList{
        @Test
        @DisplayName("그룹 생성 성공")
        void success() throws Exception {
            GroupUserListResponse groupUserResponse = new GroupUserListResponse(
                    List.of(new GroupUserResponse(1L, "user", "mom", true),
                            new GroupUserResponse(2L, "user2", "dad", false)),
                    2);
            given(groupService.getGroupUsers(1L, "user")).willReturn(groupUserResponse);

            mockMvc.perform(
                            RestDocumentationRequestBuilders.get("/api/v1/groups/{groupId}/users", 1L)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.users").exists())
                    .andDo(
                            restDocs.document(
                                    pathParameters(
                                            parameterWithName("groupId").description("그룹 번호")
                                    ),
                                    responseFields(
                                            fieldWithPath("resultCode").description("결과코드"),
                                            fieldWithPath("result.users").description("그룹 내 유저 리스트"),
                                            fieldWithPath("result.users[].id").description("유저 번호"),
                                            fieldWithPath("result.users[].userName").description("유저 아이디"),
                                            fieldWithPath("result.users[].roleInGroup").description("그룹 내 역할"),
                                            fieldWithPath("result.users[].owner").description("그룹장 여부"),
                                            fieldWithPath("result.count").description("그룹 내 유저 수"))
                            )
                    );
            verify(groupService).getGroupUsers(1L, "user");
        }
    }
}