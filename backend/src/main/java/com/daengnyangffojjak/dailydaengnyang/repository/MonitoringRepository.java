package com.daengnyangffojjak.dailydaengnyang.repository;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Monitoring;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitoringRepository extends JpaRepository<Monitoring, Long> {
	List<Monitoring> findAllByDateBetweenAndPetId(Sort sort, LocalDate start, LocalDate end, Long petId);

	boolean existsByDateAndPetId(LocalDate date, Long petId);
}
