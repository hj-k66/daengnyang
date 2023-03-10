package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.*;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.ScheduleException;
import com.daengnyangffojjak.dailydaengnyang.service.ScheduleService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScheduleRestController.class)
class ScheduleRestControllerTest extends ControllerTest {

	@MockBean
	ScheduleService scheduleService;

	private JavaTimeModule javaTimeModule = new JavaTimeModule();

	//?????? ???????????? ?????? ???????????? -> ???????????? ??? ?????????????????? ?????? ?????? ????????? ??????
	LocalDateTime dateTime = LocalDateTime.of(2023, 1, 25, 10,26);

	//????????????
	ScheduleCreateRequest scheduleCreateRequest = new ScheduleCreateRequest(1L, "??????",
			"????????? ??????", 1L, "??????????????????", dateTime);

	//????????????
	ScheduleModifyRequest scheduleModifyRequest = new ScheduleModifyRequest(1L,
			"?????? ??????", "?????? ????????? ??????", 1L, "?????? ??????????????????", dateTime, true);

	//?????? ????????????
	ScheduleAssignRequest scheduleAssignRequest = new ScheduleAssignRequest("??????", "???????????? ?????????!");

	//?????? ????????????
	ScheduleCompleteRequest scheduleCompleteRequest = new ScheduleCompleteRequest(true);

	//------------------------------------------------------------------------------------------
	@Nested
	@DisplayName("?????? ???????????? ")
	class ScheduleComplete {

		@Test
		@DisplayName("?????? ???????????? ??????")
		void complete_success() throws Exception {
			ScheduleCompleteResponse scheduleCompleteResponse = new ScheduleCompleteResponse(
					"????????? ?????????????????????.", 1L);
			given(scheduleService.complete(1L, 1L, scheduleCompleteRequest, "user"))
					.willReturn(scheduleCompleteResponse);
			mockMvc.perform(
							RestDocumentationRequestBuilders.put(
											"/api/v1/pets/{petId}/schedules/{scheduleId}/completed", 1L, 1L)
									.with(csrf())
									.content(objectMapper.writeValueAsBytes(scheduleCompleteRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.result.message").value("????????? ?????????????????????."))
					.andExpect(jsonPath("$.result.id").value(1L))
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("petId").description("???????????? ??????"),
											parameterWithName("scheduleId").description("?????? ??????")

									),
									requestFields(
											fieldWithPath("completed").description("?????? ??????")
									),
									responseFields(
											fieldWithPath("resultCode").description("????????????"),
											fieldWithPath("result.message").description("???????????????"),
											fieldWithPath("result.id").description("?????? id")
									)
							));
			verify(scheduleService).complete(1L, 1L, scheduleCompleteRequest, "user");
		}
	}

	//------------------------------------------------------------------------------------------
	@Nested
	@DisplayName("??????????????????")
	class ScheduleAssign {

		@Test
		@DisplayName("?????????????????? ??????")
		void assign_success() throws Exception {
			MessageResponse messageResponse = new MessageResponse("????????? ???????????? ?????????????????????.");
			given(scheduleService.assign(1L, 1L, scheduleAssignRequest, "user"))
					.willReturn(messageResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.put(
											"/api/v1/pets/{petId}/schedules/{scheduleId}/assign", 1L, 1L)
									.with(csrf())
									.content(objectMapper.writeValueAsBytes(scheduleAssignRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.result.msg").value("????????? ???????????? ?????????????????????."))
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("petId").description("???????????? ??????"),
											parameterWithName("scheduleId").description("?????? ??????")

									),
									requestFields(
											fieldWithPath("receiverName").description("???????????? ??????"),
											fieldWithPath("message").description("?????? ?????????")
									),
									responseFields(
											fieldWithPath("resultCode").description("????????????"),
											fieldWithPath("result.msg").description("???????????????")
									)
							));
			verify(scheduleService).assign(1L, 1L, scheduleAssignRequest, "user");

		}
	}

	//------------------------------------------------------------------------------------------

	@Nested
	@DisplayName("????????????")
	class ScheduleCreate {

		@Test
		@DisplayName("???????????? ??????")
		void create_success() throws Exception {

			ScheduleCreateResponse scheduleCreateResponse = new ScheduleCreateResponse("?????? ?????? ??????",
					1L);

			given(scheduleService.create(1L, scheduleCreateRequest, "user"))
					.willReturn(scheduleCreateResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.post("/api/v1/pets/{petId}/schedules", 1L)
									//java 8 ?????? LocalDateTime??? ?????? ????????? ObjectMapper ????????? ???????????? ????????? ?????? ????????? ?????? ??????????????? ????????? ?????? ???????????? ????????? ?????? ??????
									.content(objectMapper.registerModule(javaTimeModule)
											.writeValueAsBytes(scheduleCreateRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.result.msg").value("?????? ?????? ??????"))
					.andExpect(jsonPath("$.result.id").value(1L))
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("petId").description("???????????? ??????")
									),
									requestFields(
											fieldWithPath("tagId").description("????????????"),
											fieldWithPath("title").description("??????"),
											fieldWithPath("body").description("??????"),
											fieldWithPath("assigneeId").description("????????? userId"),
											fieldWithPath("place").description("??????"),
											fieldWithPath("dueDate").description("????????????")
									),
									responseFields(
											fieldWithPath("resultCode").description("????????????"),
											fieldWithPath("result.msg").description("???????????????"),
											fieldWithPath("result.id").description("????????????"))
							)
					);
			verify(scheduleService).create(1L, scheduleCreateRequest, "user");
		}

		@Test
		@DisplayName("???????????? ?????? - ????????? ?????? ??????")
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
		@DisplayName("???????????? ?????? - ???????????? ?????? ??????????????? ??????")
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
					.andExpect(jsonPath("$.result.message").value("????????? ??????????????? ????????????."))
					.andDo(print());

			verify(scheduleService).create(1L, scheduleCreateRequest, "user");
		}

		@Test
		@DisplayName("???????????? ?????? - ????????? ????????? ?????? ??????")
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
					.andExpect(jsonPath("$.result.message").value("?????? ????????? ???????????? ????????????."))
					.andDo(print());

			verify(scheduleService).create(1L, scheduleCreateRequest, "user");
		}
	}

	//-------------------------------------------------------------------------------------------

	@Nested
	@DisplayName("????????????")
	class ScheduleModify {

		@Test
		@DisplayName("???????????? ??????")
		void modify_success() throws Exception {

			ScheduleModifyResponse scheduleModifyResponse = new ScheduleModifyResponse(1L, "?????? ??????",
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
					.andExpect(jsonPath("$.result.title").value("?????? ??????"))
					.andExpect(jsonPath("$.result.lastModifiedAt").value("2023-01-25 10:26:00"))

					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("petId").description("???????????? ??????"),
											parameterWithName("scheduleId").description("?????? ??????")
									),
									requestFields(
											fieldWithPath("tagId").description("????????????"),
											fieldWithPath("title").description("????????????"),
											fieldWithPath("body").description("????????????"),
											fieldWithPath("assigneeId").description(
													"????????? userId ??????"),
											fieldWithPath("place").description("????????????"),
											fieldWithPath("dueDate").description("??????????????????"),
											fieldWithPath("completed").description("?????? ?????? ??????")
									),
									responseFields(
											fieldWithPath("resultCode").description("????????????"),
											fieldWithPath("result.id").description("????????????"),
											fieldWithPath("result.title").description("???????????????"),
											fieldWithPath("result.lastModifiedAt").description(
													"???????????????")
									)
							)
					);
			verify(scheduleService).modify(1L, 1L, scheduleModifyRequest, "user");
		}

		@Test
		@DisplayName("???????????? ?????? - ????????? ?????? ??????")
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
		@DisplayName("???????????? ?????? - ???????????? ?????? ??????????????? ??????")
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
					.andExpect(jsonPath("$.result.message").value("????????? ??????????????? ????????????."))
					.andDo(print());

			verify(scheduleService).modify(1L, 1L, scheduleModifyRequest, "user");
		}

		@Test
		@DisplayName("???????????? ?????? - ????????? ????????? ?????? ??????")
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
					.andExpect(jsonPath("$.result.message").value("?????? ????????? ???????????? ????????????."))
					.andDo(print());

			verify(scheduleService).modify(1L, 1L, scheduleModifyRequest, "user");
		}

		@Test
		@DisplayName("???????????? ?????? - ????????? ????????? ?????? ??????")
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
					.andExpect(jsonPath("$.result.message").value("????????? ????????? ????????????."))
					.andDo(print());

			verify(scheduleService).modify(1L, 1L, scheduleModifyRequest, "user");
		}

		@Test
		@DisplayName("???????????? ?????? - ??????????????? != ????????????")
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
					.andExpect(jsonPath("$.result.message").value("???????????? ????????? ????????????."))
					.andDo(print());

			verify(scheduleService).modify(1L, 1L, scheduleModifyRequest, "user");
		}

	}

	//-------------------------------------------------------------------------------------------

	@Nested
	@DisplayName("????????????")
	class ScheduleDelete {

		@Test
		@DisplayName("???????????? ??????")
		void delete_success() throws Exception {

			ScheduleDeleteResponse scheduleDeleteResponse = new ScheduleDeleteResponse(
					"????????? ?????????????????????.");

			given(scheduleService.delete(1L, "user"))
					.willReturn(scheduleDeleteResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.delete(
									"/api/v1/pets/{petId}/schedules/{scheduleId}", 1L, 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.msg").value("????????? ?????????????????????."))
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("petId").description("???????????? ??????"),
											parameterWithName("scheduleId").description("?????? ??????")
									),
									responseFields(
											fieldWithPath("resultCode").description("????????????"),
											fieldWithPath("result.msg").description("???????????????")
									)
							)
					);
			verify(scheduleService).delete(1L, "user");
		}

		@Test
		@DisplayName("???????????? ?????? - ????????? ?????? ??????")
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
		@DisplayName("???????????? ?????? - ????????? ????????? ?????? ??????")
		void delete_fail_schedule_not_found() throws Exception {
			given(scheduleService.delete(1L, "user"))
					.willThrow(new ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND));

			mockMvc.perform(
							delete("/api/v1/pets/1/schedules/1"))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.result.errorCode").value("SCHEDULE_NOT_FOUND"))
					.andExpect(jsonPath("$.result.message").value("????????? ????????? ????????????."))
					.andDo(print());

			verify(scheduleService).delete(1L, "user");
		}

		@Test
		@DisplayName("???????????? ?????? - ??????????????? != ????????????")
		void delete_fail_invalid_permission() throws Exception {
			given(scheduleService.delete(1L, "user"))
					.willThrow(new ScheduleException(ErrorCode.INVALID_PERMISSION));

			mockMvc.perform(
							delete("/api/v1/pets/1/schedules/1"))
					.andExpect(status().isUnauthorized())
					.andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"))
					.andExpect(jsonPath("$.result.message").value("???????????? ????????? ????????????."))
					.andDo(print());

			verify(scheduleService).delete(1L, "user");
		}

	}

	//-------------------------------------------------------------------------------------------

	@Nested
	@DisplayName("???????????? - ????????????(??????)")
	class ScheduleGet {

		@Test
		@DisplayName("?????????????????? ??????")
		void get_success() throws Exception {
			//??????????????????(??????)
			ScheduleResponse scheduleResponse = new ScheduleResponse(1L, "??????", 1L, "user", 1L,
					"pet",
					"??????", "????????? ??????", 1L, "??????", "??????????????????", dateTime, false, dateTime, dateTime);

			given(scheduleService.get(1L, 1L, "user"))
					.willReturn(scheduleResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.get(
									"/api/v1/pets/{petId}/schedules/{scheduleId}", 1L, 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.id").value(1L))
					.andExpect(jsonPath("$.result.tag").value("??????"))
					.andExpect(jsonPath("$.result.userId").value(1L))
					.andExpect(jsonPath("$.result.userName").value("user"))
					.andExpect(jsonPath("$.result.petId").value(1L))
					.andExpect(jsonPath("$.result.petName").value("pet"))
					.andExpect(jsonPath("$.result.title").value("??????"))
					.andExpect(jsonPath("$.result.body").value("????????? ??????"))
					.andExpect(jsonPath("$.result.assigneeId").value(1L))
					.andExpect(jsonPath("$.result.roleInGroup").value("??????"))
					.andExpect(jsonPath("$.result.place").value("??????????????????"))
					.andExpect(jsonPath("$.result.dueDate").value("2023-01-25 10:26:00"))
					.andExpect(jsonPath("$.result.completed").value(false))
					.andExpect(jsonPath("$.result.createdAt").value("2023-01-25 10:26:00"))
					.andExpect(jsonPath("$.result.lastModifiedAt").value("2023-01-25 10:26:00"))
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("petId").description("???????????? ??????"),
											parameterWithName("scheduleId").description("?????? ??????")
									),
									responseFields(
											fieldWithPath("resultCode").description("????????????"),
											fieldWithPath("result.id").description("????????????"),
											fieldWithPath("result.tag").description("??????"),
											fieldWithPath("result.userId").description(
													"????????? userId"),
											fieldWithPath("result.userName").description(
													"????????? name"),
											fieldWithPath("result.petId").description("???????????? ??????"),
											fieldWithPath("result.petName").description("???????????? ??????"),
											fieldWithPath("result.title").description("??????"),
											fieldWithPath("result.body").description("??????"),
											fieldWithPath("result.assigneeId").description(
													"????????? userId"),
											fieldWithPath("result.roleInGroup").description(
													"????????? ????????? ?????? ??????"),
											fieldWithPath("result.place").description("??????"),
											fieldWithPath("result.dueDate").description("????????????"),
											fieldWithPath("result.completed").description(
													"?????? ?????? ??????"),
											fieldWithPath("result.createdAt").description("??????????????????"),
											fieldWithPath("result.lastModifiedAt").description(
													"??????????????????")

									)
							)
					);
			verify(scheduleService).get(1L, 1L, "user");
		}

		@Test
		@DisplayName("?????????????????? ?????? - ????????? ?????? ??????")
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
		@DisplayName("?????????????????? ?????? - ????????? ????????? ?????? ??????")
		void get_fail_schedule_not_found() throws Exception {
			given(scheduleService.get(1L, 1L, "user"))
					.willThrow(new ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND));

			mockMvc.perform(
							get("/api/v1/pets/1/schedules/1"))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.result.errorCode").value("SCHEDULE_NOT_FOUND"))
					.andExpect(jsonPath("$.result.message").value("????????? ????????? ????????????."))
					.andDo(print());

			verify(scheduleService).get(1L, 1L, "user");
		}
	}

	@Nested
	@DisplayName("???????????? - ???????????????(??????)")
	class ScheduleList {

		@Test
		void list_success() throws Exception {
			//???????????????????????????
			Pageable pageable = PageRequest.of(0, 20, Sort.Direction.DESC, "dueDate");

			Page<ScheduleListResponse> scheduleListResponsePage = new PageImpl<>(
					Arrays.asList(new ScheduleListResponse(1L, "??????", "title", "body", 1L, "??????",
							"?????? ????????????", dateTime, false)));

			given(scheduleService.list(1L, "user", pageable)).willReturn(scheduleListResponsePage);

			mockMvc.perform(
							RestDocumentationRequestBuilders.get("/api/v1/pets/{petId}/schedules", 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.content").exists())
					.andExpect(jsonPath("$['result']['content'][0]['tag']").value("??????"))
					.andExpect(jsonPath("$['result']['content'][0]['title']").value("title"))
					.andExpect(jsonPath("$['result']['content'][0]['body']").value("body"))
					.andExpect(jsonPath("$['result']['content'][0]['assigneeId']").value(1L))
					.andExpect(jsonPath("$['result']['content'][0]['roleInGroup']").value("??????"))
					.andExpect(jsonPath("$['result']['content'][0]['place']").value("?????? ????????????"))
					.andExpect(jsonPath("$['result']['content'][0]['dueDate']").value(
							"2023-01-25 10:26:00"))
					.andExpect(jsonPath("$['result']['content'][0]['completed']").value(false))
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("petId").description("???????????? ??????")
									),
									responseFields(
											fieldWithPath("resultCode").description("????????????"),
											fieldWithPath(
													"['result']['content'][0].['id']").description(
													"?????? ????????????"),
											fieldWithPath(
													"['result']['content'][0].['tag']").description(
													"??????"),
											fieldWithPath(
													"['result']['content'][0].['title']").description(
													"??????"),
											fieldWithPath(
													"['result']['content'][0].['body']").description(
													"??????"),
											fieldWithPath(
													"['result']['content'][0].['assigneeId']").description(
													"????????? userId"),
											fieldWithPath(
													"['result']['content'][0].['roleInGroup']").description(
													"????????? ????????? ??????"),
											fieldWithPath(
													"['result']['content'][0].['place']").description(
													"??????"),
											fieldWithPath(
													"['result']['content'][0].['dueDate']").description(
													"????????????"),
											fieldWithPath(
													"['result']['content'][0].['completed']").description(
													"?????? ?????? ??????"),
											fieldWithPath("result.last").description(
													"????????? ??????????????? ??????"),
											fieldWithPath("result.totalPages").description(
													"???????????? ???????????? ??? ????????? ???"),
											fieldWithPath("result.totalElements").description(
													"?????? ???????????? ???????????? ??? ????????? ???"),
											fieldWithPath("result.size").description(
													"??? ???????????? ????????? ????????? ???"),
											fieldWithPath("result.number").description("?????? ????????? ??????"),
											fieldWithPath("result.pageable").description(
													"pageable"),
											fieldWithPath("result.sort.empty").description(
													"???????????? ??????????????? ?????? ??????"),
											fieldWithPath("result.sort.unsorted").description(
													"????????????"),
											fieldWithPath("result.sort.sorted").description("????????????"),
											fieldWithPath("result.first").description(
													"????????? ??????????????? ??????"),
											fieldWithPath("result.numberOfElements").description(
													"?????? ????????? ??????"),
											fieldWithPath("result.empty").description(
													"???????????? ??????????????? ?????? ??????")
									)
							)
					);
		}
	}

	@Nested
	@DisplayName("???????????? - ???????????????(?????????)")
	class ScheduleListPeroid {

		@Test
		void list_success() throws Exception {
			//???????????????????????????
			List<ScheduleListResponse> response = Arrays.asList(new ScheduleListResponse(1L, "??????", "title", "body", 1L, "??????",
							"?????? ????????????", dateTime, false));

			given(scheduleService.getScheduleList(1L, "20230101", "20230131", "user")).willReturn(response);

			mockMvc.perform(
							RestDocumentationRequestBuilders.get("/api/v1/pets/{petId}/schedules/period?fromDate=20230101&toDate=20230131", 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result").exists())
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("petId").description("???????????? ??????")
									),
									responseFields(
											fieldWithPath("resultCode").description("????????????"),
											fieldWithPath("result").description("?????? ?????????"),
											fieldWithPath(
													"result[].id").description("?????? ????????????"),
											fieldWithPath(
													"result[].tag").description("??????"),
											fieldWithPath(
													"result[].title").description("??????"),
											fieldWithPath(
													"result[].body").description("??????"),
											fieldWithPath(
													"result[].assigneeId").description("????????? userId"),
											fieldWithPath(
													"result[].roleInGroup").description("????????? ????????? ??????"),
											fieldWithPath(
													"result[].place").description("??????"),
											fieldWithPath(
													"result[].dueDate").description("????????????"),
											fieldWithPath(
													"result[].completed").description("?????? ?????? ??????")
									)
							)
					);
		}
	}
}

