package com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Monitoring;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MntGetResponse {
	private LocalDate date;          //오늘 이전 날짜

	private double weight;        //몸무게

	private boolean vomit;        //구토
	private boolean amPill;       //오전 투약
	private boolean pmPill;       //오후 투약
	private boolean customSymptom;           //custom symptom
	private String customSymptomName;        //custom symptom 이름

	private int feedToGram;        //식이량 (g)
	private int walkCnt;         //산책횟수 - 강아지
	private int playCnt;         //놀이횟수 - 고양이
	private int urination;        //배뇨 횟수
	private int defecation;       //배변 횟수
	private int respiratoryRate;  //호흡수
	private int customInt;        //custom 모니터링
	private String customIntName;

	private String notes;    //기타 특이사항

	public static MntGetResponse from(Monitoring monitoring) {
		return MntGetResponse.builder()
				.date(monitoring.getDate())
				.weight(monitoring.getWeight())
				.vomit(monitoring.isVomit())
				.amPill(monitoring.isAmPill())
				.pmPill(monitoring.isPmPill())
				.customSymptom(monitoring.isCustomSymptom())
				.customSymptomName(monitoring.getCustomSymptomName())
				.feedToGram(monitoring.getFeedToGram())
				.walkCnt(monitoring.getWalkCnt())
				.playCnt(monitoring.getPlayCnt())
				.urination(monitoring.getUrination())
				.defecation(monitoring.getDefecation())
				.respiratoryRate(monitoring.getRespiratoryRate())
				.customInt(monitoring.getCustomInt())
				.customIntName(monitoring.getCustomIntName())
				.notes(monitoring.getNotes())
				.build();
	}
}
