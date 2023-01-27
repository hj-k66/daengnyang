package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleModifyRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Category;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")   //작성자
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;
    @Enumerated(EnumType.STRING)
    private Category category;
    @Column(nullable = false)
    private String title;
    private String body;
    private Long assigneeId;        //책임자 user-id
    private String place;           //추후 지도 연동 시 좌표로 변경 가능
    private Boolean isCompleted;    //일정 수행 여부
    private LocalDateTime dueDate;      //예정일


    public void changeToSchedule(ScheduleModifyRequest scheduleModifyRequest) {
        this.category = scheduleModifyRequest.getCategory();
        this.title = scheduleModifyRequest.getTitle();
        this.body = scheduleModifyRequest.getBody();
        this.assigneeId = scheduleModifyRequest.getAssigneeId();
        this.place = scheduleModifyRequest.getPlace();
        this.dueDate = scheduleModifyRequest.getDueDate();

    }

}
