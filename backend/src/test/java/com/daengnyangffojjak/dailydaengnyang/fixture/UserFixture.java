package com.daengnyangffojjak.dailydaengnyang.fixture;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserRole;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.BaseEntity;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;

public class UserFixture {
	public static User get (){
		User user = User.builder().id(1L).userName("user").password("password").email("@.")
				.role(UserRole.ROLE_USER).build();
		ReflectionTestUtils.setField(
				user,
				BaseEntity.class,
				"createdAt",
				LocalDateTime.of(2022, 12, 12, 12, 12, 12),
				LocalDateTime.class
		);
		ReflectionTestUtils.setField(
				user,
				BaseEntity.class,
				"lastModifiedAt",
				LocalDateTime.of(2022, 12, 12, 12, 12, 12),
				LocalDateTime.class
		);
		return user;
	}
}
