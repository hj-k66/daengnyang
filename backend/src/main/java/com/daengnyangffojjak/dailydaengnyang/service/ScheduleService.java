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
import com.daengnyangffojjak.dailydaengnyang.repository.PetRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.ScheduleRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;
import static com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;

    // 일정 등록
    @Transactional
    public ScheduleCreateResponse create(Long petId, ScheduleCreateRequest scheduleCreateRequest, String userName) {

        // 유저가 없는 경우 예외발생
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ScheduleException(USERNAME_NOT_FOUND));

        // 펫이 없는 경우 예외발생
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ScheduleException(PET_NOT_FOUND));

        // 일정 저장
        Schedule savedSchedule = scheduleRepository.saveAndFlush(scheduleCreateRequest.toEntity(pet, user));

        return ScheduleCreateResponse.builder()
                .message("일정 등록 완료")
                .id(savedSchedule.getId())
                .build();

    }

    // 일정 수정
    @Transactional
    public ScheduleModifyResponse modify(Long petId, Long scheduleId, ScheduleModifyRequest scheduleModifyRequest, String userName) {

        // 유저가 없는 경우 예외발생
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ScheduleException(USERNAME_NOT_FOUND));

        // 펫이 없는 경우 예외발생
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ScheduleException(PET_NOT_FOUND));

        // 일정이 없는 경우 예외발생
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

        // 로그인유저 != 일정작성유저일 경우 예외발생
        Long loginUserId = user.getId();
        Long scheduleWriteUserId = schedule.getUser().getId();

        if (!Objects.equals(scheduleWriteUserId, loginUserId)){
            throw new ScheduleException(ErrorCode.INVALID_PERMISSION);
        }

        // 수정된 일정 저장
        schedule.changeToSchedule(scheduleModifyRequest);
        Schedule savedSchedule = scheduleRepository.saveAndFlush(schedule);

        return ScheduleModifyResponse.builder()
                .id(savedSchedule.getId())
                .title(schedule.getTitle())
                .lastModifiedAt(schedule.getLastModifiedAt())
                .build();

    }

}
