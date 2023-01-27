package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupPetListResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupUserListResponse;
import com.daengnyangffojjak.dailydaengnyang.service.GroupService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

}

