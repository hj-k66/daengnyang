package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntMakeRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntMakeResponse;
import com.daengnyangffojjak.dailydaengnyang.service.MonitoringService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MonitoringRestController {

	private final MonitoringService monitoringService;

	@PostMapping(value = "/pets/{petId}/monitorings")
	public ResponseEntity<Response<MntMakeResponse>> create(
			@AuthenticationPrincipal UserDetails user, @PathVariable Long petId,
			@Valid @RequestBody MntMakeRequest mntMakeRequest) {
		MntMakeResponse mntMakeResponse = monitoringService.create(petId, mntMakeRequest,
				user.getUsername());
		return ResponseEntity.created(
						URI.create("/api/v1/pets/" + petId + "/monitorings" + mntMakeResponse.getId()))
				.body(Response.success(mntMakeResponse));
	}
}
