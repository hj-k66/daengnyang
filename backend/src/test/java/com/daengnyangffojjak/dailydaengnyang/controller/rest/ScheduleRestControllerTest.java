package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleCreateRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleCreateResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleModifyRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleModifyResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Category;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.ScheduleException;
import com.daengnyangffojjak.dailydaengnyang.service.ScheduleService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import java.time.LocalDateTime;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScheduleRestController.class)
class ScheduleRestControllerTest extends ControllerTest{
    @MockBean
    ScheduleService scheduleService;

    // 일정 등록시간 미리 지정해둠 -> 테스트할 때 현재시간으로 되어 시간이 계속 안맞음 해결
    LocalDateTime dateTime = LocalDateTime.of(2023, 1, 25, 10, 26);

    // 일정등록
    ScheduleCreateRequest scheduleCreateRequest = new ScheduleCreateRequest(Category.HOSPITAL, "병원", "초음파 재검", 3L, "멋사동물원", dateTime);
    ScheduleCreateResponse scheduleCreateResponse = new ScheduleCreateResponse("일정 등록 완료", 1L);

    // 일정수정
    ScheduleModifyRequest scheduleModifyRequest = new ScheduleModifyRequest(Category.HOSPITAL, "수정 병원", "수정 초음파 재검", 1L, "수정 멋사동물병원", dateTime);
    ScheduleModifyResponse scheduleModifyResponse = new ScheduleModifyResponse(1L,"수정 병원", dateTime);

    // ----------------------------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("일정등록")
    class ScheduleCreate{
        @Test
        @DisplayName("일정등록 성공")
        void create_success() throws Exception {
            given(scheduleService.create(1L, scheduleCreateRequest, "user"))
                    .willReturn(scheduleCreateResponse);

            mockMvc.perform(
                            RestDocumentationRequestBuilders.post("/api/v1/pets/{petId}/schedules", 1L)
                                    .with(csrf())
                                    // java 8 부터 LocalDateTime을 가진 객체를 ObjectMapper 함수를 사용하여 가져올 경우 직렬화 또는 역직렬화를 못하는 에러 발생으로 아래의 코드 작성
                                    .content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsBytes(scheduleCreateRequest))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.result.message").value("일정 등록 완료"))
                    .andExpect(jsonPath("$.result.id").value(1L))
                    .andDo(
                            restDocs.document(
                                    pathParameters(
                                            parameterWithName("petId").description("반려동물 번호")
                                    ),
                                    requestFields(
                                            fieldWithPath("category").description("카테고리"),
                                            fieldWithPath("title").description("제목"),
                                            fieldWithPath("body").description("내용"),
                                            fieldWithPath("assigneeId").description("책임자"),
                                            fieldWithPath("place").description("장소"),
                                            fieldWithPath("dueDate").description("예정날짜")
                                    ),
                                    responseFields(
                                            fieldWithPath("resultCode").description("결과코드"),
                                            fieldWithPath("result.message").description("결과메세지"),
                                            fieldWithPath("result.id").description("일정번호"))
                            )
                    );
            verify(scheduleService).create(1L, scheduleCreateRequest, "user");
        }

        @Test
        @DisplayName("일정등록 실패 - 유저가 없는 경우")
        void create_fail_username_not_found() throws Exception {
            given(scheduleService.create(1L, scheduleCreateRequest, "user"))
                    .willThrow(new ScheduleException(ErrorCode.USERNAME_NOT_FOUND));

            mockMvc.perform(
                            post("/api/v1/pets/1/schedules")
                                    .content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsBytes(scheduleCreateRequest))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value("USERNAME_NOT_FOUND"))
                    .andExpect(jsonPath("$.result.message").value("Not founded"))
                    .andDo(print());

            verify(scheduleService).create(1L, scheduleCreateRequest, "user");
        }

        @Test
        @DisplayName("일정등록 실패 - 등록되지 않은 반려동물일 경우")
        void create_fail_pet_not_found() throws Exception {
            given(scheduleService.create(1L, scheduleCreateRequest, "user"))
                    .willThrow(new ScheduleException(ErrorCode.PET_NOT_FOUND));

            mockMvc.perform(
                            post("/api/v1/pets/1/schedules")
                                    .content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsBytes(scheduleCreateRequest))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value("PET_NOT_FOUND"))
                    .andExpect(jsonPath("$.result.message").value("등록된 반려동물이 아닙니다."))
                    .andDo(print());

            verify(scheduleService).create(1L, scheduleCreateRequest, "user");
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("일정수정")
    class ScheduleModify{
        @Test
        @DisplayName("일정수정 성공")
        void modify_success() throws Exception {
            given(scheduleService.modify(1L, 1L, scheduleModifyRequest, "user"))
                    .willReturn(scheduleModifyResponse);

            mockMvc.perform(
                            RestDocumentationRequestBuilders.put("/api/v1/pets/{petId}/schedules/{scheduleId}", 1L, 1L)
                                    .content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsBytes(scheduleModifyRequest))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.result.id").value(1L))
                    .andExpect(jsonPath("$.result.title").value("수정 병원"))
                    .andExpect(jsonPath("$.result.lastModifiedAt").value("2023/01/25 10:26:00"))

                    .andDo(
                            restDocs.document(
                                    pathParameters(
                                            parameterWithName("petId").description("반려동물 번호"),
                                            parameterWithName("scheduleId").description("일정 번호")
                                    ),
                                    requestFields(
                                            fieldWithPath("category").description("카테고리수정"),
                                            fieldWithPath("title").description("제목수정"),
                                            fieldWithPath("body").description("내용수정"),
                                            fieldWithPath("assigneeId").description("책임자 userId 수정"),
                                            fieldWithPath("place").description("장소수정"),
                                            fieldWithPath("dueDate").description("예정날짜수정")
                                    ),
                                    responseFields(
                                            fieldWithPath("resultCode").description("결과코드"),
                                            fieldWithPath("result.id").description("일정번호"),
                                            fieldWithPath("result.title").description("수정한제목"),
                                            fieldWithPath("result.lastModifiedAt").description("수정한날짜")
                                    )
                            )
                    );
            verify(scheduleService).modify(1L, 1L, scheduleModifyRequest, "user");
        }

        @Test
        @DisplayName("일정수정 실패 - 유저가 없는 경우")
        void modify_fail_username_not_found() throws Exception {
            given(scheduleService.modify(1L, 1L, scheduleModifyRequest, "user"))
                    .willThrow(new ScheduleException(ErrorCode.USERNAME_NOT_FOUND));

            mockMvc.perform(
                            put("/api/v1/pets/1/schedules/1")
                                    .content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsBytes(scheduleModifyRequest))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value("USERNAME_NOT_FOUND"))
                    .andExpect(jsonPath("$.result.message").value("Not founded"))
                    .andDo(print());

            verify(scheduleService).modify(1L, 1L, scheduleModifyRequest, "user");
        }

        @Test
        @DisplayName("일정수정 실패 - 등록되지 않은 반려동물일 경우")
        void modify_fail_pet_not_found() throws Exception {
            given(scheduleService.modify(1L, 1L, scheduleModifyRequest, "user"))
                    .willThrow(new ScheduleException(ErrorCode.PET_NOT_FOUND));

            mockMvc.perform(
                            put("/api/v1/pets/1/schedules/1")
                                    .content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsBytes(scheduleModifyRequest))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value("PET_NOT_FOUND"))
                    .andExpect(jsonPath("$.result.message").value("등록된 반려동물이 아닙니다."))
                    .andDo(print());

            verify(scheduleService).modify(1L, 1L, scheduleModifyRequest, "user");
        }

        @Test
        @DisplayName("일정수정 실패 - 등록된 일정이 없는 경우")
        void modify_fail_schdule_not_found() throws Exception {
            given(scheduleService.modify(1L, 1L, scheduleModifyRequest, "user"))
                    .willThrow(new ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND));

            mockMvc.perform(
                            put("/api/v1/pets/1/schedules/1")
                                    .with(csrf())
                                    .content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsBytes(scheduleModifyRequest))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value("SCHEDULE_NOT_FOUND"))
                    .andExpect(jsonPath("$.result.message").value("등록된 일정이 없습니다."))
                    .andDo(print());

            verify(scheduleService).modify(1L, 1L, scheduleModifyRequest, "user");
        }

        @Test
        @DisplayName("일정수정 실패 - 로그인유저 != 작성유저")
        void modify_fail_invalid_permission() throws Exception {
            given(scheduleService.modify(1L, 1L, scheduleModifyRequest, "user"))
                    .willThrow(new ScheduleException(ErrorCode.INVALID_PERMISSION));

            mockMvc.perform(
                            put("/api/v1/pets/1/schedules/1")
                                    .content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsBytes(scheduleModifyRequest))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"))
                    .andExpect(jsonPath("$.result.message").value("사용자가 권한이 없습니다."))
                    .andDo(print());

            verify(scheduleService).modify(1L, 1L, scheduleModifyRequest, "user");
        }
    }
}