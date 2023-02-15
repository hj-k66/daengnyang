package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.*;
import com.daengnyangffojjak.dailydaengnyang.service.ScheduleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class ScheduleRestController {

	private final ScheduleService scheduleService;

	//일정 완료하기
	@PutMapping(value = "/pets/{petId}/schedules/{scheduleId}/completed")
	public ResponseEntity<Response<ScheduleCompleteResponse>> completeSchedule(@PathVariable Long petId, @PathVariable Long scheduleId,
			@RequestBody ScheduleCompleteRequest scheduleCompleteRequest,
			@AuthenticationPrincipal UserDetails user) {
		 ScheduleCompleteResponse scheduleCompleteResponse = scheduleService.complete(petId, scheduleId,
				scheduleCompleteRequest, user.getUsername());
		log.info("일정 완료하기가 완료되었습니다.");
		return ResponseEntity.created(
						URI.create("api/v1/pets/" + petId + "/schedules/" + scheduleId +"/completed"))
				.body(Response.success(scheduleCompleteResponse));

	}

	//일정 부탁하기
	@PutMapping(value = "/pets/{petId}/schedules/{scheduleId}/assign")
	public ResponseEntity<Response<MessageResponse>> assignSchedule(@PathVariable Long petId, @PathVariable Long scheduleId,
			@RequestBody ScheduleAssignRequest scheduleAssignRequest,
			@AuthenticationPrincipal UserDetails user) {
		MessageResponse messageResponse = scheduleService.assign(petId, scheduleId,
				scheduleAssignRequest, user.getUsername());
		log.info("일정 부탁하기가 완료되었습니다.");
		return ResponseEntity.created(
						URI.create("api/v1/pets/" + petId + "/schedules/" + scheduleId +"/assign"))
				.body(Response.success(messageResponse));

	}

	//일정 등록
	@PostMapping(value = "/pets/{petId}/schedules")
	public ResponseEntity<Response<ScheduleCreateResponse>> createSchedule(@PathVariable Long petId,
			@RequestBody ScheduleCreateRequest scheduleCreateRequest,
			@AuthenticationPrincipal UserDetails user) {
		log.debug("petId : {} /scheduleCreateRequest : {} /authentication : {} ", petId,
				scheduleCreateRequest, user.getUsername());
		ScheduleCreateResponse scheduleCreateResponse = scheduleService.create(petId,
				scheduleCreateRequest, user.getUsername());
		log.info("일정 등록이 완료되었습니다.");
		return ResponseEntity.created(
						URI.create("api/v1/pets/" + petId + "/schedules/" + scheduleCreateResponse.getId()))
				.body(Response.success(scheduleCreateResponse));

	}

	//일정 수정
	@PutMapping(value = "/pets/{petId}/schedules/{scheduleId}")
	public ResponseEntity<Response<ScheduleModifyResponse>> modifySchedule(@PathVariable Long petId,
			@PathVariable Long scheduleId, @RequestBody ScheduleModifyRequest scheduleModifyRequest,
			@AuthenticationPrincipal UserDetails user) {
		log.debug("scheduleId : {} /scheduleModifyRequest : {} /authentication : {} ", scheduleId,
				scheduleModifyRequest, user.getUsername());
		ScheduleModifyResponse scheduleModifyResponse = scheduleService.modify(petId, scheduleId,
				scheduleModifyRequest, user.getUsername());
		return ResponseEntity.created(
						URI.create("api/v1/pets/" + petId + "/schedules/" + scheduleId))
				.body(Response.success(scheduleModifyResponse));

	}

	//일정 삭제
	@DeleteMapping(value = "/pets/{petId}/schedules/{scheduleId}")
	public ResponseEntity<Response<ScheduleDeleteResponse>> deleteSchedule(@PathVariable Long petId,
			@PathVariable Long scheduleId, @AuthenticationPrincipal UserDetails user) {
		ScheduleDeleteResponse scheduleDeleteResponse = scheduleService.delete(scheduleId,
				user.getUsername());
		return ResponseEntity.ok().body(Response.success(scheduleDeleteResponse));
	}

	//일정 상세 조회(단건)
	@GetMapping(value = "/pets/{petId}/schedules/{scheduleId}")
	public ResponseEntity<Response<ScheduleResponse>> getSchedule(@PathVariable Long petId,
			@PathVariable Long scheduleId, @AuthenticationPrincipal UserDetails user) {
		ScheduleResponse scheduleResponse = scheduleService.get(petId, scheduleId,
				user.getUsername());
		return ResponseEntity.ok().body(Response.success(scheduleResponse));
	}

	//일정 전체 조회
	@GetMapping(value = "/pets/{petId}/schedules")
	public ResponseEntity<Response<Page<ScheduleListResponse>>> listSchedule(
			@PathVariable Long petId, @AuthenticationPrincipal UserDetails user,
			@PageableDefault(size = 20) @SortDefault(sort = "dueDate", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<ScheduleListResponse> scheduleListResponses = scheduleService.list(petId,
				user.getUsername(), pageable);
		return ResponseEntity.ok().body(Response.success(scheduleListResponses));
	}

	// 일정 리스트 기간별 조회
	@GetMapping(value = "/pets/{petId}/schedules/period")
	public Response<List<ScheduleListResponse>> getRecordListWithDate(@AuthenticationPrincipal UserDetails user,
			@PathVariable Long petId, @RequestParam String fromDate, @RequestParam String toDate) {
		List<ScheduleListResponse> scheduleResponses = scheduleService.getScheduleList(petId, fromDate, toDate,
				user.getUsername());
		return Response.success(scheduleResponses);
	}

}
