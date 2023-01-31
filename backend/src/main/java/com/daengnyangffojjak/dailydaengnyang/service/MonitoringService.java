package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntWriteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntWriteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Monitoring;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.UserGroup;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.MonitoringException;
import com.daengnyangffojjak.dailydaengnyang.repository.MonitoringRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MonitoringService {

	private final MonitoringRepository monitoringRepository;
	private final Validator validator;

	@Transactional
	public MntWriteResponse create(Long petId, MntWriteRequest mntWriteRequest, String username) {
		Pet pet = validatePetWithUsername(petId, username);
		Monitoring saved = monitoringRepository.save(mntWriteRequest.toEntity(pet));
		return MntWriteResponse.from(saved);
	}

	@Transactional	//일단 pet은 바꿀 수 없음
	public MntWriteResponse modify(Long petId, Long monitoringId, MntWriteRequest mntWriteRequest, String username) {
		Pet pet = validatePetWithUsername(petId, username);

		Monitoring monitoring = validator.getMonitoringById(monitoringId);
		if (!monitoring.getPet().getId().equals(petId)) {		//모니터링의 펫 정보가 PathVariable의 펫 정보와 다르면 에러
			throw new MonitoringException(ErrorCode.INVALID_REQUEST);
		}
		monitoring.modify(mntWriteRequest);
		Monitoring modified = monitoringRepository.save(monitoring);
		return MntWriteResponse.from(modified);
	}

	@Transactional
	public MntDeleteResponse delete(Long petId, Long monitoringId, String username) {
		Pet pet = validatePetWithUsername(petId, username);

		Monitoring monitoring = validator.getMonitoringById(monitoringId);
		if (!monitoring.getPet().getId().equals(petId)) {		//모니터링의 펫 정보가 PathVariable의 펫 정보와 다르면 에러
			throw new MonitoringException(ErrorCode.INVALID_REQUEST);
		}
		monitoring.deleteSoftly();
		return MntDeleteResponse.builder()
				.message("모니터링 삭제 완료")
				.id(monitoring.getId()).build();
	}

	private Pet validatePetWithUsername(Long petId, String username) {
		Pet pet = validator.getPetById(petId);
		List<UserGroup> userGroupList = validator.getUserGroupListByUsername(pet.getGroup(),
				username);
		return pet;
	}
}
