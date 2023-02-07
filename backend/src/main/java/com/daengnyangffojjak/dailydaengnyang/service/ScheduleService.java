package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.*;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Schedule;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.ScheduleException;
import com.daengnyangffojjak.dailydaengnyang.repository.ScheduleRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.TagRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

	private final ScheduleRepository scheduleRepository;
	private final Validator validator;

	//일정 등록
	@Transactional
	public ScheduleCreateResponse create(Long petId, ScheduleCreateRequest scheduleCreateRequest,
			String userName) {

		//유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		//Pet과 userName인 User가 같은 그룹이면 Pet을 반환
		Pet pet = validator.getPetWithUsername(petId, user.getUsername());

		//등록 된 태그가 없으면 예외발생
		Tag tag = validator.getTagById(scheduleCreateRequest.getTagId());

		//일정 저장
		Schedule savedSchedule = scheduleRepository.save(scheduleCreateRequest.toEntity(pet, user, tag));
		String message = "일정 등록 완료";

		return ScheduleCreateResponse.toResponse(message, savedSchedule);

	}

	//일정 수정
	@Transactional
	public ScheduleModifyResponse modify(Long petId, Long scheduleId,
			ScheduleModifyRequest scheduleModifyRequest, String userName) {

		//유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		//Pet과 userName인 User가 같은 그룹이면 Pet을 반환
		Pet pet = validator.getPetWithUsername(petId, user.getUsername());

		//등록 된 태그가 없으면 예외발생
		Tag tag = validator.getTagById(scheduleModifyRequest.getTagId());

		//일정이 없는 경우 예외발생
		Schedule schedule = scheduleRepository.findById(scheduleId)
				.orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

		//로그인유저 != 일정작성유저일 경우 예외발생
		Long loginUserId = user.getId();
		Long scheduleWriteUserId = schedule.getUser().getId();

		if (!loginUserId.equals(scheduleWriteUserId)) {
			throw new ScheduleException(ErrorCode.INVALID_PERMISSION);
		}

		//수정된 일정 저장
		schedule.changeToSchedule(pet, scheduleModifyRequest, tag);
		Schedule modifySchedule = scheduleRepository.saveAndFlush(schedule);

		return ScheduleModifyResponse.toResponse(modifySchedule);

	}

	//일정 삭제
	@Transactional
	public ScheduleDeleteResponse delete(Long scheduleId, String userName) {

		//유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		//일정이 없는 경우 예외발생
		Schedule schedule = scheduleRepository.findById(scheduleId)
				.orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

		//로그인유저 != 일정작성유저일 경우 예외발생
		Long loginUserId = user.getId();
		Long scheduleWriteUserId = schedule.getUser().getId();

		if (!loginUserId.equals(scheduleWriteUserId)) {
			throw new ScheduleException(ErrorCode.INVALID_PERMISSION);
		}

		//일정 삭제
		schedule.deleteSoftly();
		String message = "일정이 삭제되었습니다.";

		return ScheduleDeleteResponse.toResponse(message);

	}

	// 일정 상세 조회(단건)
	@Transactional(readOnly = true)
	public ScheduleResponse get(Long petId, Long scheduleId, String userName) {

		//유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		//Pet과 userName인 User가 같은 그룹이면 Pet을 반환
		Pet pet = validator.getPetWithUsername(petId, user.getUsername());

		//일정이 없는 경우 예외발생
		Schedule schedule = scheduleRepository.findById(scheduleId)
				.orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

		return ScheduleResponse.toResponse(user, pet, schedule);

	}

	// 개체별 일정 전체 보기
	@Transactional(readOnly = true)
	public Page<ScheduleListResponse> list(Long petId, String userName, Pageable pageable) {

		//Pet과 userName인 User가 같은 그룹이면 Pet을 반환
		Pet pet = validator.getPetWithUsername(petId, userName);

		Page<Schedule> schedules = scheduleRepository.findAllByPetId(pet.getId(), pageable);

		return ScheduleListResponse.toResponse(schedules);

	}
}
