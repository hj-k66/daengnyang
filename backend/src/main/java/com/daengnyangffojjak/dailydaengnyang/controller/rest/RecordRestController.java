package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordWorkRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordWorkResponse;
import com.daengnyangffojjak.dailydaengnyang.service.RecordService;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RecordRestController {

	private final RecordService recordService;

	// 일기 상세(1개) 조회
	@GetMapping(value = "/pets/{petId}/records/{recordId}")
	public ResponseEntity<Response<RecordResponse>> getOneRecord(
			@PathVariable Long petId,
			@PathVariable Long recordId,
			@AuthenticationPrincipal UserDetails user) {

		RecordResponse recordResponse = recordService.getOneRecord(petId, recordId,
				user.getUsername());
		return ResponseEntity.ok().body(Response.success(recordResponse));
	}

	// 전체 피드 조회
	@GetMapping(value = "/records/feed")
	public ResponseEntity<Response<Page>> getAllRecords(
			@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

		Page<RecordResponse> recordResponses = recordService.getAllRecords(pageable);
		return ResponseEntity.ok().body(Response.success(recordResponses));
	}

	// 반려동물의 기간별 일기 리스트 조회
	@GetMapping(value = "/pets/{petId}/records")
	public Response<List<RecordResponse>> getRecordList (@AuthenticationPrincipal UserDetails user, @PathVariable Long petId, @RequestParam String fromDate, @RequestParam String toDate) {
		List<RecordResponse> recordResponses = recordService.getRecordList(petId, fromDate, toDate, user.getUsername());
		return Response.success(recordResponses);
	}

	// 일기 작성
	@PostMapping(value = "/pets/{petId}/records")
	public ResponseEntity<Response<RecordWorkResponse>> createRecord(
			@PathVariable Long petId,
			@RequestBody RecordWorkRequest recordWorkRequest,
			@AuthenticationPrincipal UserDetails user) {

		RecordWorkResponse recordWorkResponse = recordService.createRecord(petId,
				recordWorkRequest, user.getUsername());
		return ResponseEntity.ok().body(Response.success(recordWorkResponse));
	}

	// 일기 수정
	@PutMapping(value = "/pets/{petId}/records/{recordId}")
	public ResponseEntity<Response<RecordWorkResponse>> modifyRecord(
			@PathVariable Long petId,
			@PathVariable Long recordId,
			@RequestBody RecordWorkRequest recordWorkRequest,
			@AuthenticationPrincipal UserDetails user) {

		RecordWorkResponse recordWorkResponse = recordService.modifyRecord(petId, recordId,
				recordWorkRequest, user.getUsername());
		return ResponseEntity.created(
				URI.create("api/v1/pets/" + petId + "/schedules/"
						+ recordId)).body(Response.success(recordWorkResponse));
	}

	// 일기 삭제
	@DeleteMapping(value = "/pets/{petId}/records/{recordId}")
	public ResponseEntity<Response<RecordWorkResponse>> deleteRecord(
			@PathVariable Long petId,
			@PathVariable Long recordId,
			@AuthenticationPrincipal UserDetails user) {

		RecordWorkResponse recordWorkResponse = recordService.deleteRecord(recordId,
				user.getUsername());
		return ResponseEntity.ok().body(Response.success(recordWorkResponse));
	}

	// 찜리스트 모아보기
//	@GetMapping(value = "/records/bookmarks")
//	public ResponseEntity

}