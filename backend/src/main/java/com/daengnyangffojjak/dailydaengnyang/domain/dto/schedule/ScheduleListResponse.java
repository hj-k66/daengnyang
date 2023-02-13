package com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Schedule;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Map;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ScheduleListResponse {

	private String tag;
	private String title;
	private String body;
	private Long assigneeId;
	private String roleInGroup;
	private String place;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime dueDate;

	private boolean isCompleted;

	public static Page<ScheduleListResponse> toResponse(Page<Schedule> schedules, Map<Long, String> getRoleInGroup) {
		Page<ScheduleListResponse> scheduleListResponses = schedules.map(
				schedule -> ScheduleListResponse.builder()
						.tag(schedule.getTag().getName())
						.title(schedule.getTitle())
						.body(schedule.getBody())
						.assigneeId(schedule.getAssigneeId())
						.roleInGroup(getRoleInGroup.get(schedule.getAssigneeId())) //assigneeId == userId -> userId의 roleInGroup 반환
						.place(schedule.getPlace())
						.dueDate(schedule.getDueDate())
						.isCompleted(schedule.isCompleted())
						.build());

		return scheduleListResponses;
	}
}
