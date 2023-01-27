package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleCreateRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleCreateResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleModifyRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleModifyResponse;
import com.daengnyangffojjak.dailydaengnyang.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class ScheduleRestController {

    private final ScheduleService scheduleService;

    // 일정 등록
    @PostMapping(value = "/pets/{petId}/schedules")
    public ResponseEntity<Response<ScheduleCreateResponse>> createSchedule(@PathVariable Long petId, @RequestBody ScheduleCreateRequest scheduleCreateRequest, @AuthenticationPrincipal UserDetails user){
        log.debug("petId : {} / scheduleCreateRequest : {} / authentication : {} ", petId, scheduleCreateRequest, user.getUsername());
        ScheduleCreateResponse scheduleCreateResponse = scheduleService.create(petId, scheduleCreateRequest, user.getUsername());
        return ResponseEntity.ok().body(Response.success(scheduleCreateResponse));
    }

    // 일정 수정
    @PutMapping(value = "/pets/{petId}/schedules/{scheduleId}")
    public ResponseEntity<Response<ScheduleModifyResponse>> modifySchedule(@PathVariable Long petId, @PathVariable Long scheduleId, @RequestBody ScheduleModifyRequest scheduleModifyRequest, @AuthenticationPrincipal UserDetails user){
        log.debug("scheduleId : {} / scheduleModifyRequest : {} / authentication : {} ", scheduleId, scheduleModifyRequest, user.getUsername());
        ScheduleModifyResponse scheduleModifyResponse = scheduleService.modify(petId, scheduleId, scheduleModifyRequest, user.getUsername());
        return ResponseEntity.ok().body(Response.success(scheduleModifyResponse));
    }

}
