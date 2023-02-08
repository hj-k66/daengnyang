package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordWorkRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordWorkResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.UserGroup;
import com.daengnyangffojjak.dailydaengnyang.exception.RecordException;
import com.daengnyangffojjak.dailydaengnyang.repository.RecordRepository;

import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import com.daengnyangffojjak.dailydaengnyang.utils.event.RecordCreateEvent;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class RecordService {

	private final RecordRepository recordRepository;
	private final Validator validator;
	private final ApplicationEventPublisher applicationEventPublisher;


	// 일기 상세(1개) 조회
	@Transactional(readOnly = true)
	public RecordResponse getOneRecord(Long petId, Long recordId, String userName) {

		// 유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		// 펫이 없는 경우 예외발생
		Pet pet = validator.getPetById(petId);

		// 일기가 없는 경우 예외발생
		Record record = validator.getRecordById(recordId);

		return RecordResponse.of(user, pet, record);
	}

	// 전체 피드 조회
	@Transactional(readOnly = true)
	public Page<RecordResponse> getAllRecords(Pageable pageable) {

		return recordRepository.findAllByIsPublicTrue(pageable)
				.map(RecordResponse::from);
	}

	// 일기 작성
	@Transactional
	public RecordWorkResponse createRecord(Long petId, RecordWorkRequest recordWorkRequest,
			String userName) {

		// 유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		// 펫이 없는 경우 예외발생
		Pet pet = validator.getPetById(petId);

		Record savedRecord = recordRepository.save(recordWorkRequest.toEntity(user, pet));

		//알림 전송 - 그룹원 모두에게 전송
		List<UserGroup> userGroupList = validator.getUserGroupListByUsername(
				pet.getGroup(), userName);
		List<String> userNameList = userGroupList.stream()
				.map(userGroup -> userGroup.getUser().getUsername()).collect(
						Collectors.toList());
		//이벤트 발생
		applicationEventPublisher.publishEvent(
				new RecordCreateEvent(userNameList, recordWorkRequest.getTitle(), userName));

		return RecordWorkResponse.builder()
				.message("일기 작성 완료")
				.recordId(savedRecord.getId())
				.build();
	}

	// 일기 수정
	@Transactional
	public RecordWorkResponse modifyRecord(Long petId, Long recordId,
			RecordWorkRequest recordWorkRequest, String userName) {

		// 유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		// 펫이 없는 경우 예외발생
		Pet pet = validator.getPetById(petId);

		// 일기가 없는 경우 예외발생
		Record record = validator.getRecordById(recordId);

		// 일기 작성 유저와 로그인 유저가 같지 않을 경우 예외발생
		if (!record.getUser().getId().equals(user.getId())) {
			throw new RecordException(INVALID_PERMISSION);
		}

		record.modifyRecord(recordWorkRequest);
		Record updated = recordRepository.saveAndFlush(record);

		return RecordWorkResponse.builder()
				.message("일기 수정 완료")
				.recordId(updated.getId())
				.build();
	}

	// 일기 삭제
	@Transactional
	public RecordWorkResponse deleteRecord(Long petId, Long recordId, String userName) {

		// 유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		// 펫이 없는 경우 예외발생
		Pet pet = validator.getPetById(petId);

		// 일기가 없는 경우 예외발생
		Record record = validator.getRecordById(recordId);

		// 일기 작성 유저와 로그인 유저가 같지 않을 경우 예외발생
		if (!Objects.equals(record.getUser().getId(), user.getId())) {
			throw new RecordException(INVALID_PERMISSION);
		}

		record.deleteSoftly();
		return RecordWorkResponse.builder()
				.message("일기 삭제 완료")
				.recordId(recordId)
				.build();
	}

}