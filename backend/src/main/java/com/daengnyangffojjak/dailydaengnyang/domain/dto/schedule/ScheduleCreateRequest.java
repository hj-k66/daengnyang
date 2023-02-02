package com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Schedule;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ScheduleCreateRequest {

	private Category category;
	private String title;
	private String body;
	private Long assigneeId;
	private String place;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime dueDate;

	public Schedule toEntity(Pet pet, User user) {
		return Schedule.builder()
				.user(user)
				.pet(pet)
				.category(category)
				.title(title)
				.body(body)
				.assigneeId(assigneeId)
				.place(place)
				.dueDate(dueDate)
				.build();

	}

}
