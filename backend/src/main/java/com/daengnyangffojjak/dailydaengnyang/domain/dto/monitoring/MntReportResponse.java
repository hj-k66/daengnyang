package com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MntReportResponse {
	private double weightAvg;        	//몸무게
	private int weightCount;

	private int vomitTrue;          //구토
	private int vomitCount;
	private int amPillTrue;
	private int amPillCount;       //오전 투약
	private int pmPillTrue;
	private int pmPillCount;       //오후 투약
	private int customSymptomTrue;
	private int customSymptomCount;      //custom symptom
	private String customSymptomName;        //custom symptom 이름

	private double feedToGramAvg;      //식이량 (g)
	private int feedToGramCount;
	private double walkCntAvg;         //산책횟수 - 강아지
	private int walkCntCount;
	private double playCntAvg;         //놀이횟수 - 고양이
	private int playCntCount;
	private double urinationAvg;        //배뇨 횟수
	private int urinationCount;
	private double defecationAvg;       //배변 횟수
	private int defecationCount;
	private double respiratoryRateAvg;  //호흡수
	private int respiratoryRateCount;
	private double customIntAvg;        //custom 모니터링
	private int customIntCount;
	private String customIntName;
}


