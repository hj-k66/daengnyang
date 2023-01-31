package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import static com.daengnyangffojjak.dailydaengnyang.utils.RestDocsConfiguration.field;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntWriteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntWriteResponse;
import com.daengnyangffojjak.dailydaengnyang.service.MonitoringService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

@WebMvcTest(MonitoringRestController.class)
class MonitoringRestControllerTest extends ControllerTest {

	@MockBean
	protected MonitoringService monitoringService;

	@Nested
	@DisplayName("모니터링 등록")
	class MonitoringCreate {

		MntWriteRequest request = MntWriteRequest.builder()
				.date(LocalDate.of(2023, 1, 30)).weight(7.7).vomit(false)
				.amPill(true).pmPill(true).urination(3).defecation(2).notes("양치").build();

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			given(monitoringService.create(1L, request, "user"))
					.willReturn(new MntWriteResponse(1L, "hoon", LocalDate.of(2023, 1, 30)));

			mockMvc.perform(
							post("/api/v1/pets/{petId}/monitorings", 1L)
									.content(objectMapper.registerModule(new JavaTimeModule())
											.writeValueAsBytes(request))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.id").value(1L))
					.andDo(restDocs.document(
							pathParameters(parameterWithName("petId").description("반려동물 번호")),
							requestFields(fieldWithPath("date").description("모니터링 날짜")
											.attributes(field("constraints", "오늘 이전만 가능")),
									fieldWithPath("weight").optional().description("몸무게"),
									fieldWithPath("vomit").optional().description("구토"),
									fieldWithPath("amPill").optional().description("오전 투약"),
									fieldWithPath("pmPill").optional().description("오후 투약"),
									fieldWithPath("customSymptom").optional()
											.description("커스텀 모니터링"),
									fieldWithPath("customSymptomName").optional()
											.description("커스텀 모니터링 지표"),
									fieldWithPath("feedToGram").optional().description("식이량(g)"),
									fieldWithPath("walkCnt").optional().description("산책 횟수"),
									fieldWithPath("playCnt").optional().description("놀이 횟수"),
									fieldWithPath("urination").optional().description("배뇨 횟수"),
									fieldWithPath("defecation").optional().description("배변 횟수"),
									fieldWithPath("respiratoryRate").optional().description("호흡수"),
									fieldWithPath("customInt").optional().description("커스텀 모니터링"),
									fieldWithPath("customIntName").optional()
											.description("커스텀 모니터링 지표"),
									fieldWithPath("notes").optional().description("특이사항")
							),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.id").description("모니터링 등록 번호"),
									fieldWithPath("result.petName").description("반려동물 이름"),
									fieldWithPath("result.date").description("모니터링 날짜"))));
			verify(monitoringService).create(1L, request, "user");
		}
	}

}