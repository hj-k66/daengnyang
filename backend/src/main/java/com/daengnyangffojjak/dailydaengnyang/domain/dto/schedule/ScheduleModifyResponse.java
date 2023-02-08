package com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Schedule;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ScheduleModifyResponse {

	private Long id;
	private String title;

	@LastModifiedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime lastModifiedAt;

	public static ScheduleModifyResponse toResponse(Schedule modifySchedule) {
		return ScheduleModifyResponse.builder()
				.id(modifySchedule.getId())
				.title(modifySchedule.getTitle())
				.lastModifiedAt(modifySchedule.getLastModifiedAt())
				.build();
	}
}
