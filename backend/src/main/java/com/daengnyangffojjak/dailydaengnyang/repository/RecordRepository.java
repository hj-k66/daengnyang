package com.daengnyangffojjak.dailydaengnyang.repository;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {

	Page<Record> findAllByIsPublicTrue(Pageable pageable);

	boolean existsByTagId(Long tagId);
}

