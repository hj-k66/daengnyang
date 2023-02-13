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
	private Long id;
	private LocalDate date;          //오늘 이전 날짜

	private Double weight;        //몸무게

	private Boolean vomit;        //구토
	private Boolean amPill;       //오전 투약
	private Boolean pmPill;       //오후 투약
	private Boolean customSymptom;           //custom symptom
	private String customSymptomName;        //custom symptom 이름

	private Integer feedToGram;        //식이량 (g)
	private Integer walkCnt;         //산책횟수 - 강아지
	private Integer playCnt;         //놀이횟수 - 고양이
	private Integer urination;        //배뇨 횟수
	private Integer defecation;       //배변 횟수
	private Integer respiratoryRate;  //호흡수
	private Integer customInt;        //custom 모니터링
	private String customIntName;

	private String notes;    //기타 특이사항

	public static MntGetResponse from(Monitoring monitoring) {
		return MntGetResponse.builder()
				.id(monitoring.getId())
				.date(monitoring.getDate())
				.weight(monitoring.getWeight())
				.vomit(monitoring.getVomit())
				.amPill(monitoring.getAmPill())
				.pmPill(monitoring.getPmPill())
				.customSymptom(monitoring.getCustomSymptom())
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
