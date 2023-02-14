package com.daengnyangffojjak.dailydaengnyang.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.comment.CommentDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.comment.CommentModifyResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.comment.CommentRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.comment.CommentResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserRole;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Comment;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Group;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Tag;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Sex;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import com.daengnyangffojjak.dailydaengnyang.repository.CommentRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

class CommentServiceTest {

	private final CommentRepository commentRepository = mock(CommentRepository.class);
	private final Validator validator = mock(Validator.class);

	User user = User.builder().id(1L).userName("user").password("password").email("@.")
			.role(UserRole.ROLE_USER).build();
	Group group = new Group(1L, user, "group");
	Pet pet = Pet.builder().id(1L).birthday(LocalDate.of(2023, 2, 3)).species(Species.CAT)
			.name("pet").group(group).sex(Sex.NEUTERED_MALE).build();
	Tag tag = new Tag(1L, group, "질병");
	Record record = new Record(1L, user, pet, tag, "제목", "본문", true);

	private CommentService commentService = new CommentService(commentRepository, validator);

	CommentRequest commentRequest = new CommentRequest("comment");

	Comment comment = Comment.builder()
			.id(1L)
			.user(user)
			.record(record)
			.comment("comment")
			.build();

	Comment comment2 = Comment.builder()
			.id(2L)
			.user(user)
			.record(record)
			.comment("comment2")
			.build();

	@Nested
	@DisplayName("댓글 등록")
	class CommentCreate {

		@Test
		@DisplayName("성공")
		void success() {

			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getRecordById(1L)).willReturn(record);

			given(commentRepository.save(
					commentRequest.toEntity(user, record))).willReturn(comment);

			CommentResponse commentResponse = assertDoesNotThrow(
					() -> commentService.createComment(1L, commentRequest, "user")
			);

			assertEquals(1L, commentResponse.getId());
			assertEquals("user", commentResponse.getUserName());
			assertEquals("comment", commentResponse.getComment());
			assertEquals(null, commentResponse.getCreatedAt());
		}
	}

	@Nested
	@DisplayName("댓글 수정")
	class CommentModify {

		CommentRequest commentRequest = new CommentRequest("modify comment");
		Comment modifyComment = Comment.builder()
				.id(1L)
				.user(user)
				.record(record)
				.comment("modify comment")
				.build();

		@Test
		@DisplayName("성공")
		void success() {
			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getRecordById(1L)).willReturn(record);
			given(validator.getCommentById(1L)).willReturn(comment);
			given(commentRepository.saveAndFlush(comment)).willReturn(modifyComment);

			CommentModifyResponse commentModifyResponse = assertDoesNotThrow(
					() -> commentService.modifyComment(1L, 1L, commentRequest, "user")
			);

			assertEquals(1L, commentModifyResponse.getId());
			assertEquals("user", commentModifyResponse.getUserName());
			assertEquals("modify comment", commentModifyResponse.getComment());
		}
	}

	@Nested
	@DisplayName("댓글 삭제")
	class CommentDelete {

		@Test
		@DisplayName("성공")
		void success() {

			given(validator.getUserByUserName("user")).willReturn(user);
			given(validator.getRecordById(1L)).willReturn(record);
			given(validator.getCommentById(1L)).willReturn(comment);

			CommentDeleteResponse commentDeleteResponse = assertDoesNotThrow(
					() -> commentService.deleteComment(1L, 1L, "user")
			);

			assertEquals("댓글이 삭제 되었습니다", commentDeleteResponse.getMessage());
		}
	}

	@Nested
	@DisplayName("댓글 조회")
	class GetAllComment {

		@Test
		@DisplayName("성공")
		void success() {

			Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
			Page<Comment> commentList = new PageImpl<>(List.of(comment, comment2));

			given(validator.getRecordById(1L)).willReturn(record);

			given(commentRepository.findCommentByRecord(record, pageable)).willReturn(commentList);
			Page<CommentResponse> commentResponse = assertDoesNotThrow(
					() -> commentService.getAllComments(1L, pageable)
			);

			assertEquals(2, commentResponse.getContent().size());
			assertEquals("user", commentResponse.getContent().get(0).getUserName());

		}
	}
}