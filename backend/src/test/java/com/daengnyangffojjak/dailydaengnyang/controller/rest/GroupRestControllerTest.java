package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupInviteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupPetListResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupPetResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupUserListResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupUserResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.GroupException;
import com.daengnyangffojjak.dailydaengnyang.exception.UserException;
import com.daengnyangffojjak.dailydaengnyang.service.GroupService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

@WebMvcTest(GroupRestController.class)
class GroupRestControllerTest extends ControllerTest {

	@MockBean
	protected GroupService groupService;

	@Nested
	@DisplayName("그룹 생성")
	class GroupCreate {

		GroupMakeRequest groupMakeRequest = new GroupMakeRequest("그룹이름", "엄마");
		GroupMakeResponse groupMakeResponse = GroupMakeResponse.builder().id(1L).name("그룹이름")
				.ownerId(1L).ownerUserName("user").build();


		@Test
		@DisplayName("그룹 생성 성공")
		void success() throws Exception {
			given(groupService.create(groupMakeRequest, "user")).willReturn(groupMakeResponse);

			mockMvc.perform(
							post("/api/v1/groups").content(objectMapper.writeValueAsBytes(groupMakeRequest))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.ownerId").value(1L)).andDo(restDocs.document(
							requestFields(fieldWithPath("name").description("그룹 이름"),
									fieldWithPath("roleInGroup").description("그룹 내 역할")),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.id").description("그룹 번호"),
									fieldWithPath("result.name").description("그룹 이름"),
									fieldWithPath("result.ownerId").description("그룹주인 번호"),
									fieldWithPath("result.ownerUserName").description("그룹주인 아이디"))));
			verify(groupService).create(groupMakeRequest, "user");
		}
	}

	@Nested
	@DisplayName("그룹 사용자 리스트")
	class GroupUserList {

		@Test
		@DisplayName("사용자 조회 성공")
		void success() throws Exception {
			GroupUserListResponse groupUserResponse = new GroupUserListResponse(
					List.of(new GroupUserResponse(1L, "user", "mom"),
							new GroupUserResponse(2L, "user2", "dad")), 2);
			given(groupService.getGroupUsers(1L, "user")).willReturn(groupUserResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.get("/api/v1/groups/{groupId}/users", 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.users").exists()).andDo(restDocs.document(
							pathParameters(parameterWithName("groupId").description("그룹 번호")),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.users").description("그룹 내 유저 리스트"),
									fieldWithPath("result.users[].id").description("유저 번호"),
									fieldWithPath("result.users[].userName").description("유저 아이디"),
									fieldWithPath("result.users[].roleInGroup").description("그룹 내 역할"),
									fieldWithPath("result.count").description("그룹 내 유저 수"))));
			verify(groupService).getGroupUsers(1L, "user");
		}
	}

	@Nested
	@DisplayName("그룹 반려동물 리스트")
	class GroupPetList {

		@Test
		@DisplayName("반려동물 조회 성공")
		void success() throws Exception {
			GroupPetListResponse petListResponse = new GroupPetListResponse(
					List.of(new GroupPetResponse(1L, "hoon", Species.CAT, "4살"),
							new GroupPetResponse(2L, "hoon2", Species.CAT, "4개월")), 2);
			given(groupService.getGroupPets(1L, "user")).willReturn(petListResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.get("/api/v1/groups/{groupId}/pets", 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.pets").exists()).andDo(restDocs.document(
							pathParameters(parameterWithName("groupId").description("그룹 번호")),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.pets").description("그룹 내 반려동물 리스트"),
									fieldWithPath("result.pets[].id").description("반려동물 번호"),
									fieldWithPath("result.pets[].name").description("반려동물 이름"),
									fieldWithPath("result.pets[].species").description("종"),
									fieldWithPath("result.pets[].age").description("나이"),
									fieldWithPath("result.count").description("그룹 내 반려동물 수"))));
			verify(groupService).getGroupPets(1L, "user");
		}
	}

	@Nested
	@DisplayName("그룹에 유저초대")
	class GroupInvite {

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			GroupInviteRequest request = new GroupInviteRequest("gmail@gmail.com", "dad");
			MessageResponse response = new MessageResponse("user2이(가) 그룹에 등록되었습니다.");
			given(groupService.inviteMember(1L, "user", request)).willReturn(response);

			mockMvc.perform(
							RestDocumentationRequestBuilders.post("/api/v1/groups/{groupId}/invite", 1L)
									.content(objectMapper.writeValueAsBytes(request))
									.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.msg").exists()).andDo(restDocs.document(
							pathParameters(parameterWithName("groupId").description("그룹 번호")),
							requestFields(fieldWithPath("email").description("초대하는 유저 email"),
									fieldWithPath("roleInGroup").description("그룹 내 역할")),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.msg").description("결과 메세지"))));
			verify(groupService).inviteMember(1L, "user", request);
		}

		@Test
		@DisplayName("초대하려는 유저가 이미 그룹 멤버인 경우")
		void fail_이미그룹의유저() throws Exception {
			GroupInviteRequest request = new GroupInviteRequest("g@g.g", "dad");
			given(groupService.inviteMember(1L, "user", request)).willThrow(
					new UserException(ErrorCode.INVALID_REQUEST, "이미 존재하는 회원입니다."));

			mockMvc.perform(
							RestDocumentationRequestBuilders.post("/api/v1/groups/{groupId}/invite", 1L)
									.content(objectMapper.writeValueAsBytes(request))
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isConflict())
					.andExpect(jsonPath("$.resultCode").value("ERROR"))
					.andExpect(jsonPath("$.result.errorCode").exists())
					.andExpect(jsonPath("$.result.message").exists()).andDo(restDocs.document(
							pathParameters(parameterWithName("groupId").description("그룹 번호")),
							requestFields(fieldWithPath("email").description("초대하는 유저 email"),
									fieldWithPath("roleInGroup").description("그룹 내 역할")),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.errorCode").description("에러코드"),
									fieldWithPath("result.message").description("에러메세지"))));
			verify(groupService).inviteMember(1L, "user", request);
		}
	}

	@Nested
	@DisplayName("그룹에서 나오기")
	class LeaveGroup {

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			MessageResponse response = new MessageResponse("그룹에서 나왔습니다.");
			given(groupService.leaveGroup(1L, "user")).willReturn(response);

			mockMvc.perform(
							RestDocumentationRequestBuilders.delete("/api/v1/groups/{groupId}/users", 1L)
									.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.msg").exists())
					.andDo(restDocs.document(
							pathParameters(parameterWithName("groupId").description("그룹 번호")),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.msg").description("결과 메세지"))));
			verify(groupService).leaveGroup(1L, "user");
		}

		@Test
		@DisplayName("그룹장이 그룹원이 있는 상태에서 나오는 경우")
		void fail_그룹장이_나오려고함() throws Exception {
			given(groupService.leaveGroup(1L, "user")).willThrow(
					new GroupException(ErrorCode.INVALID_REQUEST, "그룹장은 그룹을 나갈 수 없습니다."));

			mockMvc.perform(
							RestDocumentationRequestBuilders.delete("/api/v1/groups/{groupId}/users", 1L)
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isConflict())
					.andExpect(jsonPath("$.resultCode").value("ERROR"))
					.andExpect(jsonPath("$.result.errorCode").value("INVALID_REQUEST"))
					.andDo(restDocs.document(
							pathParameters(parameterWithName("groupId").description("그룹 번호")),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.errorCode").description("에러코드"),
									fieldWithPath("result.message").description("에러메세지"))));
			verify(groupService).leaveGroup(1L, "user");
		}
	}

	@Nested
	@DisplayName("그룹에서 내보내기")
	class DeleteGroupMember {

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			MessageResponse response = new MessageResponse("그룹에서 내보내기를 성공하였습니다.");
			given(groupService.deleteMember(1L, "user", 2L)).willReturn(response);

			mockMvc.perform(
							RestDocumentationRequestBuilders.delete(
									"/api/v1/groups/{groupId}/users/{userId}", 1L, 2L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
					.andExpect(jsonPath("$.result.msg").exists())
					.andDo(restDocs.document(
							pathParameters(parameterWithName("groupId").description("그룹 번호"),
									parameterWithName("userId").description("유저 번호")),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.msg").description("결과 메세지"))));
			verify(groupService).deleteMember(1L, "user", 2L);
		}

		@Test
		@DisplayName("그룹장이 아닌경우")
		void fail_그룹장이_아닌경우() throws Exception {
			given(groupService.deleteMember(1L, "user", 2L)).willThrow(
					new UserException(ErrorCode.INVALID_PERMISSION));

			mockMvc.perform(
							RestDocumentationRequestBuilders.delete(
									"/api/v1/groups/{groupId}/users/{userId}", 1L, 2L))

					.andExpect(status().isUnauthorized())
					.andExpect(jsonPath("$.resultCode").value("ERROR"))
					.andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"))
					.andDo(restDocs.document(
							pathParameters(parameterWithName("groupId").description("그룹 번호"),
									parameterWithName("userId").description("유저 번호")),
							responseFields(fieldWithPath("resultCode").description("결과코드"),
									fieldWithPath("result.errorCode").description("에러코드"),
									fieldWithPath("result.message").description("에러메세지"))));
			verify(groupService).deleteMember(1L, "user", 2L);
		}
	}
}
