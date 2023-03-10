package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetShowResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetUpdateResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Sex;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.PetException;
import com.daengnyangffojjak.dailydaengnyang.service.PetService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PetRestController.class)
class PetRestControllerTest extends ControllerTest {

	@MockBean
	PetService petService;

	@Nested
	@DisplayName("pet ??????")
	class addPet {

		PetAddRequest petAddRequest = new PetAddRequest("?????????", Species.DOG, "?????????", Sex.MALE,
				LocalDate.of(2022, 1, 1), 5.5); // LocalDate ??????
		PetAddResponse petAddResponse = new PetAddResponse(1l, "?????????", "1???",
				LocalDateTime.of(2022, 1, 1, 1, 1)); // ?????? ??????

		@Test
		@DisplayName("pet ?????? ??????")
		void success() throws Exception {

			given(petService.add(1l, petAddRequest, "user")).willReturn(petAddResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.post("/api/v1/groups/{groupId}/pets", 1l)
									.with(csrf())
									.content(objectMapper.registerModule(new JavaTimeModule())
											.writeValueAsBytes(petAddRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andDo(print())
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result").exists())
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("groupId").description("?????? ??????")
									),
									requestFields(
											fieldWithPath("name").description("??? ??????"),
											fieldWithPath("species").description("??????"),
											fieldWithPath("breed").description("???"),
											fieldWithPath("sex").description("??????"),
											fieldWithPath("birthday").description("??????"),
											fieldWithPath("weight").description("?????????")
									),
									responseFields(
											fieldWithPath("resultCode").description("????????????"),
											fieldWithPath("result.id").description("??? ??????"),
											fieldWithPath("result.name").description("??? ??????"),
											fieldWithPath("result.age").description("??? ??????"),
											fieldWithPath("result.createdAt").description("????????????"))
							));

			verify(petService).add(1l, petAddRequest, "user");

		}

		@Test
		@DisplayName("pet ?????? ?????? - ????????? ?????? ??????")
		void fail_??????????????????() throws Exception {

			given(petService.add(1L, petAddRequest, "user"))
					.willThrow(new PetException(ErrorCode.GROUP_NOT_FOUND));

			mockMvc.perform(
							RestDocumentationRequestBuilders.post("/api/v1/groups/{groupId}/pets", 1l)
									.with(csrf())
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.registerModule(new JavaTimeModule())
											.writeValueAsBytes(petAddRequest))
					)
					.andDo(print())
					.andExpect(status().is(ErrorCode.GROUP_NOT_FOUND.getStatus().value()));

			verify(petService).add(1l, petAddRequest, "user");
		}

		@Test
		@DisplayName("pet ?????? ?????? - ????????? ???????????? ????????? ??????")
		void fail_??????????????????() throws Exception {
			PetAddRequest birthday = new PetAddRequest("?????????", Species.DOG, "?????????", Sex.MALE,
					LocalDate.of(9999, 1, 1), 5.5); // LocalDate ??????

			given(petService.add(1L, birthday, "user"))
					.willThrow(new PetException(ErrorCode.INVALID_BIRTHDAY));

			mockMvc.perform(
							RestDocumentationRequestBuilders.post("/api/v1/groups/{groupId}/pets", 1l)
									.with(csrf())
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.registerModule(new JavaTimeModule())
											.writeValueAsBytes(birthday))
					)
					.andDo(print())
					.andExpect(status().isBadRequest());

		}

		@Test
		@DisplayName("pet ?????? ?????? - ????????? ????????? ?????? ??????????????????.")
		void fail_?????????_??????????????????() throws Exception {

			given(petService.add(1L, petAddRequest, "user"))
					.willThrow(new PetException(ErrorCode.INVALID_PERMISSION));

			mockMvc.perform(
							RestDocumentationRequestBuilders.post("/api/v1/groups/{groupId}/pets", 1l)
									.with(csrf())
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.registerModule(new JavaTimeModule())
											.writeValueAsBytes(petAddRequest))
					)
					.andDo(print())
					.andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));

			verify(petService).add(1l, petAddRequest, "user");
		}
	}


	@Nested
	@DisplayName("pet ??????")
	class showPet {

		PetShowResponse petShowResponse = new PetShowResponse(1l, "??????", Species.DOG, "???", Sex.MALE,
				LocalDate.of(2022, 1, 1), // ??????
				7.7,
				LocalDateTime.of(2022, 1, 1, 1, 1), // ?????? ??????
				LocalDateTime.of(2022, 1, 1, 1, 1)); // ?????? ??????

		@Test
		@DisplayName("pet ?????? ?????? ??????")
		void success() throws Exception {

			given(petService.show(1l, 1l, "user")).willReturn(petShowResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.get("/api/v1/groups/{groupId}/pets/{id}", 1l,
									1l))
					.andDo(print())
					.andExpect(status().isOk())
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("groupId").description("?????? ??????"),
											parameterWithName("id").description("??? ??????")
									),
									responseFields(
											fieldWithPath("resultCode").description("????????????"),
											fieldWithPath("result").description(
													"?????? ??? ???????????? ?????????"),
											fieldWithPath("result.id").description(
													"??? ??????"),
											fieldWithPath("result.name").description(
													"??? ??????"),
											fieldWithPath("result.species").description(
													"??????"),
											fieldWithPath("result.breed").description(
													"???"),
											fieldWithPath("result.sex").description("??????"),
											fieldWithPath("result.birthday").description(
													"??????"),
											fieldWithPath("result.weight").description(
													"?????????"),
											fieldWithPath("result.createdAt").description(
													"????????????"),
											fieldWithPath(
													"result.lastModifiedAt").description(
													"????????????"))
							));

			verify(petService).show(1l, 1l, "user");

		}

		@Test
		@DisplayName("pet ?????? ?????? ?????? - ?????? ????????? ??????")
		void fail_??????????????????() throws Exception {

			given(petService.show(1l, 1l, "user"))
					.willThrow(new PetException(ErrorCode.GROUP_NOT_FOUND));

			mockMvc.perform(
							RestDocumentationRequestBuilders.get("/api/v1/groups/{groupId}/pets/{id}", 1l,
									1l))
					.andDo(print())
					.andExpect(status().is(ErrorCode.GROUP_NOT_FOUND.getStatus().value()));

			verify(petService).show(1l, 1l, "user");

		}

		@Test
		@DisplayName("pet ?????? ?????? ?????? - ?????? ?????? ??????")
		void fail_???????????????() throws Exception {

			given(petService.show(1l, 1l, "user"))
					.willThrow(new PetException(ErrorCode.PET_NOT_FOUND));

			mockMvc.perform(
							RestDocumentationRequestBuilders.get("/api/v1/groups/{groupId}/pets/{id}", 1l,
									1l))
					.andDo(print())
					.andExpect(status().is(ErrorCode.PET_NOT_FOUND.getStatus().value()));

			verify(petService).show(1l, 1l, "user");

		}
	}

	@Nested
	@DisplayName("pet ??????")
	class updatePet {

		PetAddRequest update = new PetAddRequest("?????????", Species.CAT, "????????????", Sex.FEMALE,
				LocalDate.of(2022, 1, 1), 1.5); // ??????
		PetUpdateResponse petUpdateResponse = new PetUpdateResponse(1l, "?????????", "1???",
				LocalDateTime.now()); // ?????? ??????

		@Test
		@DisplayName("pet ?????? ??????")
		void success() throws Exception {

			given(petService.modify(1l, 1l, update, "user"))
					.willReturn(petUpdateResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.put("/api/v1/groups/{groupId}/pets/{id}",
											1l, 1l)
									.with(csrf())
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.registerModule(new JavaTimeModule())
											.writeValueAsBytes(update))
					)
					.andDo(print())
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result").exists())
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("groupId").description("?????? ??????"),
											parameterWithName("id").description("??? ??????")
									),
									requestFields(
											fieldWithPath("name").description("??? ??????"),
											fieldWithPath("species").description("??????"),
											fieldWithPath("breed").description("???"),
											fieldWithPath("sex").description("??????"),
											fieldWithPath("birthday").description("??????"),
											fieldWithPath("weight").description("?????????")

									),
									responseFields(
											fieldWithPath("resultCode").description("????????????"),
											fieldWithPath("result.id").description("??? ??????"),
											fieldWithPath("result.name").description("??? ??????"),
											fieldWithPath("result.age").description("??? ??????"),
											fieldWithPath("result.lastModifiedAt").description(
													"????????????"))
							));

			verify(petService).modify(1l, 1l, update, "user");

		}

		@Test
		@DisplayName("pet ?????? ?????? - ?????? ?????? ??????")
		void fail_??????????????????() throws Exception {

			given(petService.modify(1l, 1l, update, "user"))
					.willThrow(new PetException(ErrorCode.INVALID_PERMISSION));

			mockMvc.perform(
							RestDocumentationRequestBuilders.put("/api/v1/groups/{groupId}/pets/{id}",
											1l,
											1l)
									.with(csrf())
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.registerModule(new JavaTimeModule())
											.writeValueAsBytes(update))
					)
					.andDo(print())
					.andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));

			verify(petService).modify(1l, 1l, update, "user");

		}

		@Test
		@DisplayName("pet ?????? ?????? - ??? ?????? ??????")
		void fail_???????????????() throws Exception {

			given(petService.modify(1l, 1l, update, "user"))
					.willThrow(new PetException(ErrorCode.PET_NOT_FOUND));

			mockMvc.perform(
							RestDocumentationRequestBuilders.put(
											"/api/v1/groups/{groupId}/pets/{id}",
											1l, 1l)
									.with(csrf())
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.registerModule(new JavaTimeModule())
											.writeValueAsBytes(update))
					)
					.andDo(print())
					.andExpect(status().is(ErrorCode.PET_NOT_FOUND.getStatus().value()));

			verify(petService).modify(1l, 1l, update, "user");

		}

		@Test
		@DisplayName("pet ?????? ?????? - ?????? ?????? ??????")
		void fail_??????????????????() throws Exception {

			given(petService.modify(1l, 1l, update, "user")).willThrow(
					new PetException(ErrorCode.GROUP_NOT_FOUND));

			mockMvc.perform(
							RestDocumentationRequestBuilders.put(
											"/api/v1/groups/{groupId}/pets/{id}",
											1l, 1l)
									.with(csrf())
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.registerModule(new JavaTimeModule())
											.writeValueAsBytes(update))
					)
					.andDo(print())
					.andExpect(status().is(ErrorCode.GROUP_NOT_FOUND.getStatus().value()));

			verify(petService).modify(1l, 1l, update, "user");

		}
	}

	@Nested
	@DisplayName("pet ??????")
	class deletePet {

		PetDeleteResponse petDeleteResponse = new PetDeleteResponse("????????? ?????????????????????.");


		@Test
		@DisplayName("pet ?????? ??????")
		void success() throws Exception {

			given(petService.delete(1l, 1l, "user")).willReturn(petDeleteResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.delete(
											"/api/v1/groups/{groupId}/pets/{id}",
											1l, 1l)
									.with(csrf())
									.contentType(MediaType.APPLICATION_JSON)
					)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result").exists())
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("groupId").description("?????? ??????"),
											parameterWithName("id").description("??? ??????")
									),
									responseFields(
											fieldWithPath("resultCode").description("????????????"),
											fieldWithPath("result.message").description(
													"??? ?????? ?????????"))
							));

			verify(petService).delete(1l, 1l, "user");

		}

		@Test
		@DisplayName("pet ?????? ?????? - ?????? ?????? ??????")
		void fail_??????????????????() throws Exception {

			given(petService.delete(1l, 1l, "user")).willThrow(
					new PetException(ErrorCode.INVALID_PERMISSION));

			mockMvc.perform(
							RestDocumentationRequestBuilders.delete(
											"/api/v1/groups/{groupId}/pets/{id}",
											1l, 1l)
									.with(csrf())
									.contentType(MediaType.APPLICATION_JSON)
					)
					.andDo(print())
					.andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));

			verify(petService).delete(1l, 1l, "user");

		}

		@Test
		@DisplayName("pet ?????? ?????? - ??? ?????? ??????")
		void fail_???????????????() throws Exception {

			given(petService.delete(1l, 1l, "user")).willThrow(
					new PetException(ErrorCode.PET_NOT_FOUND));

			mockMvc.perform(
							RestDocumentationRequestBuilders.delete(
											"/api/v1/groups/{groupId}/pets/{id}",
											1l, 1l)
									.with(csrf())
									.contentType(MediaType.APPLICATION_JSON)
					)
					.andDo(print())
					.andExpect(status().is(ErrorCode.PET_NOT_FOUND.getStatus().value()));

			verify(petService).delete(1l, 1l, "user");

		}

		@Test
		@DisplayName("pet ?????? ?????? - ?????? ?????? ??????")
		void fail_??????????????????() throws Exception {

			given(petService.delete(1l, 1l, "user")).willThrow(
					new PetException(ErrorCode.GROUP_NOT_FOUND));

			mockMvc.perform(
							RestDocumentationRequestBuilders.delete(
											"/api/v1/groups/{groupId}/pets/{id}",
											1l, 1l)
									.with(csrf())
									.contentType(MediaType.APPLICATION_JSON)
					)
					.andDo(print())
					.andExpect(status().is(ErrorCode.GROUP_NOT_FOUND.getStatus().value()));

			verify(petService).delete(1l, 1l, "user");

		}
	}


}