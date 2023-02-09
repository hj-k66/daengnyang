package com.daengnyangffojjak.dailydaengnyang.domain.dto.record;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordFileResponse {

	private Long id;
	private List<String> uploadFileName;
	private List<String> S3StoredFileName;
	private String message;

	public static RecordFileResponse ofUpload(List<String> original, List<String> stored) {
		return RecordFileResponse.builder()
				.uploadFileName(original)
				.S3StoredFileName(stored)
				.message("파일 첨부 완료")
				.build();
	}

	public static RecordFileResponse ofDeleted(Long id, String message) {
		return RecordFileResponse.builder()
				.id(id)
				.message("파일 삭제 완료")
				.build();
	}
}
