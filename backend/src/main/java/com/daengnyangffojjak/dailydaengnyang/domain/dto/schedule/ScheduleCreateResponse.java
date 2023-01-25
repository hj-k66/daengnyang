package com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ScheduleCreateResponse {
    private String message;
    private Long id;

}
