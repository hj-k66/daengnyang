package com.daengnyangffojjak.dailydaengnyang.repository;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PetRepositoryTest {
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private GroupRepository groupRepository;

    @Test
    @DisplayName("그룹 id로 조회")
    @Transactional
    void get_by_groupId(){
        Group group = Group.builder().name("그룹이름").build();
        Group saved = groupRepository.save(group);

        petRepository.save(Pet.builder().name("hoon").group(saved).build());
        petRepository.save(Pet.builder().name("hoon2").group(saved).build());
        petRepository.save(Pet.builder().name("hoone").group(saved).build());

        List<Pet> pets = petRepository.findAllByGroupId(saved.getId());

        assertEquals(3, pets.size());
    }
}