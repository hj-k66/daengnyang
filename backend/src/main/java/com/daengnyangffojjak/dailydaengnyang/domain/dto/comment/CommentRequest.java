package com.daengnyangffojjak.dailydaengnyang.domain.dto.comment;

import com.daengnyangffojjak.dailydaengnyang.domain.entity.Comment;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CommentRequest {
	private String comment;

	public Comment toEntity(User user, Record record) {
		return Comment.builder()
				.user(user)
				.record(record)
				.comment(comment)
				.build();
	}
}
