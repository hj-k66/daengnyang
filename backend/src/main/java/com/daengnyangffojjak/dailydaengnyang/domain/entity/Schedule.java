package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleModifyRequest;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Where(clause = "deleted_at is NULL")
public class Schedule extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pet_id")
	private Pet pet;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tag_id")
	private Tag tag;
	@Column(nullable = false)
	private String title;
	private String body;
	private Long assigneeId;        //책임자 user-id
	private String place;           //추후 지도 연동 시 좌표로 변경 가능
	private LocalDateTime dueDate;      //예정일
	private boolean isCompleted;    //일정 수행 여부

	//수정 된 Schedule 저장
	public void changeToSchedule(Pet pet, ScheduleModifyRequest scheduleModifyRequest, Tag tag) {
		this.pet = pet;
		this.tag = tag;
		this.title = scheduleModifyRequest.getTitle();
		this.body = scheduleModifyRequest.getBody();
		this.assigneeId = scheduleModifyRequest.getAssigneeId();
		this.place = scheduleModifyRequest.getPlace();
		this.dueDate = scheduleModifyRequest.getDueDate();
		this.isCompleted = scheduleModifyRequest.isCompleted();

	}

	//일정 책임자 변경
	public void changeToAssignee(Long assigneeId){
		this.assigneeId = assigneeId;
	}

}
