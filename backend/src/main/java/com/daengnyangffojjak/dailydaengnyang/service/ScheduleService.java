package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleCreateRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleCreateResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleModifyRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleModifyResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Schedule;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.ScheduleException;
import com.daengnyangffojjak.dailydaengnyang.exception.UserException;
import com.daengnyangffojjak.dailydaengnyang.repository.PetRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.ScheduleRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Objects;
import static com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;

    // 일정 등록
    public ScheduleCreateResponse create(Long petId, ScheduleCreateRequest scheduleCreateRequest, String userName) {

        // 유저가 없는 경우
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ScheduleException(INVALID_PERMISSION));

        // 펫이 없는 경우
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ScheduleException(PET_NOT_FOUND));

        // 일정 저장
        Schedule schedule = new Schedule();
        Schedule savedSchedule = scheduleRepository.save(schedule.of(pet, user, scheduleCreateRequest));

        return ScheduleCreateResponse.builder()
                .message("일정 등록 완료")
                .id(savedSchedule.getId())
                .build();

    }

    // 일정 수정
    public ScheduleModifyResponse modify(Long petId, Long scheduleId, ScheduleModifyRequest scheduleModifyRequest, String userName) {

        // 유저가 없는 경우
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ScheduleException(INVALID_PERMISSION));

        // 펫이 없는 경우
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ScheduleException(PET_NOT_FOUND));

        // 일정이 없는 경우
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

        // 로그인유저 != 일정작성유저
        Long loginUserId = user.getId();
        Long scheduleWriteUserId = schedule.getUser().getId();

        if (!Objects.equals(scheduleWriteUserId, loginUserId)){
            throw new ScheduleException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다.");
        }

        // 수정된 일정 저장
        schedule.setTitle(scheduleModifyRequest.getTitle());
        schedule.setBody(scheduleModifyRequest.getBody());
        Schedule savedSchedule = scheduleRepository.saveAndFlush(schedule);

        return ScheduleModifyResponse.builder()
                .message("일정 수정 완료")
                .id(savedSchedule.getId())
                .build();

    }

}
