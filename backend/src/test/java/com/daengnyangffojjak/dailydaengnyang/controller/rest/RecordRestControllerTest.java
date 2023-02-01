package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordResultRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordResultResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Category;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.RecordException;
import com.daengnyangffojjak.dailydaengnyang.service.RecordService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

@WebMvcTest(RecordRestController.class)
class RecordRestControllerTest extends ControllerTest {

	@MockBean
	RecordService recordService;

	LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 11, 11);
	LocalDateTime lastmodifiedAt = LocalDateTime.of(2023, 1, 2, 22, 22);

	RecordResultRequest recordResultRequest = new RecordResultRequest("제목", "본문", false,
			Category.WALK);

	// 일기 상세(1개) 조회
	RecordResponse recordResponse = new RecordResponse(1L, 1L, 1L, "제목", "본문", "user",
			Category.WALK, createdAt, lastmodifiedAt);

	Pageable pageable = PageRequest.of(0, 20, Direction.DESC, "createdAt");
	// 일기 작성
	RecordResultResponse createRecordResponse = new RecordResultResponse("일기 작성 완료", 1L);
	// 일기 수정
	RecordResultResponse modifyRecordResponse = new RecordResultResponse("일기 수정 완료", 1L);
	RecordResultRequest modiftyRecordRequest = new RecordResultRequest("바뀐 제목", "바뀐 본문", true,
			Category.TRAVEL);
	// 일기 삭제
	RecordResultResponse deleteRecordResponse = new RecordResultResponse("일기 삭제 완료", 1L);


	@Nested
	@DisplayName("일기 상세(1개) 조회")
	class GetOneRecord {

		@Test
		@DisplayName("일기 상세(1개) 조회 성공")
		void success_get_one_record() throws Exception {

			given(recordService.getOneRecord(1L, 1L, "user"))
					.willReturn(recordResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.get("/api/v1/pets/{petId}/records/{recordId}",
									1L, 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.id").value(1L))
					.andExpect(jsonPath("$.result.userId").value(1L))
					.andExpect(jsonPath("$.result.petId").value(1L))
					.andExpect(jsonPath("$.result.title").value("제목"))
					.andExpect(jsonPath("$.result.body").value("본문"))
					.andExpect(jsonPath("$.result.userName").value("user"))
					.andExpect(jsonPath("$.result.category").value("WALK"))
					.andExpect(jsonPath("$.result.createdAt").value("2023-01-01 11:11:00"))
					.andExpect(jsonPath("$.result.lastModifiedAt").value("2023-01-02 22:22:00"))
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("petId").description("반려동물 번호"),
											parameterWithName("recordId").description("일기 번호")
									),
									responseFields(
											fieldWithPath("resultCode").description("결과 코드"),
											fieldWithPath("result.id").description("일기 번호"),
											fieldWithPath("result.userId").description("작성자 번호"),
											fieldWithPath("result.petId").description("반려동물 번호"),
											fieldWithPath("result.title").description("제목"),
											fieldWithPath("result.body").description("본문"),
											fieldWithPath("result.userName").description("작성자"),
											fieldWithPath("result.category").description("카테고리"),
											fieldWithPath("result.createdAt").description(
													"일기 등록시간"),
											fieldWithPath("result.lastModifiedAt").description(
													"일기 수정시간")
									)
							)
					);

			verify(recordService).getOneRecord(1L, 1L, "user");
		}
	}

	@Nested
	@DisplayName("전체 피드 조회")
	class GetAllRecords {

		@Test
		@DisplayName("전체 피드 조회 성공")
		void success_get_all_records() throws Exception {

			List<RecordResponse> allRecords = List.of(RecordResponse.builder()
					.id(1L)
					.title("제목")
					.body("본문")
					.userName("user")
					.category(Category.WALK)
					.build());
			Page<RecordResponse> responses = new PageImpl<>(allRecords);

			given(recordService.getAllRecords(pageable)).willReturn(responses);

			mockMvc.perform(
							RestDocumentationRequestBuilders.get("/api/v1/records/feed"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.content").exists())
					.andExpect(jsonPath("$['result']['content'][0]['id']").value(1L))
					.andExpect(jsonPath("$['result']['content'][0]['title']").value("제목"))
					.andExpect(jsonPath("$['result']['content'][0]['body']").value("본문"))
					.andExpect(jsonPath("$['result']['content'][0]['userName']").value("user"))
					.andExpect(jsonPath("$['result']['content'][0]['category']").value("WALK"))
					.andDo(
							restDocs.document(
									responseFields(
											fieldWithPath("resultCode").description("결과코드"),
											fieldWithPath("result.content[].userId").description(
													"유저 번호"),
											fieldWithPath("result.content[].petId").description(
													"반려동물 번호"),
											fieldWithPath("result.content[].createdAt").description(
													"작성 날짜"),
											fieldWithPath(
													"result.content[].lastModifiedAt").description(
													"수정 날짜"),
											fieldWithPath(
													"['result']['content'][0].['id']").description(
													"일기번호"),
											fieldWithPath(
													"['result']['content'][0].['title']").description(
													"제목"),
											fieldWithPath(
													"['result']['content'][0].['body']").description(
													"내용"),
											fieldWithPath(
													"['result']['content'][0].['userName']").description(
													"user"),
											fieldWithPath(
													"['result']['content'][0].['category']").description(
													"카테고리"),
											fieldWithPath("result.last").description(
													"마지막 페이지인지 확인"),
											fieldWithPath("result.totalPages").description(
													"페이지로 제공되는 총 페이지 수"),
											fieldWithPath("result.totalElements").description(
													"모든 페이지에 존재하는 총 게시글 수"),
											fieldWithPath("result.size").description(
													"한 페이지에 조회할 게시글 수"),
											fieldWithPath("result.number").description("현재 페이지 번호"),
											fieldWithPath("result.pageable").description(
													"pageable"),
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
													"리스트가 비어있는지 여부 확인")
									)
							)
					);
		}
	}

	@Nested
	@DisplayName("일기 작성")
	class CreateRecord {

		@Test
		@DisplayName("일기 작성 성공")
		void success_create_record() throws Exception {

			given(recordService.createRecord(1L, recordResultRequest, "user"))
					.willReturn(createRecordResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.post("/api/v1/pets/{petId}/records", 1L)
									.content(objectMapper.writeValueAsBytes(recordResultRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.result.message").value("일기 작성 완료"))
					.andExpect(jsonPath("$.result.recordId").value(1L))
					.andDo(
							restDocs.document(
									pathParameters(parameterWithName("petId").description("반려동물 번호")
									),
									requestFields(
											fieldWithPath("title").description("제목"),
											fieldWithPath("body").description("본문"),
											fieldWithPath("isPublic").description("공유 여부"),
											fieldWithPath("category").description("카테고리")
									),
									responseFields(
											fieldWithPath("resultCode").description("결과코드"),
											fieldWithPath("result.message").description("결과메세지"),
											fieldWithPath("result.recordId").description("일기 번호"))
							)
					);

			verify(recordService).createRecord(1L, recordResultRequest, "user");
		}
	}

	@Nested
	@DisplayName("일기 수정")
	class ModifyRecord {

		@Test
		@DisplayName("일기 수정 성공")
		void success_modify_record() throws Exception {

			given(recordService.modifyRecord(1L, 1L, modiftyRecordRequest, "user"))
					.willReturn(modifyRecordResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.put("/api/v1//pets/{petId}/records/{recordId}",
											1L, 1L)
									.content(objectMapper.writeValueAsBytes(modiftyRecordRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.recordId").value(1L))
					.andExpect(jsonPath("$.result.message").value("일기 수정 완료"))
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("petId").description("반려동물 번호"),
											parameterWithName("recordId").description("일기 번호")
									),
									requestFields(
											fieldWithPath("title").description("제목"),
											fieldWithPath("body").description("본문"),
											fieldWithPath("isPublic").description("공유 여부"),
											fieldWithPath("category").description("카테고리")
									),
									responseFields(
											fieldWithPath("resultCode").description("결과코드"),
											fieldWithPath("result.message").description("결과메세지"),
											fieldWithPath("result.recordId").description("일기 번호"))
							)
					);

			verify(recordService).modifyRecord(1L, 1L, modiftyRecordRequest, "user");

		}
	}

	@Nested
	@DisplayName("일기 삭제")
	class DeleteRecord {

		@Test
		@DisplayName("일기 삭제 성공")
		void succes_delete_record() throws Exception {

			given(recordService.deleteRecord(1L, 1L, "user"))
					.willReturn(deleteRecordResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.delete(
									"/api/v1/pets/{petId}/records/{recordId}", 1L, 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.recordId").value(1L))
					.andExpect(jsonPath("$.result.message").value("일기 삭제 완료"))
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("petId").description("반려동물 번호"),
											parameterWithName("recordId").description("일기 번호")
									),
									responseFields(
											fieldWithPath("resultCode").description("결과코드"),
											fieldWithPath("result.message").description("결과메세지"),
											fieldWithPath("result.recordId").description("일기 번호"))
							)
					);

			verify(recordService).deleteRecord(1L, 1L, "user");
		}
	}
}