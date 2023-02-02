package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import static com.daengnyangffojjak.dailydaengnyang.utils.RestDocsConfiguration.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.tag.TagListResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.tag.TagWorkRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.tag.TagWorkResponse;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.TagException;
import com.daengnyangffojjak.dailydaengnyang.service.TagService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

@WebMvcTest(TagRestController.class)
class TagRestControllerTest extends ControllerTest {

	@MockBean
	protected TagService tagService;

	@Nested
	@DisplayName("태그 등록")
	class TagCreate {

		TagWorkRequest request = new TagWorkRequest("산책");

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			given(tagService.create(1L, request, "user")).willReturn(new TagWorkResponse(1L, "산책"));

			mockMvc.perform(
							post("/api/v1/groups/{groupId}/tags", 1L)
									.content(objectMapper.writeValueAsBytes(request))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.id").value(1L))
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("groupId").description("그룹 번호")
							),
							requestFields(fieldWithPath("name").description("태그 이름")
									.attributes(field("constraints", "2~10자 사이"))),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.id").description("태그 등록 번호"),
									fieldWithPath("result.name").description("태그 이름"))));
			verify(tagService).create(1L, request, "user");

		}
	}

	@Nested
	@DisplayName("태그 수정")
	class TagModify {

		TagWorkRequest request = new TagWorkRequest("산책");

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			given(tagService.modify(1L, 1L, request, "user")).willReturn(
					new TagWorkResponse(1L, "산책"));

			mockMvc.perform(
							put("/api/v1/groups/{groupId}/tags/{tagId}", 1L, 1L)
									.content(objectMapper.writeValueAsBytes(request))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.id").value(1L))
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("groupId").description("그룹 번호"),
									parameterWithName("tagId").description("태그 번호")
							),
							requestFields(fieldWithPath("name").description("태그 이름")
									.attributes(field("constraints", "2~10자 사이"))),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.id").description("태그 등록 번호"),
									fieldWithPath("result.name").description("태그 이름"))));
			verify(tagService).modify(1L, 1L, request, "user");

		}
	}

	@Nested
	@DisplayName("태그 삭제")
	class TagDelete {

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			given(tagService.delete(1L, 1L, "user")).willReturn(
					new MessageResponse("태그가 삭제되었습니다."));

			mockMvc.perform(
							delete("/api/v1/groups/{groupId}/tags/{tagId}", 1L, 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.msg").value("태그가 삭제되었습니다."))
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("groupId").description("그룹 번호"),
									parameterWithName("tagId").description("태그 번호")
							),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.msg").description("결과 메세지"))));
			verify(tagService).delete(1L, 1L, "user");

		}

		@Test
		@DisplayName("실패 - 태그를 사용하고 있을 때")
		void fail_태그사용중() throws Exception {
			given(tagService.delete(1L, 1L, "user")).willThrow(
					new TagException(ErrorCode.INVALID_REQUEST));

			mockMvc.perform(
							delete("/api/v1/groups/{groupId}/tags/{tagId}", 1L, 1L))
					.andExpect(status().isConflict())
					.andExpect(jsonPath("$.resultCode").value("ERROR"))
					.andExpect(jsonPath("$.result.errorCode").value("INVALID_REQUEST"))
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("groupId").description("그룹 번호"),
									parameterWithName("tagId").description("태그 번호")
							),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.errorCode").description("에러코드"),
									fieldWithPath("result.message").description("에러 메세지"))));
			verify(tagService).delete(1L, 1L, "user");

		}
	}

	@Nested
	@DisplayName("태그리스트 조회")
	class TagListGet {

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			given(tagService.getList(1L, "user")).willReturn(
					new TagListResponse(List.of("일상", "목욕"))
			);

			mockMvc.perform(
							get("/api/v1/groups/{groupId}/tags", 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.tags").exists())
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("groupId").description("그룹 번호")
							),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.tags").description("태그 리스트"))));
			verify(tagService).getList(1L, "user");

		}
	}

}