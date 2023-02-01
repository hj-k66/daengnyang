package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.*;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Schedule;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.ScheduleException;
import com.daengnyangffojjak.dailydaengnyang.repository.ScheduleRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ScheduleService {

	private final ScheduleRepository scheduleRepository;
	private final Validator validator;

	// 일정 등록
	@Transactional
	public ScheduleCreateResponse create(Long petId, ScheduleCreateRequest scheduleCreateRequest,
			String userName) {

		// 유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		// 펫이 없는 경우 예외발생
		Pet pet = validator.getPetById(petId);

		// 일정 저장
		Schedule savedSchedule = scheduleRepository.save(scheduleCreateRequest.toEntity(pet, user));

		return ScheduleCreateResponse.toResponse(savedSchedule);

	}

	// 일정 수정
	@Transactional
	public ScheduleModifyResponse modify(Long petId, Long scheduleId,
			ScheduleModifyRequest scheduleModifyRequest, String userName) {

		// 유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		// 펫이 없는 경우 예외발생
		Pet pet = validator.getPetById(petId);

		// 일정이 없는 경우 예외발생
		Schedule schedule = scheduleRepository.findById(scheduleId)
				.orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

		// 로그인유저 != 일정작성유저일 경우 예외발생
		Long loginUserId = user.getId();
		Long scheduleWriteUserId = schedule.getUser().getId();

		if (!loginUserId.equals(scheduleWriteUserId)) {
			throw new ScheduleException(ErrorCode.INVALID_PERMISSION);
		}

		// 수정된 일정 저장
		schedule.changeToSchedule(scheduleModifyRequest);
		Schedule savedSchedule = scheduleRepository.saveAndFlush(schedule);

		return ScheduleModifyResponse.toResponse(savedSchedule, schedule);

	}

	// 일정 삭제
	@Transactional
	public ScheduleDeleteResponse delete(Long petId, Long scheduleId, String userName) {

		// 유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		// 펫이 없는 경우 예외발생
		Pet pet = validator.getPetById(petId);

		// 일정이 없는 경우 예외발생
		Schedule schedule = scheduleRepository.findById(scheduleId)
				.orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

		// 로그인유저 != 일정작성유저일 경우 예외발생
		Long loginUserId = user.getId();
		Long scheduleWriteUserId = schedule.getUser().getId();

		if (!loginUserId.equals(scheduleWriteUserId)) {
			throw new ScheduleException(ErrorCode.INVALID_PERMISSION);
		}

		// 일정 삭제
		schedule.deleteSoftly();
		String message = "일정이 삭제되었습니다.";

		return ScheduleDeleteResponse.toResponse(message);

	}

	// 일정 상세 조회(단건)
	@Transactional
	public ScheduleResponse get(Long petId, Long scheduleId, String userName) {

		// 유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		// 펫이 없는 경우 예외발생
		Pet pet = validator.getPetById(petId);

		// 일정이 없는 경우 예외발생
		Schedule schedule = scheduleRepository.findById(scheduleId)
				.orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

		return ScheduleResponse.toResponse(user, pet, schedule);

	}

	// 개체별 일정 전체 보기
	@Transactional
	public Page<ScheduleListResponse> list(Long petId, Pageable pageable) {

		Page<Schedule> schedules = scheduleRepository.findAllByPetId(petId, pageable);

		return ScheduleListResponse.toResponse(schedules);

	}
}
