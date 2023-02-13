package com.daengnyangffojjak.dailydaengnyang.repository;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.NotificationUser;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationUserRepository extends JpaRepository<NotificationUser, Long> {
	List<NotificationUser> findAllByUserAndCreatedAtBetween(User user, LocalDateTime before30days, LocalDateTime now);
	Optional<NotificationUser> findByNotificationIdAndUserId(Long notificationId, Long userId);

}
