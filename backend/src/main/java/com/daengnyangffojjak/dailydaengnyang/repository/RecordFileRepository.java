package com.daengnyangffojjak.dailydaengnyang.repository;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.RecordFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordFileRepository extends JpaRepository<RecordFile, Long> {

	List<RecordFile> findByRecord_Id(Long id);
}
