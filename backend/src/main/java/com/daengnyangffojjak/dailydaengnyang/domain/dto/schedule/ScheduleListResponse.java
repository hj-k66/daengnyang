package com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Schedule;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ScheduleListResponse {

	private Category category;
	private String title;
	private String body;
	private Long assigneeId;
	private String place;
	private Boolean isCompleted;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime dueDate;

	public static Page<ScheduleListResponse> toResponse(Page<Schedule> schedules) {
		Page<ScheduleListResponse> scheduleListResponses = schedules.map(
				schedule -> ScheduleListResponse.builder()
						.category(schedule.getCategory())
						.title(schedule.getTitle())
						.body(schedule.getBody())
						.assigneeId(schedule.getAssigneeId())
						.place(schedule.getPlace())
						.isCompleted(schedule.isCompleted())
						.dueDate(schedule.getDueDate())
						.build());

		return scheduleListResponses;
	}
}
