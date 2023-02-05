package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.disease.DizWriteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.disease.DizWriteResponse;
import com.daengnyangffojjak.dailydaengnyang.service.DiseaseService;
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
public class DiseaseRestController {
	private final DiseaseService diseaseService;

	@PostMapping(value = "/pets/{petId}/diseases")
	public ResponseEntity<Response<DizWriteResponse>> create(@AuthenticationPrincipal UserDetails user, @PathVariable Long petId,
			@Valid @RequestBody DizWriteRequest dizWriteRequest) {
		DizWriteResponse dizWriteResponse = diseaseService.create(petId, dizWriteRequest,
				user.getUsername());
		return ResponseEntity.created(
				URI.create("/api/v1/pets/"+petId+"/diseases/"+dizWriteResponse.getId()))
				.body(Response.success(dizWriteResponse));
	}
	@PutMapping(value = "/pets/{petId}/diseases/{diseaseId}")
	public ResponseEntity<Response<DizWriteResponse>> modify(
			@AuthenticationPrincipal UserDetails user, @PathVariable Long petId,
			@PathVariable Long diseaseId,
			@Valid @RequestBody DizWriteRequest dizWriteRequest) {
		DizWriteResponse dizWriteResponse = diseaseService.modify(petId, diseaseId,
				dizWriteRequest,
				user.getUsername());
		return ResponseEntity.created(
						URI.create("/api/v1/pets/" + petId + "/diseases/" + diseaseId))
				.body(Response.success(dizWriteResponse));
	}
}
