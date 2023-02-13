package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordFileResponse;
import com.daengnyangffojjak.dailydaengnyang.service.RecordFileService;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
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

	// 파일 업로드
	@PostMapping(value = "/recordFiles/upload")
	public ResponseEntity<Response<RecordFileResponse>> uploadFile(@PathVariable Long petId,
			@PathVariable Long recordId, @RequestPart(required = false)
	List<MultipartFile> multipartFiles, @AuthenticationPrincipal UserDetails user) {

		RecordFileResponse recordFileResponse = recordFileService.uploadRecordFiles(petId, recordId,
				multipartFiles, user.getUsername());
		return ResponseEntity.created(
				URI.create("api/v1/pets/" + petId + "/schedules/"
						+ recordId)).body(Response.success(recordFileResponse));
	}

	// 파일 삭제
	@DeleteMapping(value = "/recordFiles/{recordFileId}")
	public ResponseEntity<Response<RecordFileResponse>> deleteRecordFile(@PathVariable Long petId,
			@PathVariable Long recordId, @PathVariable Long recordFileId,
			@AuthenticationPrincipal UserDetails user) {

		RecordFileResponse recordFileResponse = recordFileService.deleteRecordFile(petId, recordId,
				recordFileId, user.getUsername());
		return ResponseEntity.ok().body(Response.success(recordFileResponse));
	}

}
