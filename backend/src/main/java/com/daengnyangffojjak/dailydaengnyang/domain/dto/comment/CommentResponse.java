package com.daengnyangffojjak.dailydaengnyang.domain.dto.comment;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Comment;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CommentResponse {

	private Long id;
	private String userName;
	private String comment;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime createdAt;


	public static CommentResponse from(Comment comment) {
		return CommentResponse.builder()
				.id(comment.getId())
				.userName(comment.getUser().getUsername())
				.comment(comment.getComment())
				.createdAt(comment.getCreatedAt())
				.build();
	}
}
