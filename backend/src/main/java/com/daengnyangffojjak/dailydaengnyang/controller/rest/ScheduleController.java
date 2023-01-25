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
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pets")
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 일정 등록
    @PostMapping(value = "/{petId}/schedules")
    public ResponseEntity<Response<ScheduleCreateResponse>> createSchedule(@PathVariable Long petId, @RequestBody ScheduleCreateRequest scheduleCreateRequest, Authentication authentication){
        log.debug("petId : {} / scheduleCreateRequest : {} / authentication : {} ", petId, scheduleCreateRequest, authentication.getName());
        ScheduleCreateResponse scheduleCreateResponse = scheduleService.create(petId, scheduleCreateRequest, authentication.getName());
        return ResponseEntity.ok().body(Response.success(scheduleCreateResponse));
    }

    // 일정 수정
    @PutMapping(value = "/{petId}/schedules/{schedulesId}")
    public ResponseEntity<Response<ScheduleModifyResponse>> modifySchedule(@PathVariable Long petId, @PathVariable Long scheduleId, @RequestBody ScheduleModifyRequest scheduleModifyRequest, Authentication authentication){
        log.debug("scheduleId : {} / scheduleModifyRequest : {} / authentication : {} ", scheduleId, scheduleModifyRequest, authentication.getName());
        ScheduleModifyResponse scheduleModifyResponse = scheduleService.modify(petId, scheduleId, scheduleModifyRequest, authentication.getName());
        return ResponseEntity.ok().body(Response.success(scheduleModifyResponse));
    }

}
