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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.disease.DizGetResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.disease.DizWriteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.disease.DizWriteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.DiseaseCategory;
import com.daengnyangffojjak.dailydaengnyang.service.DiseaseService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

@WebMvcTest(DiseaseRestController.class)
class DiseaseRestControllerTest extends ControllerTest {

	@MockBean
	protected DiseaseService diseaseService;
	private JavaTimeModule javaTimeModule = new JavaTimeModule();

	@Nested
	@DisplayName("질병 등록")
	class DiseaseCreate {

		DizWriteRequest request = DizWriteRequest.builder().name("질병")
				.category(DiseaseCategory.DERMATOLOGY)
				.startedAt(LocalDate.of(2023, 1, 1)).endedAt(LocalDate.of(2023, 1, 31)).build();

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			given(diseaseService.create(1L, request, "user")).willReturn(
					new DizWriteResponse(1L, "펫이름", "질병"));

			mockMvc.perform(
							post("/api/v1/pets/{petId}/diseases", 1L)
									.content(objectMapper.registerModule(javaTimeModule)
											.writeValueAsBytes(request))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.id").value(1L))
					.andDo(restDocs.document(
							pathParameters(parameterWithName("petId").description("반려동물 번호")),
							requestFields(fieldWithPath("name").description("질병 이름"),
									fieldWithPath("category").optional().description("진료 과목"),
									fieldWithPath("startedAt").optional().description("진단 날짜")
											.attributes(field("constraints", "오늘 이전만 가능")),
									fieldWithPath("endedAt").optional().description("종료 날짜")

							),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.id").description("질병 등록 번호"),
									fieldWithPath("result.petName").description("반려동물 이름"),
									fieldWithPath("result.name").description("질병 이름"))));
			verify(diseaseService).create(1L, request, "user");
		}
	}

	@Nested
	@DisplayName("질병 수정")
	class DiseaseModify {

		DizWriteRequest request = DizWriteRequest.builder().name("질병")
				.category(DiseaseCategory.DERMATOLOGY)
				.startedAt(LocalDate.of(2023, 1, 1)).endedAt(LocalDate.of(2023, 1, 31)).build();

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			given(diseaseService.modify(1L, 1L, request, "user")).willReturn(
					new DizWriteResponse(1L, "펫이름", "질병"));

			mockMvc.perform(
							put("/api/v1/pets/{petId}/diseases/{diseaseId}", 1L, 1L)
									.content(objectMapper.registerModule(javaTimeModule)
											.writeValueAsBytes(request))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.id").value(1L))
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("petId").description("반려동물 번호"),
									parameterWithName("diseaseId").description("질병 등록 번호")),
							requestFields(fieldWithPath("name").description("질병 이름"),
									fieldWithPath("category").optional().description("진료 과목"),
									fieldWithPath("startedAt").optional().description("진단 날짜")
											.attributes(field("constraints", "오늘 이전만 가능")),
									fieldWithPath("endedAt").optional().description("종료 날짜")

							),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.id").description("질병 등록 번호"),
									fieldWithPath("result.petName").description("반려동물 이름"),
									fieldWithPath("result.name").description("질병 이름"))));
			verify(diseaseService).modify(1L, 1L, request, "user");
		}
	}

	@Nested
	@DisplayName("질병 삭제")
	class DiseaseDelete {

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			given(diseaseService.delete(1L, 1L, "user")).willReturn(
					new MessageResponse("질병 기록이 삭제되었습니다."));

			mockMvc.perform(
							delete("/api/v1/pets/{petId}/diseases/{diseaseId}", 1L, 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.msg").value("질병 기록이 삭제되었습니다."))
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("petId").description("반려동물 번호"),
									parameterWithName("diseaseId").description("질병 등록 번호")),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.msg").description("결과 메세지"))));
			verify(diseaseService).delete(1L, 1L, "user");
		}
	}

	@Nested
	@DisplayName("질병 단건 조회")
	class DiseaseGet {

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			DizGetResponse response = DizGetResponse.builder().id(1L).name("질병")
					.category(DiseaseCategory.DERMATOLOGY)
					.startedAt(LocalDate.of(2000, 1, 1)).endedAt(LocalDate.of(2000, 1, 31)).build();
			given(diseaseService.getDisease(1L, 1L, "user")).willReturn(response);

			mockMvc.perform(
							get("/api/v1/pets/{petId}/diseases/{diseaseId}", 1L, 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.id").value(1L))
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("petId").description("반려동물 번호"),
									parameterWithName("diseaseId").description("질병 등록 번호")),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.id").description("질병기록 등록 번호"),
									fieldWithPath("result.category").description("진료 과목"),
									fieldWithPath("result.name").description("질병 이름"),
									fieldWithPath("result.startedAt").description("진단 날짜"),
									fieldWithPath("result.endedAt").description("종료 날짜"))));
			verify(diseaseService).getDisease(1L, 1L, "user");
		}
	}

}