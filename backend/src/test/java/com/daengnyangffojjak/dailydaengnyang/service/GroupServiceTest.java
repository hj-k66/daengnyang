package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupMakeResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.group.GroupUserListResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserRole;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.UserGroup;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.UserException;
import com.daengnyangffojjak.dailydaengnyang.repository.GroupRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.UserGroupRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class GroupServiceTest {
    private GroupService groupService;
    private final GroupRepository groupRepository = mock(GroupRepository.class);
    private final UserGroupRepository userGroupRepository = mock(UserGroupRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    void setUp(){
        groupService = new GroupService(groupRepository, userGroupRepository, userRepository);
    }
    User user = User.builder().id(1L).userName("user").password("password").email("@.").role(UserRole.ROLE_USER).build();
    Group group = Group.builder().id(1L).name("그룹이름").user(user).build();
    UserGroup userGroup = UserGroup.builder().id(1L).user(user).group(group).roleInGroup("엄마").isOwner(true).build();


    @Nested
    @DisplayName("그룹 만들기")
    class CreateGroup{
        GroupMakeRequest request = new GroupMakeRequest("그룹이름", "엄마");
        @Test
        @DisplayName("성공")
        void success(){
            given(userRepository.findByUserName("user")).willReturn(Optional.of(user));
            given(groupRepository.save(request.toEntity(user))).willReturn(group);
            given(userGroupRepository.save(UserGroup.from(user, group, request.getRoleInGroup(), true)))
                    .willReturn(userGroup);

            GroupMakeResponse response = assertDoesNotThrow(() -> groupService.create(request, "user"));

            assertEquals(1L, response.getId());
            assertEquals("그룹이름", response.getName());
            assertEquals(1L, response.getOwnerId());
            assertEquals("user", response.getOwnerUserName());
        }
    }
    @Nested
    @DisplayName("그룹 내 유저 조회")
    class GetGroupUser{
        @Test
        @DisplayName("성공")
        void success(){
            List<UserGroup> userGroupList = List.of(
                new UserGroup(1L, User.builder().userName("user").build(), group, "mom", true),
                new UserGroup(1L, User.builder().userName("user1").build(), group, "mom", false)
            );
            given(userRepository.findByUserName("user")).willReturn(Optional.of(user));
            given(groupRepository.findById(1L)).willReturn(Optional.of(group));
            given(userGroupRepository.findAllByGroup(group)).willReturn(userGroupList);

            GroupUserListResponse response = assertDoesNotThrow(() -> groupService.getGroupUsers(1L, "user"));

            assertEquals(2, response.getCount());
            assertEquals(2, response.getUsers().size());
        }
        @Test
        @DisplayName("그룹 내 유저가 아닌 경우")
        void fail_그룹내유저아님(){
            List<UserGroup> userGroupList = List.of(
                new UserGroup(1L, User.builder().userName("user2").build(), group, "mom", true),
                new UserGroup(1L, User.builder().userName("user1").build(), group, "mom", false)
            );
            given(userRepository.findByUserName("user")).willReturn(Optional.of(user));
            given(groupRepository.findById(1L)).willReturn(Optional.of(group));
            given(userGroupRepository.findAllByGroup(group)).willReturn(userGroupList);

            UserException e = assertThrows(UserException.class,
                    () -> groupService.getGroupUsers(1L, "user"));

            assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
        }
    }

}