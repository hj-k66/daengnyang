package com.daengnyangffojjak.dailydaengnyang.fixture;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.BaseEntity;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;

public class GroupFixture {
	public static Group get (){
		Group group = Group.builder().id(1L).name("그룹이름").user(UserFixture.get()).build();

		ReflectionTestUtils.setField(
				group,
				BaseEntity.class,
				"createdAt",
				LocalDateTime.of(2022, 12, 12, 12, 12, 12),
				LocalDateTime.class
		);
		ReflectionTestUtils.setField(
				group,
				BaseEntity.class,
				"lastModifiedAt",
				LocalDateTime.of(2022, 12, 12, 12, 12, 12),
				LocalDateTime.class
		);
		return group;
	}
}
