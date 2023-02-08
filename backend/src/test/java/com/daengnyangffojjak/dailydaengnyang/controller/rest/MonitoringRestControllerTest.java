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

import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntGetResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntMonthlyResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntReportResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntWriteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntWriteResponse;
import com.daengnyangffojjak.dailydaengnyang.service.MonitoringService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.List;
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
	private JavaTimeModule javaTimeModule = new JavaTimeModule();

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
									.content(objectMapper.registerModule(javaTimeModule)
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

	@Nested
	@DisplayName("모니터링 수정")
	class MonitoringModify {

		MntWriteRequest request = MntWriteRequest.builder()
				.date(LocalDate.of(2023, 1, 30)).weight(7.7).vomit(false)
				.amPill(true).pmPill(true).urination(3).defecation(2).notes("양치").build();

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			given(monitoringService.modify(1L, 1L, request, "user"))
					.willReturn(new MntWriteResponse(1L, "hoon", LocalDate.of(2023, 1, 30)));

			mockMvc.perform(
							put("/api/v1/pets/{petId}/monitorings/{monitoringId}", 1L, 1L)
									.content(objectMapper.registerModule(javaTimeModule)
											.writeValueAsBytes(request))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.id").value(1L))
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("petId").description("반려동물 번호"),
									parameterWithName("monitoringId").description("모니터링 번호")
							),
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
			verify(monitoringService).modify(1L, 1L, request, "user");
		}
	}

	@Nested
	@DisplayName("모니터링 삭제")
	class MonitoringDelete {

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			given(monitoringService.delete(1L, 1L, "user"))
					.willReturn(new MntDeleteResponse("모니터링 삭제 완료", 1L));

			mockMvc.perform(
							delete("/api/v1/pets/{petId}/monitorings/{monitoringId}", 1L, 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.id").value(1L))
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("petId").description("반려동물 번호"),
									parameterWithName("monitoringId").description("모니터링 번호")
							),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.id").description("모니터링 등록 번호"),
									fieldWithPath("result.message").description("결과 메세지"))));
			verify(monitoringService).delete(1L, 1L, "user");
		}
	}

	@Nested
	@DisplayName("모니터링 단건조회")
	class MonitoringShow {

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			MntGetResponse response = MntGetResponse.builder()
					.date(LocalDate.of(2023, 1, 30)).weight(7.7).vomit(false)
					.amPill(true).pmPill(true).customSymptom(false).customSymptomName(null)
					.feedToGram(40).urination(3).defecation(2).respiratoryRate(24)
					.customInt(2).customIntName("이뻐해주기").notes("양치").build();
			given(monitoringService.getMonitoring(1L, 1L, "user"))
					.willReturn(response);

			mockMvc.perform(
							get("/api/v1/pets/{petId}/monitorings/{monitoringId}", 1L, 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("petId").description("반려동물 번호"),
									parameterWithName("monitoringId").description("모니터링 번호")
							),
							responseFields(
									fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.date").description("모니터링 날짜"),
									fieldWithPath("result.weight").description("몸무게"),
									fieldWithPath("result.vomit").description("구토"),
									fieldWithPath("result.amPill").description("오전 투약"),
									fieldWithPath("result.pmPill").description("오후 투약"),
									fieldWithPath("result.customSymptom")
											.description("커스텀 모니터링"),
									fieldWithPath("result.customSymptomName")
											.description("커스텀 모니터링 지표"),
									fieldWithPath("result.feedToGram").description("식이량(g)"),
									fieldWithPath("result.walkCnt").description("산책 횟수"),
									fieldWithPath("result.playCnt").description("놀이 횟수"),
									fieldWithPath("result.urination").description("배뇨 횟수"),
									fieldWithPath("result.defecation").description("배변 횟수"),
									fieldWithPath("result.respiratoryRate").description("호흡수"),
									fieldWithPath("result.customInt").description("커스텀 모니터링"),
									fieldWithPath("result.customIntName")
											.description("커스텀 모니터링 지표"),
									fieldWithPath("result.notes").description("특이사항")
							)));
			verify(monitoringService).getMonitoring(1L, 1L, "user");
		}
	}

	@Nested
	@DisplayName("모니터링 기간 단위 조회")
	class MonitoringListShow {

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			MntGetResponse response1 = MntGetResponse.builder()
					.date(LocalDate.of(2023, 1, 30)).weight(7.7).vomit(false)
					.amPill(true).pmPill(true).customSymptom(false).customSymptomName(null)
					.feedToGram(40).urination(3).defecation(2).respiratoryRate(24)
					.customInt(2).customIntName("이뻐해주기").notes("양치").build();
			MntGetResponse response2 = MntGetResponse.builder()
					.date(LocalDate.of(2023, 1, 25)).weight(7.6).vomit(false)
					.amPill(true).pmPill(true).customSymptom(false).customSymptomName(null)
					.feedToGram(40).urination(3).defecation(2).respiratoryRate(24)
					.customInt(2).customIntName("이뻐해주기").notes("양치").build();
			given(monitoringService.getMonitoringList(1L, "20230101", "20230131", "user"))
					.willReturn(MntMonthlyResponse.builder()
							.monthlyMonitorings(List.of(response1, response2)).build());

			mockMvc.perform(
							get("/api/v1/pets/{petId}/monitorings?fromDate=20230101&toDate=20230131", 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("petId").description("반려동물 번호")
							),
							responseFields(
									fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.monthlyMonitorings").description(
											"모니터링 리스트"),

									fieldWithPath("result.monthlyMonitorings[].date").description(
											"모니터링 날짜"),
									fieldWithPath("result.monthlyMonitorings[].weight").description(
											"몸무게"),
									fieldWithPath("result.monthlyMonitorings[].vomit").description(
											"구토"),
									fieldWithPath("result.monthlyMonitorings[].amPill").description(
											"오전 투약"),
									fieldWithPath("result.monthlyMonitorings[].pmPill").description(
											"오후 투약"),
									fieldWithPath("result.monthlyMonitorings[].customSymptom")
											.description("커스텀 모니터링"),
									fieldWithPath("result.monthlyMonitorings[].customSymptomName")
											.description("커스텀 모니터링 지표"),
									fieldWithPath(
											"result.monthlyMonitorings[].feedToGram").description(
											"식이량(g)"),
									fieldWithPath(
											"result.monthlyMonitorings[].walkCnt").description(
											"산책 횟수"),
									fieldWithPath(
											"result.monthlyMonitorings[].playCnt").description(
											"놀이 횟수"),
									fieldWithPath(
											"result.monthlyMonitorings[].urination").description(
											"배뇨 횟수"),
									fieldWithPath(
											"result.monthlyMonitorings[].defecation").description(
											"배변 횟수"),
									fieldWithPath(
											"result.monthlyMonitorings[].respiratoryRate").description(
											"호흡수"),
									fieldWithPath(
											"result.monthlyMonitorings[].customInt").description(
											"커스텀 모니터링"),
									fieldWithPath("result.monthlyMonitorings[].customIntName")
											.description("커스텀 모니터링 지표"),
									fieldWithPath("result.monthlyMonitorings[].notes").description(
											"특이사항")
							)));
			verify(monitoringService).getMonitoringList(1L, "20230101", "20230131", "user");
		}
	}

	@Nested
	@DisplayName("모니터링 레포트 조회")
	class MonitoringReportShow {

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			MntReportResponse response = MntReportResponse.builder().weightAvg(7.7).weightCount(10)
					.vomitTrue(2).vomitCount(15).amPillTrue(30).amPillCount(30).pmPillTrue(29)
					.pmPillCount(30).customSymptomTrue(20).customSymptomCount(20)
					.customSymptomName("양치").feedToGramAvg(44).feedToGramCount(20).walkCntAvg(0)
					.walkCntCount(0).playCntAvg(1).playCntCount(20).urinationAvg(3.3)
					.urinationCount(20).defecationAvg(1.8).defecationCount(20)
					.respiratoryRateAvg(20.5).respiratoryRateCount(20).customIntAvg(2)
					.customIntCount(20).customIntName("이뻐해주기").build();

			given(monitoringService.getReport(1L, "20230101", "20230131", "user"))
					.willReturn(response);

			mockMvc.perform(
							get("/api/v1/pets/{petId}/monitorings/report?fromDate=20230101&toDate=20230131",
									1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andDo(restDocs.document(
							pathParameters(
									parameterWithName("petId").description("반려동물 번호")
							),
							responseFields(
									fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.weightAvg").description("몸무게 평균"),
									fieldWithPath("result.weightCount").description("몸무게 기록 갯수"),
									fieldWithPath("result.vomitTrue").description("구토한 날수"),
									fieldWithPath("result.vomitCount").description("구토 기록 갯수"),
									fieldWithPath("result.amPillTrue").description("아침 약 먹은 횟수"),
									fieldWithPath("result.amPillCount").description("아침약 기록 갯수"),
									fieldWithPath("result.pmPillTrue").description("저녁 약 먹은 횟수"),
									fieldWithPath("result.pmPillCount").description("저녁약 기록 갯수"),
									fieldWithPath("result.customSymptomTrue").description(
											"개별 관리 기록 횟수"),
									fieldWithPath("result.customSymptomCount").description(
											"개별 관리 기록 갯수"),
									fieldWithPath("result.customSymptomName").description(
											"개별 관리 항목 이름"),
									fieldWithPath("result.feedToGramAvg").description("식이량 평균"),
									fieldWithPath("result.feedToGramCount").description(
											"식이량 기록 갯수"),
									fieldWithPath("result.walkCntAvg").description("산책 횟수 평균"),
									fieldWithPath("result.walkCntCount").description("산책 기록 갯수"),
									fieldWithPath("result.playCntAvg").description("놀이 횟수 평균"),
									fieldWithPath("result.playCntCount").description("놀이 기록 갯수"),
									fieldWithPath("result.urinationAvg").description("배뇨 횟수 평균"),
									fieldWithPath("result.urinationCount").description("배뇨 기록 갯수"),
									fieldWithPath("result.defecationAvg").description("배변 횟수 평균"),
									fieldWithPath("result.defecationCount").description("배변 기록 갯수"),
									fieldWithPath("result.respiratoryRateAvg").description(
											"호흡수 평균"),
									fieldWithPath("result.respiratoryRateCount").description(
											"호흡수 기록 갯수"),
									fieldWithPath("result.customIntAvg").description("개별 관리 항목 평균"),
									fieldWithPath("result.customIntCount").description(
											"개별 관리 기록 갯수"),
									fieldWithPath("result.customIntName").description("개별 관리 항목 이름")
							)));
			verify(monitoringService).getReport(1L, "20230101", "20230131", "user");
		}
	}
}