package com.daengnyangffojjak.dailydaengnyang.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UtilControllerTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void Profile확인 () {
		//when
		String profile = this.restTemplate.getForObject("/utils/profile", String.class);

		//then
		assertThat(profile).isEqualTo("local");
	}
}