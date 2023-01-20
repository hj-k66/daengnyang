package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinResponse;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.UserException;
import com.daengnyangffojjak.dailydaengnyang.service.UserService;
import com.daengnyangffojjak.dailydaengnyang.utils.RestDocsConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static com.daengnyangffojjak.dailydaengnyang.utils.RestDocsConfiguration.field;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
@WithMockUser
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
class UserRestControllerTest {
    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    protected RestDocumentationResultHandler restDocs;
    @Autowired
    private MockMvc mockMvc;
    UserJoinRequest userJoinRequest = new UserJoinRequest("hoon", "hi", "gg@gmail.com");


    @BeforeEach
    void setUp(final WebApplicationContext context,
               final RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))  // rest docs 설정 주입
                .alwaysDo(MockMvcResultHandlers.print()) // andDo(print()) 코드 포함
                .alwaysDo(restDocs) // pretty 패턴과 문서 디렉토리 명 정해준것 적용
                .addFilters(new CharacterEncodingFilter("UTF-8", true)) // 한글 깨짐 방지
                .build();
    }

    @Test
    @DisplayName("테스트")
    void test() throws Exception {
        mockMvc.perform(
                get("/api/v1/users/test")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                responseFields(
                                        fieldWithPath("test").description("ok")
                                ))
                );
    }

    @Nested
    @DisplayName("회원가입")
    class Join{
        @Test
        @DisplayName("회원가입 성공")
        void join_success() throws Exception {
            given(userService.join(userJoinRequest)).willReturn(new UserJoinResponse(0L, "hoon", "gg@gmail.com"));

            mockMvc.perform(
                            post("/api/v1/users/join")
                                    .content(objectMapper.writeValueAsBytes(userJoinRequest))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.result.userName").value("hoon"))
                    .andExpect(jsonPath("$.result.id").value(0))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("userName").description("유저아이디").attributes(field("constraints", "중복 불가능")),
                                            fieldWithPath("password").description("비밀번호"),
                                            fieldWithPath("email").description("이메일").attributes(field("constraints", "중복 불가능"))
                                    ),
                                    responseFields(
                                            fieldWithPath("resultCode").description("결과코드"),
                                            fieldWithPath("result.id").description("유저번호"),
                                            fieldWithPath("result.userName").description("유저아이디"),
                                            fieldWithPath("result.email").description("이메일"))
                            )
                    );
            verify(userService).join(userJoinRequest);
        }
        @Test
        @DisplayName("회원가입 실패 - 이메일 형식 오류")
        void join_fail_invalid_email() throws Exception {
            given(userService.join(userJoinRequest))
                    .willThrow(new UserException(ErrorCode.INVALID_VALUE));

            mockMvc.perform(
                            post("/api/v1/users/join")
                                    .content(objectMapper.writeValueAsBytes(userJoinRequest))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("INVALID_VALUE"))
                    .andDo(print());
            verify(userService).join(userJoinRequest);
        }
        @Test
        @DisplayName("회원가입 실패 - 아이디 중복")
        void join_fail_duplicated_id() throws Exception {
            given(userService.join(userJoinRequest))
                    .willThrow(new UserException(ErrorCode.DUPLICATED_USER_NAME));

            mockMvc.perform(
                            post("/api/v1/users/join")
                                    .content(objectMapper.writeValueAsBytes(userJoinRequest))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.result.errorCode").value("DUPLICATED_USER_NAME"))
                    .andExpect(jsonPath("$.result.message").value("UserName이 중복됩니다."))
                    .andDo(print());
            verify(userService).join(userJoinRequest);
        }
        @Test
        @DisplayName("회원가입 실패 - 이메일 중복")
        void join_fail_이메일중복() throws Exception {
            given(userService.join(userJoinRequest))
                    .willThrow(new UserException(ErrorCode.DUPLICATED_EMAIL));

            mockMvc.perform(
                            post("/api/v1/users/join")
                                    .content(objectMapper.writeValueAsBytes(userJoinRequest))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.result.errorCode").value("DUPLICATED_EMAIL"))
                    .andExpect(jsonPath("$.result.message").value("이메일이 중복됩니다."))
                    .andDo(print());
            verify(userService).join(userJoinRequest);
        }


    }

}