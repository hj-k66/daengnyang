package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordResultRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordResultResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.exception.RecordException;
import com.daengnyangffojjak.dailydaengnyang.repository.PetRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.RecordRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.UserRepository;

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

	private final UserRepository userRepository;
	private final PetRepository petRepository;
	private final RecordRepository recordRepository;

	// 일기 상세(1개) 조회
	public RecordResponse getOneRecord(Long petId, Long recordId, String userName) {

		// 유저가 없는 경우 예외발생
		User user = userRepository.findByUserName(userName)
				.orElseThrow(() -> new RecordException(USERNAME_NOT_FOUND));

		// 펫이 없는 경우 예외발생
		Pet pet = petRepository.findById(petId)
				.orElseThrow(() -> new RecordException(PET_NOT_FOUND));

		// 일기가 없는 경우 예외발생
		Record record = recordRepository.findById(recordId)
				.orElseThrow(() -> new RecordException(RECORD_NOT_FOUND));

		return RecordResponse.of(user, pet, record);
	}

	// 전체 피드 조회
	@Transactional
	public Page<RecordResponse> getAllRecords(Pageable pageable) {

		return recordRepository.findAllByIsPublicTrue(pageable)
				.map(RecordResponse::of);
	}

	// 일기 작성
	@Transactional
	public RecordResultResponse createRecord(Long petId, RecordResultRequest recordResultRequest,
			String userName) {

		// 유저가 없는 경우 예외발생
		User user = userRepository.findByUserName(userName)
				.orElseThrow(() -> new RecordException(USERNAME_NOT_FOUND));

		// 펫이 없는 경우 예외발생
		Pet pet = petRepository.findById(petId)
				.orElseThrow(() -> new RecordException(PET_NOT_FOUND));

		Record savedRecord = recordRepository.save(recordResultRequest.toEntity(user, pet));

		return RecordResultResponse.builder()
				.message("일기 작성 완료")
				.recordId(savedRecord.getId())
				.build();
	}

	// 일기 수정
	@Transactional
	public RecordResultResponse modifyRecord(Long petId, Long recordId,
			RecordResultRequest recordResultRequest, String userName) {

		// 유저가 없는 경우 예외발생
		User user = userRepository.findByUserName(userName)
				.orElseThrow(() -> new RecordException(USERNAME_NOT_FOUND));

		// 펫이 없는 경우 예외발생
		Pet pet = petRepository.findById(petId)
				.orElseThrow(() -> new RecordException(PET_NOT_FOUND));

		// 일기가 없는 경우 예외발생
		Record record = recordRepository.findById(recordId)
				.orElseThrow(() -> new RecordException(RECORD_NOT_FOUND));

		// 일기 작성 유저와 로그인 유저가 같지 않을 경우 예외발생
		if (!record.getUser().getId().equals(user.getId())) {
			throw new RecordException(INVALID_PERMISSION);
		}

		record.modifyRecord(recordResultRequest.editEntity());
		Record updated = recordRepository.saveAndFlush(record);

		return RecordResultResponse.builder()
				.message("일기 수정 완료")
				.recordId(record.getId())
				.build();
	}

	// 일기 삭제
	@Transactional
	public RecordResultResponse deleteRecord(Long petId, Long recordId, String userName) {

		// 유저가 없는 경우 예외발생
		User user = userRepository.findByUserName(userName)
				.orElseThrow(() -> new RecordException(USERNAME_NOT_FOUND));

		// 펫이 없는 경우 예외발생
		Pet pet = petRepository.findById(petId)
				.orElseThrow(() -> new RecordException(PET_NOT_FOUND));

		// 일기가 없는 경우 예외발생
		Record record = recordRepository.findById(recordId)
				.orElseThrow(() -> new RecordException(RECORD_NOT_FOUND));

		// 일기 작성 유저와 로그인 유저가 같지 않을 경우 예외발생
		if (!Objects.equals(record.getUser().getId(), user.getId())) {
			throw new RecordException(INVALID_PERMISSION);
		}

		record.deleteSoftly();
		return RecordResultResponse.builder()
				.message("일기 삭제 완료")
				.recordId(recordId)
				.build();
	}

}