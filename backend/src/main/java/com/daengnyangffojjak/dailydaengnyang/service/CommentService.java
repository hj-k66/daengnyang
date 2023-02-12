package com.daengnyangffojjak.dailydaengnyang.service;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.comment.CommentModifyResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.comment.CommentRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.comment.CommentResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Comment;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.User;
import com.daengnyangffojjak.dailydaengnyang.exception.CommentException;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.repository.CommentRepository;
import com.daengnyangffojjak.dailydaengnyang.repository.RecordRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
	private final CommentRepository commentRepository;
	private final Validator validator;

	// 댓글 등록
	@Transactional
	public CommentResponse createComment(Long recordsId, CommentRequest commentRequest, String userName) {

		// 유저가 없는 경우 예외 발생
		User user = validator.getUserByUserName(userName);

		// 일기가 없는 경우 예외 발생
		Record record = validator.getRecordById(recordsId);

		Comment savedComment = commentRepository.save(commentRequest.toEntity(user, record));

		return CommentResponse.from(savedComment);
	}

	// 댓글 수정
	@Transactional
	public CommentModifyResponse modifyComment(Long recordsId, Long id, CommentRequest commentRequest, String userName) {

		// 유저가 없는 경우 예외 발생
		User user = validator.getUserByUserName(userName);

		// 일기가 없는 경우 예외 발생
		Record record = validator.getRecordById(recordsId);

		// 댓글이 없는 경우 예외 발생
		Comment comment = validator.getCommentById(id);

		// 댓글 작성 유저와 로그인 유저가 같지 않을 경우 예외 발생
		if (!comment.getUser().getId().equals(user.getId())) {
			throw new CommentException(ErrorCode.INVALID_PERMISSION);
		}

		comment.modifyComment(commentRequest);
		Comment updated = commentRepository.saveAndFlush(comment);

		return CommentModifyResponse.from(updated);
	}

	// 댓글 삭제
	@Transactional
	public MessageResponse deleteComment(Long recordsId, Long id, String userName) {

		// 유저가 없는 경우 예외 발생
		User user = validator.getUserByUserName(userName);

		// 일기가 없는 경우 예외 발생
		Record record = validator.getRecordById(recordsId);

		// 댓글이 없는 경우 예외 발생
		Comment comment = validator.getCommentById(id);

		// 댓글 작성 유저와 로그인 유저가 같지 않을 경우 예외 발생
		if (!comment.getUser().getId().equals(user.getId())) {
			throw new CommentException(ErrorCode.INVALID_PERMISSION);
		}

		comment.deleteSoftly();

		return new MessageResponse("댓글이 삭제 되었습니다");
	}


	// 댓글 조회
	@Transactional(readOnly = true)
	public Page<CommentResponse> getAllComments(Long recordsId, Pageable pageable) {

		// 일기가 없는 경우 예외 발생
		Record record = validator.getRecordById(recordsId);

		Page<Comment> comments = commentRepository.findCommentByRecord(record, pageable);

		return commentRepository.findCommentByRecord(record, pageable)
				.map(CommentResponse::from);
	}

	@Transactional(readOnly = true)
	public Page<CommentResponse> getMyComments(String userName, Pageable pageable) {

		// 유저가 없는 경우 예외 발생
		User user = validator.getUserByUserName(userName);

		Page<Comment> comments = commentRepository.findCommentByUser(user, pageable);

		return commentRepository.findCommentByUser(user, pageable)
				.map(CommentResponse::from);
	}

}
