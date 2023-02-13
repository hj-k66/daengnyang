package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupInviteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupPetListResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupUserListResponse;
import com.daengnyangffojjak.dailydaengnyang.repository.GroupRepository;
import com.daengnyangffojjak.dailydaengnyang.service.GroupService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class GroupRestController {

	private final GroupService groupService;
	private final GroupRepository groupRepository;

	@PostMapping
	public ResponseEntity<Response<GroupMakeResponse>> create(
			@AuthenticationPrincipal UserDetails user,
			@RequestBody GroupMakeRequest groupMakeRequest) {
		GroupMakeResponse groupMakeResponse = groupService.create(groupMakeRequest,
				user.getUsername());
		return ResponseEntity.created(URI.create("/api/v1/groups/" + groupMakeResponse.getId()))
				.body(Response.success(groupMakeResponse));
	}

	@GetMapping(value = "/{groupId}/users")
	public Response<GroupUserListResponse> getGroupUsers(@AuthenticationPrincipal UserDetails user,
			@PathVariable Long groupId) {
		GroupUserListResponse groupUserResponse = groupService.getGroupUsers(groupId,
				user.getUsername());
		return Response.success(groupUserResponse);
	}

	@GetMapping(value = "/{groupId}/pets")
	public Response<GroupPetListResponse> getGroupPets(@AuthenticationPrincipal UserDetails user,
			@PathVariable Long groupId) {
		GroupPetListResponse groupPetResponse = groupService.getGroupPets(groupId,
				user.getUsername());
		return Response.success(groupPetResponse);
	}

	@PostMapping(value = "/{groupId}/invite")
	public Response<MessageResponse> inviteUser(@AuthenticationPrincipal UserDetails user,
			@PathVariable Long groupId, @RequestBody GroupInviteRequest groupInviteRequest) {
		MessageResponse messageResponse = groupService.inviteMember(groupId,
				user.getUsername(), groupInviteRequest);
		return Response.success(messageResponse);
	}

	@DeleteMapping(value = "/{groupId}/users")
	public Response<MessageResponse> leaveGroup(@AuthenticationPrincipal UserDetails user,
			@PathVariable Long groupId) {
		MessageResponse messageResponse = groupService.leaveGroup(groupId, user.getUsername());
		return Response.success(messageResponse);
	}

	@DeleteMapping(value = "/{groupId}/users/{userId}")
	public Response<MessageResponse> deleteMember(@AuthenticationPrincipal UserDetails user,
			@PathVariable Long groupId, @PathVariable Long userId) {
		MessageResponse messageResponse = groupService.deleteMember(groupId, user.getUsername(),
				userId);
		return Response.success(messageResponse);
	}
}

