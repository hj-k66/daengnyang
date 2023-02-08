package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntGetResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntMonthlyResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntReportResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntWriteRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntWriteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Monitoring;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.MonitoringException;
import com.daengnyangffojjak.dailydaengnyang.repository.MonitoringRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
		if (monitoringRepository.existsByDate(mntWriteRequest.getDate())) {
			throw new MonitoringException(ErrorCode.INVALID_REQUEST, "해당 날짜의 모니터링이 이미 존재합니다.");
		}
		Monitoring saved = monitoringRepository.save(mntWriteRequest.toEntity(pet));
		return MntWriteResponse.from(saved);
	}
	@Transactional(readOnly = true)
	public MntMonthlyResponse getMonitoringList(Long petId, String fromDate, String toDate, String username) {
		Pet pet = validator.getPetWithUsername(petId, username);
		List<Monitoring> monitorings = getMonitoringListFromDate(fromDate, toDate);

		return MntMonthlyResponse.from(monitorings);
	}

	@Transactional(readOnly = true)
	public MntReportResponse getReport(Long petId, String fromDate, String toDate, String username) {
		Pet pet = validator.getPetWithUsername(petId, username);
		List<Monitoring> monitorings = getMonitoringListFromDate(fromDate, toDate);

		return makeReport(monitorings);
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

	@Transactional(readOnly = true)
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

	// 20220101 -> LocalDate.of(2022, 1, 1)
	private LocalDate getLocalDateFromString (String date) {
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(4, 6));
		int day = Integer.parseInt(date.substring(6, 8));

		return LocalDate.of(year, month, day);
	}

	private List<Monitoring> getMonitoringListFromDate (String fromDate, String toDate) {
		LocalDate start = getLocalDateFromString(fromDate);
		LocalDate end = getLocalDateFromString(toDate);

		long days = ChronoUnit.DAYS.between(start, end);
		if (days < 7 || days > 93) {
			throw new MonitoringException(ErrorCode.INVALID_REQUEST, "레포트 작성은 7일 이상, 3달 이하의 기간만 가능합니다.");
		}
		return monitoringRepository.findAllByDateBetween(start, end);
	}

	// ^^;;; 좋은 방법이 있으면 알려주세요.
	private MntReportResponse makeReport (List<Monitoring> monitorings) {
		double weightSum = 0;
		int weightCount = 0;
		int vomitTrue = 0;
		int vomitCount = 0;
		int amPillTrue = 0;
		int amPillCount = 0;
		int pmPillTrue = 0;
		int pmPillCount = 0;
		int customSymptomTrue = 0;
		int customSymptomCount = 0;
		String customSymptomName = "";

		int feedToGramSum = 0;
		int feedToGramCount = 0;
		int walkCntSum = 0;
		int walkCntCount = 0;
		int playCntSum = 0;
		int playCntCount = 0;
		int urinationSum = 0;
		int urinationCount = 0;
		int defecationSum = 0;
		int defecationCount = 0;
		int respiratoryRateSum = 0;
		int respiratoryRateCount = 0;
		int customIntSum = 0;
		int customIntCount = 0;
		String customIntName = "";
		for (Monitoring monitoring : monitorings) {
			Double weight = monitoring.getWeight();
			if (weight != null) {
				weightSum += weight;
				weightCount += 1;
			}
			Boolean vomit = monitoring.getVomit();
			if (vomit != null) {
				vomitCount += 1;
				if (vomit) {
					vomitTrue += 1;
				}
			}
			Boolean amPill = monitoring.getAmPill();
			if (amPill != null) {
				amPillCount += 1;
				if (amPill) {
					amPillTrue += 1;
				}
			}
			Boolean pmPill = monitoring.getPmPill();
			if (pmPill != null) {
				pmPillCount += 1;
				if (pmPill) {
					pmPillTrue += 1;
				}
			}
			Boolean customSymptom = monitoring.getCustomSymptom();
			if (customSymptom != null && monitoring.getCustomSymptomName() != null) {
				if(!monitoring.getCustomSymptomName().equals(customSymptomName)) {
					customSymptomName = monitoring.getCustomSymptomName();
					customSymptomCount = 0;
					customSymptomTrue = 0;
				}
				customSymptomCount += 1;
				if (customSymptom) {
					customSymptomTrue += 1;
				}
			}

			Integer feedToGram = monitoring.getFeedToGram();
			if (feedToGram != null) {
				feedToGramCount += 1;
				feedToGramSum += feedToGram;
			}

			Integer walkCnt = monitoring.getWalkCnt();
			if (walkCnt != null) {
				walkCntCount += 1;
				walkCntSum += walkCnt;
			}
			Integer playCnt = monitoring.getPlayCnt();
			if (playCnt != null) {
				playCntCount += 1;
				playCntSum += playCnt;
			}
			Integer urination = monitoring.getUrination();
			if (urination != null) {
				urinationCount += 1;
				urinationSum += urination;
			}
			Integer defecation = monitoring.getDefecation();
			if (defecation != null) {
				defecationCount += 1;
				defecationSum += defecation;
			}
			Integer respiratoryRate = monitoring.getRespiratoryRate();
			if (respiratoryRate != null) {
				respiratoryRateCount += 1;
				respiratoryRateSum += respiratoryRate;
			}
			Integer customInt = monitoring.getCustomInt();
			if (customInt != null && monitoring.getCustomIntName() != null) {
				if(!monitoring.getCustomIntName().equals(customIntName)) {
					customIntName = monitoring.getCustomIntName();
					customIntCount = 0;
					customIntSum = 0;
				}
				customIntCount += 1;
				customIntSum += customInt;
			}
		}
		double weightAvg = 0.0;
		if (weightCount != 0) {weightAvg = weightSum/weightCount;}
		double feedToGramAvg = 0.0;
		if (feedToGramCount != 0) {feedToGramAvg = (double)feedToGramSum/feedToGramCount;}
		double walkCntAvg = 0.0;
		if (walkCntCount != 0) {walkCntAvg = (double)walkCntSum/walkCntCount;}
		double playCntAvg = 0.0;
		if (playCntCount != 0) {playCntAvg = (double)playCntSum/playCntCount;}
		double urinationAvg = 0.0;
		if (urinationCount != 0) {urinationAvg = (double)urinationSum/urinationCount;}
		double defecationAvg = 0.0;
		if (defecationCount != 0) {defecationAvg = (double)defecationSum/defecationCount;}
		double respiratoryRateAvg = 0.0;
		if (respiratoryRateCount != 0) {respiratoryRateAvg = (double)respiratoryRateSum/respiratoryRateCount;}
		double customIntAvg = 0.0;
		if (customIntCount != 0) {customIntAvg = (double)customIntSum/customIntCount;}

		return MntReportResponse.builder().weightAvg(weightAvg).weightCount(weightCount)
				.vomitTrue(vomitTrue).vomitCount(vomitCount)
				.amPillTrue(amPillTrue)
				.amPillCount(amPillCount)
				.pmPillTrue(pmPillTrue)
				.pmPillCount(pmPillCount)
				.customSymptomTrue(customSymptomTrue)
				.customSymptomCount(customSymptomCount)
				.customSymptomName(customSymptomName)
				.feedToGramAvg(feedToGramAvg)
				.feedToGramCount(feedToGramCount)
				.walkCntAvg(walkCntAvg)
				.walkCntCount(walkCntCount)
				.playCntAvg(playCntAvg)
				.playCntCount(playCntCount)
				.urinationAvg(urinationAvg)
				.urinationCount(urinationCount)
				.defecationAvg(defecationAvg)
				.defecationCount(defecationCount)
				.respiratoryRateAvg(respiratoryRateAvg)
				.respiratoryRateCount(respiratoryRateCount)
				.customIntAvg(customIntAvg)
				.customIntCount(customIntCount)
				.customIntName(customIntName)
				.build();
	}

}
