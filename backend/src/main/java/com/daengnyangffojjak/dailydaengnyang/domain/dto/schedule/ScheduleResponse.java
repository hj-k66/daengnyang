package com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Schedule;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ScheduleResponse {

	private Long id;
	private Long userId;
	private Long petId;
	private String petName;
	private Category category;
	private String title;
	private String body;
	private Long assigneeId;
	private String place;
	private Boolean isCompleted;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime dueDate;

	@CreatedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime createdAt;

	@LastModifiedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime lastModifiedAt;

	public static ScheduleResponse toResponse(User user, Pet pet, Schedule schedule) {
		return ScheduleResponse.builder()
				.id(schedule.getId())
				.userId(user.getId())
				.petId(pet.getId())
				.petName(pet.getName())
				.category(schedule.getCategory())
				.title(schedule.getTitle())
				.body(schedule.getBody())
				.assigneeId(schedule.getAssigneeId())
				.isCompleted(schedule.getIsCompleted())
				.place(schedule.getPlace())
				.dueDate(schedule.getDueDate())
				.createdAt(schedule.getCreatedAt())
				.lastModifiedAt(schedule.getLastModifiedAt())
				.build();
	}
}
