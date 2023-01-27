package com.daengnyangffojjak.dailydaengnyang.repository;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
