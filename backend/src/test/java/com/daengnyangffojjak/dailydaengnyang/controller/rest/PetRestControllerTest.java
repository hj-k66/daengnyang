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
	@DisplayName("pet 등록")
	class addPet {

		PetAddRequest petAddRequest = new PetAddRequest("멍뭉이", Species.DOG, "진돗개", Sex.MALE,
				LocalDate.of(2022, 1, 1), 5.5); // LocalDate 생일
		PetAddResponse petAddResponse = new PetAddResponse(1l, "멍뭉이", "1살",
				LocalDateTime.of(2022, 1, 1, 1, 1)); // 생성 시간

		@Test
		@DisplayName("pet 등록 성공")
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
											parameterWithName("groupId").description("그룹 번호")
									),
									requestFields(
											fieldWithPath("name").description("펫 이름"),
											fieldWithPath("species").description("품종"),
											fieldWithPath("breed").description("종"),
											fieldWithPath("sex").description("성별"),
											fieldWithPath("birthday").description("생일"),
											fieldWithPath("weight").description("몸무게")
									),
									responseFields(
											fieldWithPath("resultCode").description("결과코드"),
											fieldWithPath("result.id").description("펫 번호"),
											fieldWithPath("result.name").description("펫 이름"),
											fieldWithPath("result.age").description("펫 나이"),
											fieldWithPath("result.createdAt").description("생성시간"))
							));

			verify(petService).add(1l, petAddRequest, "user");

		}

		@Test
		@DisplayName("pet 등록 실패 - 그룹이 없는 경우")
		void fail_그룹정보없음() throws Exception {

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
		@DisplayName("pet 등록 실패 - 생일을 현재보다 미래로 지정")
		void fail_생일범위오류() throws Exception {
			PetAddRequest birthday = new PetAddRequest("멍뭉이", Species.DOG, "진돗개", Sex.MALE,
					LocalDate.of(9999, 1, 1), 5.5); // LocalDate 생일

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
		@DisplayName("pet 등록 실패 - 그룹에 속하지 않은 사용자입니다.")
		void fail_그룹에_속해있지않음() throws Exception {

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
	@DisplayName("pet 조회")
	class showPet {

		PetShowResponse petShowResponse = new PetShowResponse(1l, "이름", Species.DOG, "종", Sex.MALE,
				LocalDate.of(2022, 1, 1), // 생일
				7.7,
				LocalDateTime.of(2022, 1, 1, 1, 1), // 생성 시간
				LocalDateTime.of(2022, 1, 1, 1, 1)); // 수정 시간

		@Test
		@DisplayName("pet 단건 조회 성공")
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
											parameterWithName("groupId").description("그룹 번호"),
											parameterWithName("id").description("펫 번호")
									),
									responseFields(
											fieldWithPath("resultCode").description("결과코드"),
											fieldWithPath("result").description(
													"그룹 내 반려동물 리스트"),
											fieldWithPath("result.id").description(
													"펫 번호"),
											fieldWithPath("result.name").description(
													"펫 이름"),
											fieldWithPath("result.species").description(
													"품종"),
											fieldWithPath("result.breed").description(
													"종"),
											fieldWithPath("result.sex").description("성별"),
											fieldWithPath("result.birthday").description(
													"생일"),
											fieldWithPath("result.weight").description(
													"몸무게"),
											fieldWithPath("result.createdAt").description(
													"생성시간"),
											fieldWithPath(
													"result.lastModifiedAt").description(
													"수정시간"))
							));

			verify(petService).show(1l, 1l, "user");

		}

		@Test
		@DisplayName("pet 단건 조회 실패 - 해당 그룹이 없음")
		void fail_해당그룹없음() throws Exception {

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
		@DisplayName("pet 단건 조회 실패 - 해당 펫이 없음")
		void fail_해당펫없음() throws Exception {

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
	@DisplayName("pet 수정")
	class updatePet {

		PetAddRequest update = new PetAddRequest("야옹이", Species.CAT, "길고양이", Sex.FEMALE,
				LocalDate.of(2022, 1, 1), 1.5); // 생일
		PetUpdateResponse petUpdateResponse = new PetUpdateResponse(1l, "야옹이", "1살",
				LocalDateTime.now()); // 수정 시간

		@Test
		@DisplayName("pet 수정 성공")
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
											parameterWithName("groupId").description("그룹 번호"),
											parameterWithName("id").description("펫 번호")
									),
									requestFields(
											fieldWithPath("name").description("펫 이름"),
											fieldWithPath("species").description("품종"),
											fieldWithPath("breed").description("종"),
											fieldWithPath("sex").description("성별"),
											fieldWithPath("birthday").description("생일"),
											fieldWithPath("weight").description("몸무게")

									),
									responseFields(
											fieldWithPath("resultCode").description("결과코드"),
											fieldWithPath("result.id").description("펫 번호"),
											fieldWithPath("result.name").description("펫 이름"),
											fieldWithPath("result.age").description("펫 나이"),
											fieldWithPath("result.lastModifiedAt").description(
													"수정시간"))
							));

			verify(petService).modify(1l, 1l, update, "user");

		}

		@Test
		@DisplayName("pet 수정 실패 - 수정 권한 없음")
		void fail_수정권한없음() throws Exception {

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
		@DisplayName("pet 수정 실패 - 펫 정보 없음")
		void fail_펫정보없음() throws Exception {

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
		@DisplayName("pet 수정 실패 - 그룹 정보 없음")
		void fail_그룹정보없음() throws Exception {

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
	@DisplayName("pet 삭제")
	class deletePet {

		PetDeleteResponse petDeleteResponse = new PetDeleteResponse("등록이 취소되었습니다.");


		@Test
		@DisplayName("pet 삭제 성공")
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
											parameterWithName("groupId").description("그룹 번호"),
											parameterWithName("id").description("펫 번호")
									),
									responseFields(
											fieldWithPath("resultCode").description("결과코드"),
											fieldWithPath("result.message").description(
													"펫 삭제 메세지"))
							));

			verify(petService).delete(1l, 1l, "user");

		}

		@Test
		@DisplayName("pet 삭제 실패 - 삭제 권한 없음")
		void fail_삭제권한없음() throws Exception {

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
		@DisplayName("pet 삭제 실패 - 펫 정보 없음")
		void fail_펫정보없음() throws Exception {

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
		@DisplayName("pet 삭제 실패 - 그룹 정보 없음")
		void fail_그룹정보없음() throws Exception {

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