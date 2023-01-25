package com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ScheduleModifyRequest {
    private String title;
    private String body;
    private Long assigneeId;
    private String place;
    private boolean isCompleted;
    private LocalDateTime dueDate;
}