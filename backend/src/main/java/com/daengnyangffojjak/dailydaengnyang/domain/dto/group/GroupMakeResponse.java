package com.daengnyangffojjak.dailydaengnyang.domain.dto.group;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupMakeResponse {
    private Long id;
    private String name;
    private Long ownerId;
    private String ownerUserName;

    public static GroupMakeResponse from(Group group){
        return GroupMakeResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .ownerId(group.getUser().getId())
                .ownerUserName(group.getUser().getUsername())
                .build();
    }
}
