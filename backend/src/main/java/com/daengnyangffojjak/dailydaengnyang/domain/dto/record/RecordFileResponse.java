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

	private List<String> uploadFileName;
	private List<String> S3StoredFileName;
	private String message;

	public static RecordFileResponse of(List<String> original, List<String> stored) {
		return RecordFileResponse.builder()
				.uploadFileName(original)
				.S3StoredFileName(stored)
				.message("파일 첨부 완료")
				.build();
	}
}
