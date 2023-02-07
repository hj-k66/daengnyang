package com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ScheduleModifyRequest {

	private Long tagId;
	private String title;
	private String body;
	private Long assigneeId;
	private String place;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime dueDate;

	private boolean completed;

}