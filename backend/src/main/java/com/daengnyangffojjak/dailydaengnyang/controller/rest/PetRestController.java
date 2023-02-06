package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetShowResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetUpdateResponse;
import com.daengnyangffojjak.dailydaengnyang.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class PetRestController {

	private final PetService petService;

	// pet 등록
	@PostMapping("/{groupId}/pets")
	public ResponseEntity<Response<PetAddResponse>> addPet(@PathVariable Long groupId,
			@Validated @RequestBody PetAddRequest petAddRequest,
			@AuthenticationPrincipal UserDetails user) {
		PetAddResponse petAddResponse = petService.add(groupId, petAddRequest,
				user.getUsername());

		return ResponseEntity.created(URI.create("/api/v1/groups/" + groupId + "/pets/"+petAddResponse.getId()))
				.body(Response.success(petAddResponse));
	}


	// pet 상세 조회(단건)
	@GetMapping(value = "/{groupsId}/pets/{id}")
	public ResponseEntity<Response<PetShowResponse>> showPet(@PathVariable Long groupsId,
			@PathVariable Long id,
			@AuthenticationPrincipal UserDetails user) {
		return ResponseEntity.ok()
				.body(Response.success(petService.show(groupsId, id, user.getUsername())));
	}


	// pet 수정
	@PutMapping("/{groupId}/pets/{id}")
	public ResponseEntity<Response<PetUpdateResponse>> modifyPet(@PathVariable Long groupId,
			@PathVariable Long id,
			@Validated @RequestBody PetAddRequest petAddRequest,
			@AuthenticationPrincipal UserDetails user) {
		return ResponseEntity.created(URI.create("/api/v1/groups/" + groupId + "/pets/" + id))
				.body(Response.success(
						petService.modify(groupId, id, petAddRequest, user.getUsername())));
	}

	// pet 삭제
	@DeleteMapping("/{groupId}/pets/{id}")
	public ResponseEntity<Response<PetDeleteResponse>> deletePet(@PathVariable Long groupId,
			@PathVariable Long id,
			@AuthenticationPrincipal UserDetails user) {
		return ResponseEntity.ok()
				.body(Response.success(petService.delete(groupId, id, user.getUsername())));
	}
}

