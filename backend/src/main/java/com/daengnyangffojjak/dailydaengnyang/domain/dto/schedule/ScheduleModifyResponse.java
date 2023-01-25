package com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ScheduleModifyResponse {
    private String message;
    private Long id;
}
