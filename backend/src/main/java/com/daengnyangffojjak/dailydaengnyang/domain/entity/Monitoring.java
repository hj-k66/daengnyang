package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.monitoring.MntWriteRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Monitoring extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pet_id")
	private Pet pet;
	@Column(nullable = false)
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


	public void modify(MntWriteRequest mntWriteRequest) {
		this.date = mntWriteRequest.getDate();

		this.weight = mntWriteRequest.getWeight();

		this.vomit = mntWriteRequest.getVomit();
		this.amPill = mntWriteRequest.getAmPill();
		this.pmPill = mntWriteRequest.getPmPill();
		this.customSymptom = mntWriteRequest.getCustomSymptom();
		this.customSymptomName = mntWriteRequest.getCustomSymptomName();

		this.feedToGram = mntWriteRequest.getFeedToGram();
		this.walkCnt = mntWriteRequest.getWalkCnt();
		this.playCnt = mntWriteRequest.getPlayCnt();
		this.urination = mntWriteRequest.getUrination();
		this.defecation = mntWriteRequest.getDefecation();
		this.respiratoryRate = mntWriteRequest.getRespiratoryRate();
		this.customInt = mntWriteRequest.getCustomInt();
		this.customIntName = mntWriteRequest.getCustomIntName();

		this.notes = mntWriteRequest.getNotes();
	}
}
