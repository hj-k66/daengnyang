package com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Schedule;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ScheduleResponse {

	private Long id;
	private String tag;
	private Long userId;
	private String userName;
	private Long petId;
	private String petName;
	private String title;
	private String body;
	private Long assigneeId;
	private String roleInGroup;
	private String place;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime dueDate;
	private boolean isCompleted;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime createdAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime lastModifiedAt;

	public static ScheduleResponse toResponse(User user, Pet pet, Schedule schedule, String roleInGroup) {
		return ScheduleResponse.builder()
				.id(schedule.getId())
				.tag(schedule.getTag().getName())
				.userId(user.getId())
				.userName(user.getUsername())
				.petId(pet.getId())
				.petName(pet.getName())
				.title(schedule.getTitle())
				.body(schedule.getBody())
				.assigneeId(schedule.getAssigneeId())
				.roleInGroup(roleInGroup)
				.place(schedule.getPlace())
				.dueDate(schedule.getDueDate())
				.isCompleted(schedule.isCompleted())
				.createdAt(schedule.getCreatedAt())
				.lastModifiedAt(schedule.getLastModifiedAt())
				.build();
	}
}
