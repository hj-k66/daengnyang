package com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Schedule;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.*;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ScheduleCreateRequest {

	private Long tagId;
	private String title;
	private String body;
	private Long assigneeId;
	private String place;
	private LocalDate dueDate;

	public Schedule toEntity(Pet pet, User user, Tag tag) {
		return Schedule.builder()
				.user(user)
				.pet(pet)
				.tag(tag)
				.title(title)
				.body(body)
				.assigneeId(assigneeId)
				.place(place)
				.dueDate(dueDate)
				.build();

	}

}
