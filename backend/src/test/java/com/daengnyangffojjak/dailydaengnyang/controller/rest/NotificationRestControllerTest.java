package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.notification.NotificationListResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.notification.NotificationResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.NotificationType;
import com.daengnyangffojjak.dailydaengnyang.service.NotificationService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(NotificationRestController.class)
public class NotificationRestControllerTest extends ControllerTest {

	@MockBean
	NotificationService notificationService;

	@Nested
	@DisplayName("알람 리스트 조회")
	class NotificationList {

		@Test
		@DisplayName("알람 리스트 조회 성공")
		void list_success() throws Exception {
			NotificationResponse notificationResponse1 = new NotificationResponse(2L, "일정부탁히기",
					"희정님이 오전산책 일정을 부탁했습니다.  ",
					NotificationType.SCHEDULE_ASSIGN, false);
			NotificationResponse notificationResponse2 = new NotificationResponse(1L, "일정등록",
					"예지님이 목욕 예약 일정을 등록했습니다.",
					NotificationType.SCHEDULE_CREATE, false);

			given(notificationService.getAllNotification(4L, 2, "user")).willReturn(
					NotificationListResponse.builder()
							.notifications(List.of(notificationResponse1, notificationResponse2))
							.build());

			mockMvc.perform(
						get(
									"/api/v1/notification?lastNotificationId=4&size=2"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andDo(restDocs.document(
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.notifications").description("알람 리스트"),
									fieldWithPath("result.notifications[].id").description("알람 id"),
									fieldWithPath("result.notifications[].title").description(
											"알람 제목"),
									fieldWithPath("result.notifications[].body").description(
											"알람 메세지 바디"),
									fieldWithPath(
											"result.notifications[].notificationType").description(
											"알람 종류"),
									fieldWithPath("result.notifications[].checked").description(
											"읽음여부"))));
			verify(notificationService).getAllNotification(4L, 2, "user");

		}
	}
}
