package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntGetResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntMonthlyResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntWriteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntWriteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Monitoring;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.UserGroup;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.MonitoringException;
import com.daengnyangffojjak.dailydaengnyang.repository.MonitoringRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import java.time.LocalDate;
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
		Pet pet = validator.getPetWithUsername(petId, username);
		Monitoring saved = monitoringRepository.save(mntWriteRequest.toEntity(pet));
		return MntWriteResponse.from(saved);
	}
	@Transactional
	public MntMonthlyResponse getMonthly(Long petId, Integer year, Integer month, String username) {
		Pet pet = validator.getPetWithUsername(petId, username);
		LocalDate start = LocalDate.of(year, month, 1);
		LocalDate end = LocalDate.of(year, month, start.lengthOfMonth());

		List<Monitoring> monitorings = monitoringRepository.findAllByDateBetween(start, end);
		return MntMonthlyResponse.from(monitorings);
	}

	@Transactional    //일단 pet은 바꿀 수 없음
	public MntWriteResponse modify(Long petId, Long monitoringId, MntWriteRequest mntWriteRequest,
			String username) {
		Pet pet = validator.getPetWithUsername(petId, username);
		Monitoring monitoring = validateMonitoringWithPetId(monitoringId, petId);

		monitoring.modify(mntWriteRequest);
		Monitoring modified = monitoringRepository.saveAndFlush(monitoring);
		return MntWriteResponse.from(modified);
	}

	@Transactional
	public MntDeleteResponse delete(Long petId, Long monitoringId, String username) {
		Pet pet = validator.getPetWithUsername(petId, username);
		Monitoring monitoring = validateMonitoringWithPetId(monitoringId, petId);
		monitoring.deleteSoftly();
		return MntDeleteResponse.from(monitoring);
	}

	@Transactional
	public MntGetResponse getMonitoring(Long petId, Long monitoringId, String username) {
		Pet pet = validator.getPetWithUsername(petId, username);
		Monitoring monitoring = validateMonitoringWithPetId(monitoringId, petId);
		return MntGetResponse.from(monitoring);
	}

	//모니터링의 해당 반려동물의 것이 맞으면 Monitoring 반환
	private Monitoring validateMonitoringWithPetId(Long monitoringId, Long petId) {
		Monitoring monitoring = validator.getMonitoringById(monitoringId);
		if (!monitoring.getPet().getId()
				.equals(petId)) {        //모니터링의 펫 정보가 PathVariable의 펫 정보와 다르면 에러
			throw new MonitoringException(ErrorCode.INVALID_REQUEST);
		}
		return monitoring;
	}
}
