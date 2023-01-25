package com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Category;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ScheduleCreateRequest {
    private Tag tagId;
    private Category category;
    private String title;
    private String body;
    private Long assigneeId;
    private String place;
    private boolean isCompleted;
    private LocalDateTime dueDate;

}
