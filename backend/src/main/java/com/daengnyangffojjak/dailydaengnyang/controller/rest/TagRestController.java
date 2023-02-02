package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntWriteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntWriteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.tag.TagWorkRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.tag.TagWorkResponse;
import com.daengnyangffojjak.dailydaengnyang.service.TagService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TagRestController {
	private final TagService tagService;

	@PostMapping(value = "/groups/{groupId}/tags")
	public ResponseEntity<Response<TagWorkResponse>> create(
			@AuthenticationPrincipal UserDetails user, @PathVariable Long groupId,
			@Valid @RequestBody TagWorkRequest tagWorkRequest) {
		TagWorkResponse tagWorkResponse = tagService.create(groupId, tagWorkRequest,
				user.getUsername());
		return ResponseEntity.created(
						URI.create("/api/v1/groups/" + groupId + "/tags/" + tagWorkResponse.getId()))
				.body(Response.success(tagWorkResponse));
	}
	@PutMapping(value = "/groups/{groupId}/tags/{tagId}")
	public ResponseEntity<Response<TagWorkResponse>> modify(
			@AuthenticationPrincipal UserDetails user, @PathVariable Long groupId, @PathVariable Long tagId,
			@Valid @RequestBody TagWorkRequest tagWorkRequest) {
		TagWorkResponse tagWorkResponse = tagService.modify(groupId, tagId, tagWorkRequest,
				user.getUsername());
		return ResponseEntity.created(
						URI.create("/api/v1/groups/" + groupId + "/tags/" + tagWorkResponse.getId()))
				.body(Response.success(tagWorkResponse));
	}
}
