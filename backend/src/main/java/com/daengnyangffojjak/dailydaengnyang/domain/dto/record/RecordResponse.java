package com.daengnyangffojjak.dailydaengnyang.domain.dto.record;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.RecordFile;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RecordResponse {

	private Long id;
	private Long userId;
	private Long petId;
	private String petName;
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

	public static RecordResponse of(Record record, List<RecordFile> recordFiles) {
		return RecordResponse.builder()
				.id(record.getId())
				.userId(record.getUser().getId())
				.petId(record.getPet().getId())
				.petName(record.getPet().getName())
				.title(record.getTitle())
				.body(record.getBody())
				.recordFiles(recordFiles)
				.recordFile(selectThumbNail(recordFiles))
				.userName(record.getUser().getUsername())
				.isPublic(record.getIsPublic())
				.tag(record.getTag().getName())
				.createdAt(record.getCreatedAt())
				.lastModifiedAt(record.getLastModifiedAt())
				.build();
	}
	public static RecordFile selectThumbNail (List<RecordFile> recordFiles) {
		RecordFile recordFile = null;
		if (!recordFiles.isEmpty()) {
			recordFile = recordFiles.get(0);
			int idx = recordFile.getStoredFileUrl().lastIndexOf(".");
			String extension = recordFile.getStoredFileUrl().substring(idx + 1);
			if (extension.equalsIgnoreCase("mp4")) {
				recordFile = null;
			}
		}
		if (recordFile == null) {
			recordFile = new RecordFile();
			recordFile.changeToDefaultImage("https://daengnyang-bucket.s3.ap-northeast-2.amazonaws.com/defaultImage.png");
		}
		return recordFile;
	}
}
