package com.daengnyangffojjak.dailydaengnyang.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

//@DataJpaTest
class RecordRepositoryTest {
//	@Autowired
//	private RecordRepository recordRepository;
//	@Test
//	@DisplayName("태그 아이디로 존재 여부 조회")
//	@Transactional
//	void exists_by_tag_id(){
//		Pet pet = Pet.builder().id(1L).name("hoon").build();
//		Pet pet2 = Pet.builder().id(2L).name("흰둥이").build();
//		User user = User.builder().id(1L).build();
//		Tag tag = Tag.builder().id(1L).name("여행").build();
//		Tag tag2 = Tag.builder().id(2L).name("산책").build();
//
//		recordRepository.saveAndFlush(Record.builder().id(1L).pet(pet).user(user).tag(tag).title("제목").isPublic(true).build());
//		recordRepository.saveAndFlush(Record.builder().id(1L).pet(pet2).user(user).tag(tag2).title("제목").isPublic(false).build());
//
//		boolean result1 = recordRepository.existsRecordByTagId(1L);
//		boolean result2 = recordRepository.existsRecordByTagId(2L);
//		boolean result3 = recordRepository.existsRecordByTagId(3L);
//
//		assertEquals(true, result1);
//		assertEquals(true, result2);
//		assertEquals(false, result3);
//	}

}