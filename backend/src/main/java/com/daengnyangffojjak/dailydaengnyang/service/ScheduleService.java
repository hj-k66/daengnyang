package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.*;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Schedule;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.UserGroup;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.ScheduleException;
import com.daengnyangffojjak.dailydaengnyang.repository.ScheduleRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import com.daengnyangffojjak.dailydaengnyang.utils.event.ScheduleAssignEvent;
import com.daengnyangffojjak.dailydaengnyang.utils.event.ScheduleCompleteEvent;
import com.daengnyangffojjak.dailydaengnyang.utils.event.ScheduleCreateEvent;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

	private final ScheduleRepository scheduleRepository;
	private final Validator validator;
	private final ApplicationEventPublisher applicationEventPublisher;

	//일정 등록
	@Transactional
	public ScheduleCreateResponse create(Long petId, ScheduleCreateRequest scheduleCreateRequest,
			String userName) {
		log.info("일정 생성 로직 시작");

		//유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		//Pet과 userName인 User가 같은 그룹이면 Pet을 반환
		Pet pet = validator.getPetWithUsername(petId, user.getUsername());

		//등록 된 태그가 없으면 예외발생
		Tag tag = validator.getTagById(scheduleCreateRequest.getTagId());

		//알림 전송 - 그룹원 모두에게 전송
		//해당 그룹(pet의 그룹) 내 멤버 이름 불러오기
		List<UserGroup> userGroupList = validator.getUserGroupListByUsername(
				pet.getGroup(), userName);
		List<User> userList = userGroupList.stream()
				.map(UserGroup::getUser).collect(
						Collectors.toList());

		applicationEventPublisher.publishEvent(
				new ScheduleCreateEvent(userList, scheduleCreateRequest.getTitle(), userName));

		//일정 저장
		Schedule savedSchedule = scheduleRepository.save(
				scheduleCreateRequest.toEntity(pet, user, tag));

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

	//일정 상세 조회(단건)
	@Transactional(readOnly = true)
	public ScheduleResponse get(Long petId, Long scheduleId, String userName) {

		//유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		//Pet과 userName인 User가 같은 그룹이면 Pet을 반환
		Pet pet = validator.getPetWithUsername(petId, user.getUsername());

		//일정이 없는 경우 예외발생
		Schedule schedule = scheduleRepository.findById(scheduleId)
				.orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

		//그룹 내 멤버 리스트 반환
		List<UserGroup> userGroupList = validator.getUserGroupListByUsername(
				pet.getGroup(), userName);

		//assigneeId == userId 찾아서 해당 userId의 roleInGroup 반환
		String roleInGroup = "";

		for (UserGroup userGroup : userGroupList
		) {
			if (userGroup.getUser().getId() == schedule.getAssigneeId()) {
				roleInGroup = userGroup.getRoleInGroup();
			}
		}

		return ScheduleResponse.toResponse(user, pet, schedule, roleInGroup);

	}

	//개체별 일정 전체 보기
	@Transactional(readOnly = true)
	public Page<ScheduleListResponse> list(Long petId, String userName, Pageable pageable) {

		//Pet과 userName인 User가 같은 그룹이면 Pet을 반환
		Pet pet = validator.getPetWithUsername(petId, userName);

		//그룹 내 멤버 리스트 반환
		List<UserGroup> userGroupList = validator.getUserGroupListByUsername(
				pet.getGroup(), userName);

		//유저아이디 : 그룹 내 역할 -> 맵 반환
		Map<Long, String> getRoleInGroup = validator.makeMapWithRoleAndId(userGroupList);

		Page<Schedule> schedules = scheduleRepository.findAllByPetId(pet.getId(), pageable);
		return schedules.map(s -> ScheduleListResponse.toResponse(s, getRoleInGroup));

	}

	//일정 책임자 수정 알람
	@Transactional
	public MessageResponse assign(Long petId, Long scheduleId,
			ScheduleAssignRequest scheduleAssignRequest, String userName) {
		//로그인 유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		//일정이 없는 경우 예외발생
		Schedule schedule = scheduleRepository.findById(scheduleId)
				.orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

		//Pet과 userName인 User가 같은 그룹이면 Pet을 반환
		Pet pet = validator.getPetWithUsername(petId, user.getUsername());

		//assignrequest receiverId가 userName(pet)과 같은 그룹이 아닐 경우 예외발생
		//receiver 유저가 없는 경우 예외 발생 같이 처리
		validator.getUserGroupListByUsername(pet.getGroup(),
				scheduleAssignRequest.getReceiverName());

		//로그인유저 != (일정작성유저 or 이전 일정 책임자)일 경우 예외발생
		Long loginUserId = user.getId();
		Long scheduleWriteUserId = schedule.getUser().getId();
		Long assigneeId = schedule.getAssigneeId();

		if (!loginUserId.equals(scheduleWriteUserId) && !loginUserId.equals(assigneeId)) {
			throw new ScheduleException(ErrorCode.INVALID_PERMISSION);
		}

		//수정된 일정 저장
		User receiver = validator.getUserByUserName(scheduleAssignRequest.getReceiverName());
		schedule.changeToAssignee(receiver.getId());
		scheduleRepository.saveAndFlush(schedule);

		//알림 전송 - 부탁받은 대상자만
		applicationEventPublisher.publishEvent(
				new ScheduleAssignEvent(receiver, scheduleAssignRequest.getMessage(),
						userName, schedule.getTitle()));

		return new MessageResponse("일정의 책임자가 변경되었습니다.");

	}


	@Transactional
	public ScheduleCompleteResponse complete(Long petId, Long scheduleId, ScheduleCompleteRequest scheduleCompleteRequest, String username) {
		//로그인 유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(username);

		//일정이 없는 경우 예외발생
		Schedule schedule = scheduleRepository.findById(scheduleId)
				.orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

		//Pet과 userName인 User가 같은 그룹이면 Pet을 반환
		Pet pet = validator.getPetWithUsername(petId, user.getUsername());


		//로그인유저 != (일정작성유저 or 일정 책임자)일 경우 예외발생
		Long loginUserId = user.getId();
		Long scheduleWriteUserId = schedule.getUser().getId();
		Long assigneeId = schedule.getAssigneeId();

		if (!loginUserId.equals(scheduleWriteUserId) && !loginUserId.equals(assigneeId)) {
			throw new ScheduleException(ErrorCode.INVALID_PERMISSION);
		}

		//수정된 일정 저장
		schedule.changeToCompleted(scheduleCompleteRequest.isCompleted());
		scheduleRepository.saveAndFlush(schedule);

		//알림 전송 - 그룹원 모두에게
		//알림 전송 - 그룹원 모두에게 전송
		//해당 그룹(pet의 그룹) 내 멤버 이름 불러오기
		List<UserGroup> userGroupList = validator.getUserGroupListByUsername(
				pet.getGroup(), username);
		List<User> userList = userGroupList.stream()
				.map(UserGroup::getUser).collect(
						Collectors.toList());

		applicationEventPublisher.publishEvent(
				new ScheduleCompleteEvent(userList, schedule.getTitle(), username));

		return new ScheduleCompleteResponse("일정이 완료되었습니다.", scheduleId);
	}

	@Transactional(readOnly = true)
	public List<ScheduleListResponse> getScheduleList(Long petId, String fromDate, String toDate, String username) {
		User user = validator.getUserByUserName(username);
		Pet pet = validator.getPetWithUsername(petId, username);

		//그룹 내 멤버 리스트 반환
		List<UserGroup> userGroupList = validator.getUserGroupListByUsername(
				pet.getGroup(), username);
		Map<Long, String> getRoleInGroup = validator.makeMapWithRoleAndId(userGroupList);

		LocalDateTime start = getLocalDateFromString(fromDate);
		LocalDateTime end = getLocalDateFromString(toDate);

		List<Schedule> schedules = scheduleRepository.findAllByCreatedAtBetweenAndPetId(
				Sort.by(Direction.DESC, "dueDate"), start, end, petId);

		return schedules.stream().map(s -> ScheduleListResponse.toResponse(s, getRoleInGroup))
				.toList();
	}

	private LocalDateTime getLocalDateFromString (String date) {
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(4, 6));
		int day = Integer.parseInt(date.substring(6, 8));

		return LocalDateTime.of(year, month, day, 0, 0, 0);
	}
}
