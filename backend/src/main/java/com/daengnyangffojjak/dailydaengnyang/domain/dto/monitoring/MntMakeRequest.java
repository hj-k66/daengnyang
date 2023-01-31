package com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Monitoring;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MntMakeRequest {

	@NotNull(message = "날짜를 입력하세요.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	@PastOrPresent
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

	public Monitoring toEntity(Pet pet) {
		return Monitoring.builder()
				.pet(pet)
				.date(this.date)
				.weight(this.weight)
				.vomit(this.vomit)
				.amPill(this.amPill)       //오전 투약
				.pmPill(this.pmPill)       //오후 투약
				.customSymptom(this.customSymptom)           //custom symptom
				.customSymptomName(this.customSymptomName)        //custom symptom 이름
				.feedToGram(this.feedToGram)        //식이량 (g)
				.walkCnt(this.walkCnt)         //산책횟수 - 강아지
				.playCnt(this.playCnt)         //놀이횟수 - 고양이
				.urination(this.urination)        //배뇨 횟수
				.defecation(this.defecation)       //배변 횟수
				.respiratoryRate(this.respiratoryRate)  //호흡수
				.customInt(this.customInt)        //custom 모니터링
				.customIntName(this.customIntName)
				.notes(this.notes)    //기타 특이사항
				.build();
	}
}
