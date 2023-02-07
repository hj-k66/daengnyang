package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.*;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.ScheduleException;
import com.daengnyangffojjak.dailydaengnyang.service.ScheduleService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Arrays;
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
import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScheduleRestController.class)
class ScheduleRestControllerTest extends ControllerTest {

	@MockBean
	ScheduleService scheduleService;

	private JavaTimeModule javaTimeModule = new JavaTimeModule();

	//일정 등록시간 미리 지정해둠 -> 테스트할 때 현재시간으로 되어 시간 안맞음 해결
	LocalDateTime dateTime = LocalDateTime.of(2023, 1, 25, 10, 26);

	//일정등록
	ScheduleCreateRequest scheduleCreateRequest = new ScheduleCreateRequest(1L, "병원",
			"초음파 재검", 1L, "멋사동물병원", dateTime);

	//일정수정
	ScheduleModifyRequest scheduleModifyRequest = new ScheduleModifyRequest(1L,
			"수정 병원", "수정 초음파 재검", 1L, "수정 멋사동물병원", dateTime, true);

	//------------------------------------------------------------------------------------------

	@Nested
	@DisplayName("일정등록")
	class ScheduleCreate {

		@Test
		@DisplayName("일정등록 성공")
		void create_success() throws Exception {

			ScheduleCreateResponse scheduleCreateResponse = new ScheduleCreateResponse("일정 등록 완료",
					1L);

			given(scheduleService.create(1L, scheduleCreateRequest, "user"))
					.willReturn(scheduleCreateResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.post("/api/v1/pets/{petId}/schedules", 1L)
									//java 8 부터 LocalDateTime을 가진 객체를 ObjectMapper 함수를 사용하여 가져올 경우 직렬화 또는 역직렬화를 못하는 에러 발생으로 아래의 코드 작성
									.content(objectMapper.registerModule(javaTimeModule)
											.writeValueAsBytes(scheduleCreateRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.result.msg").value("일정 등록 완료"))
					.andExpect(jsonPath("$.result.id").value(1L))
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("petId").description("반려동물 번호")
									),
									requestFields(
											fieldWithPath("tagId").description("태그번호"),
											fieldWithPath("title").description("제목"),
											fieldWithPath("body").description("내용"),
											fieldWithPath("assigneeId").description("책임자"),
											fieldWithPath("place").description("장소"),
											fieldWithPath("dueDate").description("예정날짜")
									),
									responseFields(
											fieldWithPath("resultCode").description("결과코드"),
											fieldWithPath("result.msg").description("결과메세지"),
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
									.content(objectMapper.registerModule(javaTimeModule)
											.writeValueAsBytes(scheduleCreateRequest))
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
									.content(objectMapper.registerModule(javaTimeModule)
											.writeValueAsBytes(scheduleCreateRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.result.errorCode").value("PET_NOT_FOUND"))
					.andExpect(jsonPath("$.result.message").value("등록된 반려동물이 아닙니다."))
					.andDo(print());

			verify(scheduleService).create(1L, scheduleCreateRequest, "user");
		}

		@Test
		@DisplayName("일정등록 실패 - 등록된 태그가 없는 경우")
		void create_fail_tag_not_found() throws Exception {
			given(scheduleService.create(1L, scheduleCreateRequest, "user"))
					.willThrow(new ScheduleException(ErrorCode.TAG_NOT_FOUND));

			mockMvc.perform(
							post("/api/v1/pets/1/schedules")
									.content(objectMapper.registerModule(javaTimeModule)
											.writeValueAsBytes(scheduleCreateRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.result.errorCode").value("TAG_NOT_FOUND"))
					.andExpect(jsonPath("$.result.message").value("해당 태그가 존재하지 않습니다."))
					.andDo(print());

			verify(scheduleService).create(1L, scheduleCreateRequest, "user");
		}
	}

	//-------------------------------------------------------------------------------------------

	@Nested
	@DisplayName("일정수정")
	class ScheduleModify {

		@Test
		@DisplayName("일정수정 성공")
		void modify_success() throws Exception {

			ScheduleModifyResponse scheduleModifyResponse = new ScheduleModifyResponse(1L, "수정 병원",
					dateTime);

			given(scheduleService.modify(1L, 1L, scheduleModifyRequest, "user"))
					.willReturn(scheduleModifyResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.put(
											"/api/v1/pets/{petId}/schedules/{scheduleId}", 1L, 1L)
									.content(objectMapper.registerModule(javaTimeModule)
											.writeValueAsBytes(scheduleModifyRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.result.id").value(1L))
					.andExpect(jsonPath("$.result.title").value("수정 병원"))
					.andExpect(jsonPath("$.result.lastModifiedAt").value("2023-01-25 10:26:00"))

					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("petId").description("반려동물 번호"),
											parameterWithName("scheduleId").description("일정 번호")
									),
									requestFields(
											fieldWithPath("tagId").description("태그수정"),
											fieldWithPath("title").description("제목수정"),
											fieldWithPath("body").description("내용수정"),
											fieldWithPath("assigneeId").description(
													"책임자 userId 수정"),
											fieldWithPath("place").description("장소수정"),
											fieldWithPath("dueDate").description("예정날짜수정"),
											fieldWithPath("completed").description("일정 완료 여부")
									),
									responseFields(
											fieldWithPath("resultCode").description("결과코드"),
											fieldWithPath("result.id").description("일정번호"),
											fieldWithPath("result.title").description("수정한제목"),
											fieldWithPath("result.lastModifiedAt").description(
													"수정한날짜")
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
									.content(objectMapper.registerModule(javaTimeModule)
											.writeValueAsBytes(scheduleModifyRequest))
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
									.content(objectMapper.registerModule(javaTimeModule)
											.writeValueAsBytes(scheduleModifyRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.result.errorCode").value("PET_NOT_FOUND"))
					.andExpect(jsonPath("$.result.message").value("등록된 반려동물이 아닙니다."))
					.andDo(print());

			verify(scheduleService).modify(1L, 1L, scheduleModifyRequest, "user");
		}

		@Test
		@DisplayName("일정수정 실패 - 등록된 태그가 없는 경우")
		void modify_fail_tag_not_found() throws Exception {
			given(scheduleService.modify(1L, 1L, scheduleModifyRequest, "user"))
					.willThrow(new ScheduleException(ErrorCode.TAG_NOT_FOUND));

			mockMvc.perform(
							put("/api/v1/pets/1/schedules/1")
									.content(objectMapper.registerModule(javaTimeModule)
											.writeValueAsBytes(scheduleModifyRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.result.errorCode").value("TAG_NOT_FOUND"))
					.andExpect(jsonPath("$.result.message").value("해당 태그가 존재하지 않습니다."))
					.andDo(print());

			verify(scheduleService).modify(1L, 1L, scheduleModifyRequest, "user");
		}

		@Test
		@DisplayName("일정수정 실패 - 등록된 일정이 없는 경우")
		void modify_fail_schedule_not_found() throws Exception {
			given(scheduleService.modify(1L, 1L, scheduleModifyRequest, "user"))
					.willThrow(new ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND));

			mockMvc.perform(
							put("/api/v1/pets/1/schedules/1")
									.content(objectMapper.registerModule(javaTimeModule)
											.writeValueAsBytes(scheduleModifyRequest))
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
									.content(objectMapper.registerModule(javaTimeModule)
											.writeValueAsBytes(scheduleModifyRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isUnauthorized())
					.andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"))
					.andExpect(jsonPath("$.result.message").value("사용자가 권한이 없습니다."))
					.andDo(print());

			verify(scheduleService).modify(1L, 1L, scheduleModifyRequest, "user");
		}

	}

	//-------------------------------------------------------------------------------------------

	@Nested
	@DisplayName("일정삭제")
	class ScheduleDelete {

		@Test
		@DisplayName("일정삭제 성공")
		void delete_success() throws Exception {

			ScheduleDeleteResponse scheduleDeleteResponse = new ScheduleDeleteResponse(
					"일정이 삭제되었습니다.");

			given(scheduleService.delete(1L, "user"))
					.willReturn(scheduleDeleteResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.delete(
									"/api/v1/pets/{petId}/schedules/{scheduleId}", 1L, 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.msg").value("일정이 삭제되었습니다."))
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("petId").description("반려동물 번호"),
											parameterWithName("scheduleId").description("일정 번호")
									),
									responseFields(
											fieldWithPath("resultCode").description("결과코드"),
											fieldWithPath("result.msg").description("결과메시지")
									)
							)
					);
			verify(scheduleService).delete(1L, "user");
		}

		@Test
		@DisplayName("일정삭제 실패 - 유저가 없는 경우")
		void delete_fail_username_not_found() throws Exception {
			given(scheduleService.delete(1L, "user"))
					.willThrow(new ScheduleException(ErrorCode.USERNAME_NOT_FOUND));

			mockMvc.perform(
							delete("/api/v1/pets/1/schedules/1"))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.result.errorCode").value("USERNAME_NOT_FOUND"))
					.andExpect(jsonPath("$.result.message").value("Not founded"))
					.andDo(print());

			verify(scheduleService).delete(1L, "user");
		}

		@Test
		@DisplayName("일정삭제 실패 - 등록된 일정이 없는 경우")
		void delete_fail_schedule_not_found() throws Exception {
			given(scheduleService.delete(1L, "user"))
					.willThrow(new ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND));

			mockMvc.perform(
							delete("/api/v1/pets/1/schedules/1"))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.result.errorCode").value("SCHEDULE_NOT_FOUND"))
					.andExpect(jsonPath("$.result.message").value("등록된 일정이 없습니다."))
					.andDo(print());

			verify(scheduleService).delete(1L, "user");
		}

		@Test
		@DisplayName("일정삭제 실패 - 로그인유저 != 작성유저")
		void delete_fail_invalid_permission() throws Exception {
			given(scheduleService.delete(1L, "user"))
					.willThrow(new ScheduleException(ErrorCode.INVALID_PERMISSION));

			mockMvc.perform(
							delete("/api/v1/pets/1/schedules/1"))
					.andExpect(status().isUnauthorized())
					.andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"))
					.andExpect(jsonPath("$.result.message").value("사용자가 권한이 없습니다."))
					.andDo(print());

			verify(scheduleService).delete(1L, "user");
		}

	}

	//-------------------------------------------------------------------------------------------

	@Nested
	@DisplayName("일정조회 - 상세조회(단건)")
	class ScheduleGet {

		@Test
		@DisplayName("일정상세조회 성공")
		void get_success() throws Exception {
			//일정상세조회(단건)
			ScheduleResponse scheduleResponse = new ScheduleResponse(1L, "일상", 1L, "user", 1L,
					"pet",
					"병원", "초음파 재검", 1L, "멋사동물병원", dateTime, false, dateTime, dateTime);

			given(scheduleService.get(1L, 1L, "user"))
					.willReturn(scheduleResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.get(
									"/api/v1/pets/{petId}/schedules/{scheduleId}", 1L, 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.id").value(1L))
					.andExpect(jsonPath("$.result.tag").value("일상"))
					.andExpect(jsonPath("$.result.userId").value(1L))
					.andExpect(jsonPath("$.result.userName").value("user"))
					.andExpect(jsonPath("$.result.petId").value(1L))
					.andExpect(jsonPath("$.result.petName").value("pet"))
					.andExpect(jsonPath("$.result.title").value("병원"))
					.andExpect(jsonPath("$.result.body").value("초음파 재검"))
					.andExpect(jsonPath("$.result.assigneeId").value(1L))
					.andExpect(jsonPath("$.result.place").value("멋사동물병원"))
					.andExpect(jsonPath("$.result.dueDate").value("2023-01-25 10:26:00"))
					.andExpect(jsonPath("$.result.completed").value(false))
					.andExpect(jsonPath("$.result.createdAt").value("2023-01-25 10:26:00"))
					.andExpect(jsonPath("$.result.lastModifiedAt").value("2023-01-25 10:26:00"))
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("petId").description("반려동물 번호"),
											parameterWithName("scheduleId").description("일정 번호")
									),
									responseFields(
											fieldWithPath("resultCode").description("결과코드"),
											fieldWithPath("result.id").description("일정번호"),
											fieldWithPath("result.tag").description("태그"),
											fieldWithPath("result.userId").description(
													"작성자 userId"),
											fieldWithPath("result.userName").description(
													"작성자 name"),
											fieldWithPath("result.petId").description("반려동물 번호"),
											fieldWithPath("result.petName").description("반려동물 이름"),
											fieldWithPath("result.title").description("제목"),
											fieldWithPath("result.body").description("내용"),
											fieldWithPath("result.assigneeId").description("책임자"),
											fieldWithPath("result.place").description("장소"),
											fieldWithPath("result.dueDate").description("예정날짜"),
											fieldWithPath("result.completed").description(
													"일정 완료 여부"),
											fieldWithPath("result.createdAt").description("일정등록시간"),
											fieldWithPath("result.lastModifiedAt").description(
													"일정수정시간")

									)
							)
					);
			verify(scheduleService).get(1L, 1L, "user");
		}

		@Test
		@DisplayName("일정상세조회 실패 - 유저가 없는 경우")
		void get_fail_username_not_found() throws Exception {
			given(scheduleService.get(1L, 1L, "user"))
					.willThrow(new ScheduleException(ErrorCode.USERNAME_NOT_FOUND));

			mockMvc.perform(
							get("/api/v1/pets/1/schedules/1"))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.result.errorCode").value("USERNAME_NOT_FOUND"))
					.andExpect(jsonPath("$.result.message").value("Not founded"))
					.andDo(print());

			verify(scheduleService).get(1L, 1L, "user");
		}

		@Test
		@DisplayName("일정상세조회 실패 - 등록된 일정이 없는 경우")
		void get_fail_schedule_not_found() throws Exception {
			given(scheduleService.get(1L, 1L, "user"))
					.willThrow(new ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND));

			mockMvc.perform(
							get("/api/v1/pets/1/schedules/1"))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.result.errorCode").value("SCHEDULE_NOT_FOUND"))
					.andExpect(jsonPath("$.result.message").value("등록된 일정이 없습니다."))
					.andDo(print());

			verify(scheduleService).get(1L, 1L, "user");
		}
	}

	@Nested
	@DisplayName("일정조회 - 개체별조회(전체)")
	class ScheduleList {

		@Test
		void list_success() throws Exception {
			//개체별일정전체조회
			Pageable pageable = PageRequest.of(0, 20, Sort.Direction.DESC, "dueDate");

			Page<ScheduleListResponse> scheduleListResponsePage = new PageImpl<>(
					Arrays.asList(new ScheduleListResponse("일상", "title", "body", 1L,
							"멋사 동물병원", dateTime, false)));

			given(scheduleService.list(1L, "user", pageable)).willReturn(scheduleListResponsePage);

			mockMvc.perform(
							RestDocumentationRequestBuilders.get("/api/v1/pets/{petId}/schedules", 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.content").exists())
					.andExpect(jsonPath("$['result']['content'][0]['tag']").value("일상"))
					.andExpect(jsonPath("$['result']['content'][0]['title']").value("title"))
					.andExpect(jsonPath("$['result']['content'][0]['body']").value("body"))
					.andExpect(jsonPath("$['result']['content'][0]['assigneeId']").value(1L))
					.andExpect(jsonPath("$['result']['content'][0]['place']").value("멋사 동물병원"))
					.andExpect(jsonPath("$['result']['content'][0]['dueDate']").value(
							"2023-01-25 10:26:00"))
					.andExpect(jsonPath("$['result']['content'][0]['completed']").value(false))
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("petId").description("반려동물 번호")
									),
									responseFields(
											fieldWithPath("resultCode").description("결과코드"),
											fieldWithPath(
													"['result']['content'][0].['tag']").description(
													"태그"),
											fieldWithPath(
													"['result']['content'][0].['title']").description(
													"제목"),
											fieldWithPath(
													"['result']['content'][0].['body']").description(
													"내용"),
											fieldWithPath(
													"['result']['content'][0].['assigneeId']").description(
													"책임자"),
											fieldWithPath(
													"['result']['content'][0].['place']").description(
													"장소"),
											fieldWithPath(
													"['result']['content'][0].['dueDate']").description(
													"예정날짜"),
											fieldWithPath(
													"['result']['content'][0].['completed']").description(
													"일정 완료 여부"),
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
}

