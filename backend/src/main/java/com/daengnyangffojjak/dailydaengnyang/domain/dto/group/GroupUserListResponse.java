package com.daengnyangffojjak.dailydaengnyang.domain.dto.group;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.UserGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class GroupUserListResponse {
    private List<GroupUserResponse> users;
    private int count;

    public static GroupUserListResponse from(List<UserGroup> userGroupList) {
        List<GroupUserResponse> users = userGroupList.stream().map(GroupUserResponse::from)
                .collect(Collectors.toList());
        return new GroupUserListResponse(users, users.size());
    }
}
