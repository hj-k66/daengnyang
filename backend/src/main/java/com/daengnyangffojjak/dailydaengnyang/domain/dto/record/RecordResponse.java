package com.daengnyangffojjak.dailydaengnyang.domain.dto.record;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
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
	private Boolean isPublic;
	private String tag;
	private List<RecordFile> recordFiles;
	private RecordFile recordFile;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime createdAt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime lastModifiedAt;

	public static RecordResponse of(Record record,
			List<RecordFile> recordFiles) {
		return RecordResponse.builder()
				.id(record.getId())
				.userId(record.getUser().getId())
				.petId(record.getPet().getId())
				.petName(record.getPet().getName())
				.title(record.getTitle())
				.body(record.getBody())
				.recordFiles(recordFiles)
				.userName(record.getUser().getUsername())
				.isPublic(record.getIsPublic())
				.tag(record.getTag().getName())
				.createdAt(record.getCreatedAt())
				.lastModifiedAt(record.getLastModifiedAt())
				.build();
	}

//	public static RecordResponse from(Record record) {
//		return RecordResponse.builder()
//				.title(record.getTitle())
//				.body(record.getBody())
//				.userName(record.getUser().getUsername())
//				.tag(record.getTag().getName())
//				.build();
//	}
}
