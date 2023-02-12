package com.daengnyangffojjak.dailydaengnyang.domain.dto.comment;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentModifyResponse {


	private Long id;
	private String userName;
	private String comment;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime lastModifiedAt;

	public static CommentModifyResponse from(Comment comment) {
		return CommentModifyResponse.builder()
				.id(comment.getId())
				.userName(comment.getUser().getUsername())
				.comment(comment.getComment())
				.lastModifiedAt(comment.getLastModifiedAt())
				.build();
	}
}
