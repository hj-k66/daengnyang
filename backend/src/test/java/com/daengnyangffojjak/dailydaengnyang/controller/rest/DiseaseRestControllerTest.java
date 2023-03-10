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
import java.util.List;
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
	@DisplayName("?????? ??????")
	class DiseaseCreate {

		DizWriteRequest request = DizWriteRequest.builder().name("??????")
				.category(DiseaseCategory.DERMATOLOGY)
				.startedAt(LocalDate.of(2023, 1, 1)).endedAt(LocalDate.of(2023, 1, 31)).build();

		@Test
		@DisplayName("??????")
		void success() throws Exception {
			given(diseaseService.create(1L, request, "user")).willReturn(
					new DizWriteResponse(1L, "?????????", "??????"));

			mockMvc.perform(
							post("/api/v1/pets/{petId}/diseases", 1L)
									.content(objectMapper.registerModule(javaTimeModule)
											.writeValueAsBytes(request))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.id").value(1L))
					.andDo(restDocs.document(
							pathParameters(parameterWithName("petId").description("???????????? ??????")),
							requestFields(fieldWithPath("name").description("?????? ??????"),
									fieldWithPath("category").optional().description("?????? ??????"),
									fieldWithPath("startedAt").optional().description("?????? ??????")
											.attributes(field("constraints", "?????? ????????? ??????")),
									fieldWithPath("endedAt").optional().description("?????? ??????")

							),
							responseFields(fieldWithPath("resultCode").description("????????????"),
									fieldWithPath("result.id").description("?????? ?????? ??????"),
									fieldWithPath("result.petName").description("???????????? ??????"),
									fieldWithPath("result.name").description("?????? ??????"))));
			verify(diseaseService).create(1L, request, "user");
		}
	}

	@Nested
	@DisplayName("?????? ??????")
	class DiseaseModify {

		DizWriteRequest request = DizWriteRequest.builder().name("??????")
				.category(DiseaseCategory.DERMATOLOGY)
				.startedAt(LocalDate.of(2023, 1, 1)).endedAt(LocalDate.of(2023, 1, 31)).build();

		@Test
		@DisplayName("??????")
		void success() throws Exception {
			given(diseaseService.modify(1L, 1L, request, "user")).willReturn(
					new DizWriteResponse(1L, "?????????", "??????"));

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
									parameterWithName("petId").description("???????????? ??????"),
									parameterWithName("diseaseId").description("?????? ?????? ??????")),
							requestFields(fieldWithPath("name").description("?????? ??????"),
									fieldWithPath("category").optional().description("?????? ??????"),
									fieldWithPath("startedAt").optional().description("?????? ??????")
											.attributes(field("constraints", "?????? ????????? ??????")),
									fieldWithPath("endedAt").optional().description("?????? ??????")

							),
							responseFields(fieldWithPath("resultCode").description("????????????"),
									fieldWithPath("result.id").description("?????? ?????? ??????"),
									fieldWithPath("result.petName").description("???????????? ??????"),
									fieldWithPath("result.name").description("?????? ??????"))));
			verify(diseaseService).modify(1L, 1L, request, "user");
		}
	}

	@Nested
	@DisplayName("?????? ??????")
	class DiseaseDelete {

		@Test
		@DisplayName("??????")
		void success() throws Exception {
			given(diseaseService.delete(1L, 1L, "user")).willReturn(
					new MessageResponse("?????? ????????? ?????????????????????."));

			mockMvc.perform(
							delete("/api/v1/pets/{petId}/diseases/{diseaseId}", 1L, 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.msg").value("?????? ????????? ?????????????????????."))
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("petId").description("???????????? ??????"),
									parameterWithName("diseaseId").description("?????? ?????? ??????")),
							responseFields(fieldWithPath("resultCode").description("????????????"),
									fieldWithPath("result.msg").description("?????? ?????????"))));
			verify(diseaseService).delete(1L, 1L, "user");
		}
	}

	DizGetResponse dizGetResponse = DizGetResponse.builder().id(1L).name("??????")
			.category(DiseaseCategory.DERMATOLOGY)
			.startedAt(LocalDate.of(2000, 1, 1)).endedAt(LocalDate.of(2000, 1, 31)).build();

	@Nested
	@DisplayName("?????? ?????? ??????")
	class DiseaseGet {


		@Test
		@DisplayName("??????")
		void success() throws Exception {
			given(diseaseService.getDisease(1L, 1L, "user")).willReturn(dizGetResponse);

			mockMvc.perform(
							get("/api/v1/pets/{petId}/diseases/{diseaseId}", 1L, 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.id").value(1L))
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("petId").description("???????????? ??????"),
									parameterWithName("diseaseId").description("?????? ?????? ??????")),
							responseFields(fieldWithPath("resultCode").description("????????????"),
									fieldWithPath("result.id").description("???????????? ?????? ??????"),
									fieldWithPath("result.category").description("?????? ??????"),
									fieldWithPath("result.name").description("?????? ??????"),
									fieldWithPath("result.startedAt").description("?????? ??????"),
									fieldWithPath("result.endedAt").description("?????? ??????"))));
			verify(diseaseService).getDisease(1L, 1L, "user");
		}
	}

	@Nested
	@DisplayName("?????? ????????? ??????")
	class DiseaseGetList {

		@Test
		@DisplayName("??????")
		void success() throws Exception {
			given(diseaseService.getDiseaseList(1L, "user")).willReturn(List.of(dizGetResponse));

			mockMvc.perform(
							get("/api/v1/pets/{petId}/diseases", 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result").exists())
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("petId").description("???????????? ??????")),
							responseFields(fieldWithPath("resultCode").description("????????????"),
									fieldWithPath("result[].id").description("???????????? ?????? ??????"),
									fieldWithPath("result[].category").description("?????? ??????"),
									fieldWithPath("result[].name").description("?????? ??????"),
									fieldWithPath("result[].startedAt").description("?????? ??????"),
									fieldWithPath("result[].endedAt").description("?????? ??????"))));
			verify(diseaseService).getDiseaseList(1L, "user");
		}
	}
}