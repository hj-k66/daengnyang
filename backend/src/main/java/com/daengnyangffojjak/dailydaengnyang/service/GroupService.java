package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupUserListResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.UserGroup;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.GroupException;
import com.daengnyangffojjak.dailydaengnyang.repository.GroupRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserService userService;

    @Transactional
    public GroupMakeResponse create(GroupMakeRequest groupMakeRequest, String username) {
        User user = userService.getUserByUserName(username);                            //유저 확인

        Group savedGroup = groupRepository.save(groupMakeRequest.toEntity(user));        //그룹 저장
        UserGroup savedUserGroup = userGroupRepository.save(
                UserGroup.from(user, savedGroup, groupMakeRequest.getRoleInGroup(), true));   //그룹 멤버로 저장

        return GroupMakeResponse.from(savedGroup);
    }
    @Transactional
    public GroupUserListResponse getGroupUsers(Long groupId, String username) {
        //그룹이 존재하는지 확인
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new GroupException(ErrorCode.GROUP_NOT_FOUND));
        //그룹으로 그룹 내 멤버 불러오기
        List<UserGroup> userGroupList = userGroupRepository.findAllByGroup(group);
        return GroupUserListResponse.from(userGroupList);
    }
}
