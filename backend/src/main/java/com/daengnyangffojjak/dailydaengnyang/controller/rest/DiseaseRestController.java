package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.disease.DizGetResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.disease.DizWriteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.disease.DizWriteResponse;
import com.daengnyangffojjak.dailydaengnyang.service.DiseaseService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
public class DiseaseRestController {

	private final DiseaseService diseaseService;

	@PostMapping(value = "/pets/{petId}/diseases")
	public ResponseEntity<Response<DizWriteResponse>> create(
			@AuthenticationPrincipal UserDetails user, @PathVariable Long petId,
			@Valid @RequestBody DizWriteRequest dizWriteRequest) {
		DizWriteResponse dizWriteResponse = diseaseService.create(petId, dizWriteRequest,
				user.getUsername());
		return ResponseEntity.created(
						URI.create("/api/v1/pets/" + petId + "/diseases/" + dizWriteResponse.getId()))
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

	@DeleteMapping(value = "/pets/{petId}/diseases/{diseaseId}")
	public Response<MessageResponse> delete(
			@AuthenticationPrincipal UserDetails user, @PathVariable Long petId,
			@PathVariable Long diseaseId) {
		MessageResponse response = diseaseService.delete(petId, diseaseId, user.getUsername());
		return Response.success(response);
	}

	@GetMapping(value = "/pets/{petId}/diseases/{diseaseId}")
	public Response<DizGetResponse> getDisease(
			@AuthenticationPrincipal UserDetails user, @PathVariable Long petId,
			@PathVariable Long diseaseId) {
		DizGetResponse dizGetResponse = diseaseService.getDisease(petId, diseaseId,
				user.getUsername());
		return Response.success(dizGetResponse);
	}

	@GetMapping(value = "/pets/{petId}/diseases")
	public Response<Page<DizGetResponse>> getDiseaseList(
			@PageableDefault(size=5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
			@AuthenticationPrincipal UserDetails user, @PathVariable Long petId) {
		Page<DizGetResponse> dizGetResponses = diseaseService.getDiseaseList(petId, pageable,
				user.getUsername());
		return Response.success(dizGetResponses);
	}
}
