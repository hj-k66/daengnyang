package com.daengnyangffojjak.dailydaengnyang.repository;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Schedule;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

	Page<Schedule> findAllByPetId(Long petId, Pageable pageable); // petId 기준 일정 모아보기
	boolean existsByTagId(Long tagId);
	List<Schedule> findAllByCreatedAtBetweenAndPetId (Sort sort, LocalDateTime start, LocalDateTime end, Long petId);

}
