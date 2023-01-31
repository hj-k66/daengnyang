package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntMakeRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntMakeResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Monitoring;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.UserGroup;
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
	public MntMakeResponse create(Long petId, MntMakeRequest mntMakeRequest, String username) {
		Pet pet = validator.getPetById(petId);        //pet과 로그인한 유저가 같은 그룹인지 확인
		List<UserGroup> userGroupList = validator.getUserGroupListByUsername(pet.getGroup(),
				username);
		Monitoring saved = monitoringRepository.save(mntMakeRequest.toEntity(pet));
		return MntMakeResponse.from(saved);
	}
}
