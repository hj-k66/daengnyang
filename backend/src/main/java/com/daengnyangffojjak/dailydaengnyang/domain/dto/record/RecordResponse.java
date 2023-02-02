package com.daengnyangffojjak.dailydaengnyang.domain.dto.record;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RecordResponse {

	private Long id;
	private Long userId;
	private Long petId;
	private String title;
	private String body;
	private String userName;
//	private boolean isPublic;
	private Category category;
	@CreatedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime createdAt;
	@LastModifiedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime lastModifiedAt;

	public static RecordResponse of(User user, Pet pet, Record record) {
		return RecordResponse.builder()
				.id(record.getId())
				.userId(user.getId())
				.petId(pet.getId())
				.title(record.getTitle())
				.body(record.getBody())
				.userName(record.getUser().getUsername())
				.category(record.getCategory())
				.createdAt(record.getCreatedAt())
				.lastModifiedAt(record.getLastModifiedAt())
				.build();
	}

	public static RecordResponse from(Record record) {
		return RecordResponse.builder()
				.id(record.getId())
				.title(record.getTitle())
				.body(record.getBody())
				.userName(record.getUser().getUsername())
				.category(record.getCategory())
				.build();
	}
}
