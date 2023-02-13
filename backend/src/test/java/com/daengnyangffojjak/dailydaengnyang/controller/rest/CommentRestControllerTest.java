package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import static com.daengnyangffojjak.dailydaengnyang.utils.RestDocsConfiguration.field;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.comment.CommentDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.comment.CommentModifyResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.comment.CommentRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.comment.CommentResponse;
import com.daengnyangffojjak.dailydaengnyang.exception.CommentException;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.service.CommentService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

@WebMvcTest(CommentRestController.class)
@Slf4j
class CommentRestControllerTest extends ControllerTest{

	@MockBean
	CommentService commentService;

	private JavaTimeModule javaTimeModule = new JavaTimeModule();

	LocalDateTime createdAt = LocalDateTime.of(2023, 2, 11, 10, 20);
	LocalDateTime lastModifiedAt = LocalDateTime.of(2023, 2, 12, 11, 25);

	@Nested
	@DisplayName("댓글 등록")
	class CommentCreate {

		CommentRequest commentRequest = new CommentRequest("댓글");
		CommentResponse commentResponse = CommentResponse.builder()
				.id(1L)
				.userName("user")
				.comment("댓글")
				.createdAt(createdAt)
				.build();


		@Test
		@DisplayName("댓글 작성 성공")
		void success() throws Exception {

			// 댓글 작성
			given(commentService.createComment(1L, commentRequest, "user"))
					.willReturn(commentResponse);

			mockMvc.perform(
					post("/api/v1/records/{recordsId}/comments", 1L)
									.content(objectMapper.registerModule(javaTimeModule)
											.writeValueAsBytes(commentRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.id").value(1L))
					.andExpect(jsonPath("$.result.userName").value("user"))
					.andExpect(jsonPath("$.result.comment").value("댓글"))
					.andExpect(jsonPath("$.result.createdAt").value("2023-02-11 10:20:00"))
					.andDo(restDocs.document(
							pathParameters(parameterWithName("recordsId").description("일기 등록 번호")),
							requestFields(fieldWithPath("comment").description("댓글")
							),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.id").description("댓글 등록 번호"),
									fieldWithPath("result.userName").description("댓글 작성자"),
									fieldWithPath("result.comment").description("댓글 내용"),
									fieldWithPath("result.createdAt").description("댓글 작성일"))));

			verify(commentService).createComment(1L, commentRequest, "user");
		}

//		@Test
//		@DisplayName("댓글 등록 실패 - 유저가 없는 경우")
//		void fail_유저없음() throws Exception {
//
//			// 댓글 작성
//			given(commentService.createComment(1L, commentRequest, "user"))
//					.willThrow(new CommentException(ErrorCode.USERNAME_NOT_FOUND));
//
//			mockMvc.perform(
//							post("/api/v1/records/{recordsId}/comments", 1L)
//									.content(objectMapper.registerModule(javaTimeModule)
//											.writeValueAsBytes(commentRequest))
//									.contentType(MediaType.APPLICATION_JSON))
//					.andExpect(status().isNotFound())
//					.andExpect(jsonPath("$.result.errorCode").value("USERNAME_NOT_FOUND"))
//					.andExpect(jsonPath("$.result.message").value("Not founded"))
//					.andDo(print());
//
//			verify(commentService).createComment(1L, commentRequest, "user");
//		}
//
//		@Test
//		@DisplayName("댓글 등록 실패 - 일기가 없는 경우")
//		void fail_일기없음() throws Exception {
//
//			given(commentService.createComment(1L, commentRequest, "user"))
//					.willThrow(new CommentException(ErrorCode.RECORD_NOT_FOUND));
//
//			mockMvc.perform(
//							post("/api/v1/records/{recordsId}/comments", 1L)
//									.content(objectMapper.registerModule(javaTimeModule)
//											.writeValueAsBytes(commentRequest))
//									.contentType(MediaType.APPLICATION_JSON))
//					.andExpect(status().isNotFound())
//					.andExpect(jsonPath("$.result.errorCode").value("RECORD_NOT_FOUND"))
//					.andExpect(jsonPath("$.result.message").value("등록된 일기가 없습니다."))
//					.andDo(print());
//
//			verify(commentService).createComment(1L, commentRequest, "user");
//		}
	}

	@Nested
	@DisplayName("댓글 수정")
	class CommentModify {

		CommentRequest commentRequest = new CommentRequest("댓글");
		CommentModifyResponse commentModifyResponse = CommentModifyResponse.builder()
				.id(1L)
				.userName("user")
				.comment("댓글")
				.lastModifiedAt(lastModifiedAt)
				.build();
		@Test
		@DisplayName("댓글 수정 성공")
		void success() throws Exception {
			given(commentService.modifyComment(1L, 1L, commentRequest, "user"))
					.willReturn(commentModifyResponse);

			mockMvc.perform(
							put("/api/v1/records/{recordsId}/comments/{id}", 1L, 1L)
									.content(objectMapper.writeValueAsBytes(commentRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.id").value(1L))
					.andExpect(jsonPath("$.result.userName").value("user"))
					.andExpect(jsonPath("$.result.comment").value("댓글"))
					.andExpect(jsonPath("$.result.lastModifiedAt").value("2023-02-12 11:25:00"))
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("recordsId").description("일기 등록 번호"),
									parameterWithName("id").description("일기 등록 번호")),
							requestFields(fieldWithPath("comment").description("댓글")
							),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.id").description("댓글 등록 번호"),
									fieldWithPath("result.userName").description("댓글 작성자"),
									fieldWithPath("result.comment").description("댓글 내용"),
									fieldWithPath("result.lastModifiedAt").description("댓글 수정일"))));

			verify(commentService).modifyComment(1L, 1L, commentRequest, "user");

		}
	}

	@Nested
	@DisplayName("댓글 삭제")
	class CommentDelete {

		CommentDeleteResponse commentDeleteResponse = new CommentDeleteResponse("댓글이 삭제되었습니다.");

		@Test
		@DisplayName("댓글 삭제 성공")
		void success() throws Exception {

			given(commentService.deleteComment(1L, 1L, "user"))
					.willReturn(commentDeleteResponse);

			mockMvc.perform(
					delete("/api/v1/records/{recordsId}/comments/{id}", 1L, 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result").exists())
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("recordsId").description("일기 번호"),
											parameterWithName("id").description("댓글 번호")
									),
									responseFields(
											fieldWithPath("resultCode").description("결과 코드"),
											fieldWithPath("result.message").description("댓글 삭제 메세지")
									)
							)
					);
			verify(commentService).deleteComment(1L, 1L, "user");
		}
	}

	@Nested
	@DisplayName("댓글 전체 조회")
	class GetAllComment {

		Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
		Page<CommentResponse> commentResponses = new PageImpl<>(
				Arrays.asList(new CommentResponse(1L, "user", "댓글", createdAt))
		);

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			given(commentService.getAllComments(1L, pageable))
					.willReturn(commentResponses);

			mockMvc.perform(
							get("/api/v1/records/{recordsId}/comments", 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.content").exists())
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("recordsId").description("일기 번호")),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.content[].id").description("댓글 등록 번호"),
									fieldWithPath("result.content[].userName").description("댓글 작성자"),
									fieldWithPath("result.content[].comment").description("댓글 내용"),
									fieldWithPath("result.content[].createdAt").description("등록 날짜"),

									fieldWithPath("result.pageable").description(
											"pageable"),
									fieldWithPath("result.last").description(
											"마지막 페이지인지 확인"),
									fieldWithPath("result.totalElements").description(
											"모든 페이지에 존재하는 총 게시글 수"),
									fieldWithPath("result.totalPages").description(
											"페이지로 제공되는 총 페이지 수"),
									fieldWithPath("result.size").description(
											"한 페이지에 조회할 게시글 수"),
									fieldWithPath("result.number").description("현재 페이지 번호"),
									fieldWithPath("result.sort.empty").description(
											"리스트가 비어있는지 여부 확인"),
									fieldWithPath("result.sort.unsorted").description(
											"정렬상태"),
									fieldWithPath("result.sort.sorted").description("정렬상태"),
									fieldWithPath("result.first").description(
											"첫번째 페이지인지 확인"),
									fieldWithPath("result.numberOfElements").description(
											"실제 데이터 개수"),
									fieldWithPath("result.empty").description(
											"리스트가 비어있는지 여부 확인"))));
			verify(commentService).getAllComments(1L, pageable);
		}
	}

	@Nested
	@DisplayName("내가 작성한 댓글 조회")
	class GetMyComment {

		Pageable pageable = PageRequest.of(0, 20, Sort.Direction.DESC, "createdAt");
		Page<CommentResponse> commentResponses = new PageImpl<>(
				Arrays.asList(new CommentResponse(1L, "user", "댓글", createdAt))
		);

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			given(commentService.getMyComments("user", pageable))
					.willReturn(commentResponses);

			mockMvc.perform(
							get("/api/v1/records/comments/my"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.content").exists())
					.andDo(restDocs.document(
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.content[].id").description("댓글 등록 번호"),
									fieldWithPath("result.content[].userName").description("댓글 작성자"),
									fieldWithPath("result.content[].comment").description("댓글 내용"),
									fieldWithPath("result.content[].createdAt").description("등록 날짜"),

									fieldWithPath("result.pageable").description(
											"pageable"),
									fieldWithPath("result.last").description(
											"마지막 페이지인지 확인"),
									fieldWithPath("result.totalElements").description(
											"모든 페이지에 존재하는 총 게시글 수"),
									fieldWithPath("result.totalPages").description(
											"페이지로 제공되는 총 페이지 수"),
									fieldWithPath("result.size").description(
											"한 페이지에 조회할 게시글 수"),
									fieldWithPath("result.number").description("현재 페이지 번호"),
									fieldWithPath("result.sort.empty").description(
											"리스트가 비어있는지 여부 확인"),
									fieldWithPath("result.sort.unsorted").description(
											"정렬상태"),
									fieldWithPath("result.sort.sorted").description("정렬상태"),
									fieldWithPath("result.first").description(
											"첫번째 페이지인지 확인"),
									fieldWithPath("result.numberOfElements").description(
											"실제 데이터 개수"),
									fieldWithPath("result.empty").description(
											"리스트가 비어있는지 여부 확인"))));
			verify(commentService).getMyComments("user", pageable);
		}
	}
}