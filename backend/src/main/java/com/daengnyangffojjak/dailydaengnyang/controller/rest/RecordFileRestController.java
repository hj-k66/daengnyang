package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordFileResponse;
import com.daengnyangffojjak.dailydaengnyang.service.RecordFileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pets/{petId}/records/{recordId}")
public class RecordFileRestController {

	private final RecordFileService recordFileService;

	@PostMapping(value = "/file/upload")
	public ResponseEntity<Response<RecordFileResponse>> uploadFile(@PathVariable Long petId,
			@PathVariable Long recordId, @RequestPart
	List<MultipartFile> multipartFiles, @AuthenticationPrincipal UserDetails user) {

		RecordFileResponse recordFileResponse = recordFileService.uploadFile(petId, recordId,
				multipartFiles, user.getUsername());
		return ResponseEntity.ok().body(Response.success(recordFileResponse));
	}

}
