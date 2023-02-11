package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordWorkRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordWorkResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.exception.RecordException;
import com.daengnyangffojjak.dailydaengnyang.repository.RecordRepository;

import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
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

	// 일기 상세(1개) 조회
	@Transactional(readOnly = true)
	public RecordResponse getOneRecord(Long petId, Long recordId, String userName) {

		// 유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

		//Pet과 userName인 User가 같은 그룹이면 Pet을 반환
		Pet pet = validator.getPetWithUsername(petId, user.getUsername());

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

		//Pet과 userName인 User가 같은 그룹이면 Pet을 반환
		Pet pet = validator.getPetWithUsername(petId, user.getUsername());

		//등록 된 태그가 없으면 예외발생
		Tag tag = validator.getTagById(recordWorkRequest.getTagId());

		Record savedRecord = recordRepository.save(recordWorkRequest.toEntity(user, pet, tag));

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

		//Pet과 userName인 User가 같은 그룹이면 Pet을 반환
		Pet pet = validator.getPetWithUsername(petId, user.getUsername());

		// 일기가 없는 경우 예외발생
		Record record = validator.getRecordById(recordId);

		//등록 된 태그가 없으면 예외발생
		Tag tag = validator.getTagById(recordWorkRequest.getTagId());

		// 일기 작성 유저와 로그인 유저가 같지 않을 경우 예외발생
		if (!record.getUser().getId().equals(user.getId())) {
			throw new RecordException(INVALID_PERMISSION);
		}

		record.modifyRecord(recordWorkRequest, tag);
		Record updated = recordRepository.saveAndFlush(record);

		return RecordWorkResponse.builder()
				.message("일기 수정 완료")
				.recordId(updated.getId())
				.build();
	}

	// 일기 삭제
	@Transactional
	public RecordWorkResponse deleteRecord(Long recordId, String userName) {

		// 유저가 없는 경우 예외발생
		User user = validator.getUserByUserName(userName);

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