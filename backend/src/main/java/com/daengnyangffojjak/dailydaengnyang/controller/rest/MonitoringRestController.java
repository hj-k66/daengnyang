package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntGetResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntWriteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntWriteResponse;
import com.daengnyangffojjak.dailydaengnyang.service.MonitoringService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MonitoringRestController {

	private final MonitoringService monitoringService;

	@PostMapping(value = "/pets/{petId}/monitorings")
	public ResponseEntity<Response<MntWriteResponse>> create(
			@AuthenticationPrincipal UserDetails user, @PathVariable Long petId,
			@Valid @RequestBody MntWriteRequest mntWriteRequest) {
		MntWriteResponse mntWriteResponse = monitoringService.create(petId, mntWriteRequest,
				user.getUsername());
		return ResponseEntity.created(
						URI.create("/api/v1/pets/" + petId + "/monitorings/" + mntWriteResponse.getId()))
				.body(Response.success(mntWriteResponse));
	}

	@PutMapping(value = "/pets/{petId}/monitorings/{monitoringId}")
	public ResponseEntity<Response<MntWriteResponse>> modify(
			@AuthenticationPrincipal UserDetails user, @PathVariable Long petId,
			@PathVariable Long monitoringId,
			@Valid @RequestBody MntWriteRequest mntWriteRequest) {
		MntWriteResponse mntWriteResponse = monitoringService.modify(petId, monitoringId,
				mntWriteRequest,
				user.getUsername());
		return ResponseEntity.created(
						URI.create("/api/v1/pets/" + petId + "/monitorings/" + monitoringId))
				.body(Response.success(mntWriteResponse));
	}

	@DeleteMapping(value = "/pets/{petId}/monitorings/{monitoringId}")
	public Response<MntDeleteResponse> delete(
			@AuthenticationPrincipal UserDetails user, @PathVariable Long petId,
			@PathVariable Long monitoringId) {
		MntDeleteResponse mntDeleteResponse = monitoringService.delete(petId, monitoringId,
				user.getUsername());
		return Response.success(mntDeleteResponse);
	}

	@GetMapping(value = "/pets/{petId}/monitorings/{monitoringId}")
	public Response<MntGetResponse> get(@AuthenticationPrincipal UserDetails user,
			@PathVariable Long petId, @PathVariable Long monitoringId) {
		MntGetResponse mntGetResponse = monitoringService.getMonitoring(petId, monitoringId,
				user.getUsername());
		return Response.success(mntGetResponse);
	}
}
