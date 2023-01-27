package com.daengnyangffojjak.dailydaengnyang.domain.dto.group;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class GroupMakeRequest {
    private String name;    //그룹이름
    private String roleInGroup;    //만든 사람의 그룹에서 이름

    public Group toEntity(User user){
        return Group.builder()
                .user(user)
                .name(this.name)
                .build();
    }
}
