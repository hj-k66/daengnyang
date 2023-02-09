package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.token.TokenInfo;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.token.TokenRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserLoginRequest;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.SecurityCustomException;
import com.daengnyangffojjak.dailydaengnyang.exception.UserException;
import com.daengnyangffojjak.dailydaengnyang.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static com.daengnyangffojjak.dailydaengnyang.utils.RestDocsConfiguration.field;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
class UserRestControllerTest extends ControllerTest {

	@MockBean
	UserService userService;

	UserJoinRequest userJoinRequest = new UserJoinRequest("hoon", "hi", "gg@gmail.com");
	UserLoginRequest userLoginRequest = new UserLoginRequest("hoon", "hi");

	TokenRequest tokenRequest = new TokenRequest("accesstokenalskdjf", "refreshToken=eyJhbGciOiefJIUzI");
	long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 10;

	@Nested
	@DisplayName("회원가입")
	class Join {

		@Test
		@DisplayName("회원가입 성공")
		void join_success() throws Exception {
			given(userService.join(userJoinRequest)).willReturn(
					new UserJoinResponse(0L, "hoon", "gg@gmail.com"));

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
											fieldWithPath("userName").description("유저아이디")
													.attributes(field("constraints", "중복 불가능")),
											fieldWithPath("password").description("비밀번호"),
											fieldWithPath("email").description("이메일")
													.attributes(field("constraints", "중복 불가능"))
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

	@Nested
	@DisplayName("로그인")
	class Login {

		@Test
		@DisplayName("로그인 성공")
		void login_success() throws Exception {

			given(userService.login(userLoginRequest)).willReturn(
					new TokenInfo("accesstoken", "refreshtoken", REFRESH_TOKEN_EXPIRE_TIME));

			mockMvc.perform(
							post("/api/v1/users/login")
									.content(objectMapper.writeValueAsBytes(userLoginRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.accessToken").value("accesstoken"))
					.andExpect(cookie().value("refreshToken", "refreshtoken"))  //쿠키 검증
					.andDo(
							restDocs.document(
									requestFields(
											fieldWithPath("userName").description("유저아이디"),
											fieldWithPath("password").description("비밀번호")
									),
									responseFields(
											fieldWithPath("resultCode").description("결과코드"),
											fieldWithPath(
													"result.accessToken").description(
													"엑세스토큰")
									)
							));
			verify(userService).login(userLoginRequest);
		}

		@Test
		@DisplayName("로그인 실패 - userName 없음")
		void login_failed_userName() throws Exception {

			given(userService.login(userLoginRequest)).willThrow(
					new UserException(ErrorCode.USERNAME_NOT_FOUND));

			mockMvc.perform(post("/api/v1/users/login")
							.with(csrf())
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsBytes(userLoginRequest)))
					.andDo(print())
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.resultCode").value("ERROR"))
					.andExpect(jsonPath("$.result.errorCode").value(
							ErrorCode.USERNAME_NOT_FOUND.name()))
					.andExpect(jsonPath("$.result.message").value(
							ErrorCode.USERNAME_NOT_FOUND.getMessage()));

		}

		@Test
		@DisplayName("로그인 실패 - password 틀림")
		void login_failed_password() throws Exception {

			given(userService.login(userLoginRequest)).willThrow(
					new UserException(ErrorCode.INVALID_PASSWORD));

			mockMvc.perform(post("/api/v1/users/login")
							.with(csrf())
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsBytes(userLoginRequest)))
					.andDo(print())
					.andExpect(status().isUnauthorized())
					.andExpect(jsonPath("$.resultCode").value("ERROR"))
					.andExpect(
							jsonPath("$.result.errorCode").value(ErrorCode.INVALID_PASSWORD.name()))
					.andExpect(jsonPath("$.result.message").value(
							ErrorCode.INVALID_PASSWORD.getMessage()));
		}

	}

	@Nested
	@DisplayName("로그아웃")
	class Logout{
		@Test
		@DisplayName("로그아웃 성공")
		void logout_success() throws Exception {

			given(userService.logout(tokenRequest)).willReturn(
					new MessageResponse("로그아웃이 되었습니다."));

			mockMvc.perform(
							post("/api/v1/users/logout")
									.content(objectMapper.writeValueAsBytes(tokenRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.msg").value("로그아웃이 되었습니다."))
					.andDo(
							restDocs.document(
									requestFields(
											fieldWithPath("accessToken").description("엑세스토큰"),
											fieldWithPath("refreshToken").description("리프레쉬토큰")
									),
									responseFields(
											fieldWithPath("resultCode").description("결과코드"),
											fieldWithPath(
													"result.msg").description(
													"결과메세지")
									)
							));
			verify(userService).logout(tokenRequest);
		}
	}

	@Nested
	@DisplayName("토큰 재발급")
	class NewToken {
		@Test
		@DisplayName("토큰 재발급 성공")
		void newtoken_success() throws Exception {
			given(userService.generateNewToken(tokenRequest)).willReturn(
					new TokenInfo("new-accesstoken", "new-refreshtoken",
							REFRESH_TOKEN_EXPIRE_TIME));
			mockMvc.perform(
							post("/api/v1/users/new-token")
									.content(objectMapper.writeValueAsBytes(tokenRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.accessToken").value("new-accesstoken"))
					.andExpect(cookie().value("refreshToken", "new-refreshtoken"))  //쿠키 검증
					.andDo(
							restDocs.document(
									requestFields(
											fieldWithPath("accessToken").description("엑세스토큰"),
											fieldWithPath("refreshToken").description("리프레시토큰")
									),
									responseFields(
											fieldWithPath("resultCode").description("결과코드"),
											fieldWithPath(
													"result.accessToken").description(
													"엑세스토큰"))
							));
			verify(userService).generateNewToken(tokenRequest);

		}

		@Test
		@DisplayName("토큰 재발급 실패 - 유효하지 않은 refreshtoken")
		void newtoken_failed_notValid() throws Exception {

			given(userService.generateNewToken(tokenRequest)).willThrow(
					new SecurityCustomException(ErrorCode.INVALID_TOKEN, "재로그인 하세요."));

			mockMvc.perform(post("/api/v1/users/new-token")
							.with(csrf())
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsBytes(tokenRequest)))
					.andDo(print())
					.andExpect(status().isUnauthorized())
					.andExpect(jsonPath("$.resultCode").value("ERROR"))
					.andExpect(jsonPath("$.result.errorCode").value(
							ErrorCode.INVALID_TOKEN.name()))
					.andExpect(jsonPath("$.result.message").value(
							ErrorCode.INVALID_TOKEN.getMessage() + " 재로그인 하세요."));
		}

		@Test
		@DisplayName("토큰 재발급 실패 - refreshToken 불일치")
		void newtoken_failed_notequal() throws Exception {

			given(userService.generateNewToken(tokenRequest)).willThrow(
					new SecurityCustomException(ErrorCode.INVALID_TOKEN, "재로그인 하세요."));

			mockMvc.perform(post("/api/v1/users/new-token")
							.with(csrf())
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsBytes(tokenRequest)))
					.andDo(print())
					.andExpect(status().isUnauthorized())
					.andExpect(jsonPath("$.resultCode").value("ERROR"))
					.andExpect(jsonPath("$.result.errorCode").value(
							ErrorCode.INVALID_TOKEN.name()))
					.andExpect(jsonPath("$.result.message").value(
							ErrorCode.INVALID_TOKEN.getMessage() + " 재로그인 하세요."));
		}
	}

}