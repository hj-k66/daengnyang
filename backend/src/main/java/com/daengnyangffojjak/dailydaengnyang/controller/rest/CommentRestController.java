package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.MessageResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.comment.CommentModifyResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.comment.CommentRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.comment.CommentResponse;
import com.daengnyangffojjak.dailydaengnyang.service.CommentService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/records")
public class CommentRestController {

	private final CommentService commentService;

	// 리스트 조회 getCommentListByRecordId
	// 댓글 조회는 모든 회원이 권한을 가진다
	// 댓글 아이디, 내용, 글쓴이, 작성날짜, 포스트 아이디가 표시된다
	// 목록 기능은 페이지 기능이 포함 된다
	// 한 페이지당 피드 갯수는 10개, 총 페이지 갯수 표시, 작성날짜 기준으로 최신순으로 sort
	@GetMapping("/{recordsId}/comments")
	public ResponseEntity<Response<Page<CommentResponse>>> getAllComments(
			@PathVariable Long recordsId,
			@PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {

		Page<CommentResponse> commentResponses = commentService.getAllComments(recordsId, pageable);
		return ResponseEntity.ok().body(Response.success(commentResponses));
	}

	// 댓글 작성 - 로그인한 사람만
	@PostMapping("/{recordsId}/comments")
	public ResponseEntity<Response<CommentResponse>> createComment(
			@PathVariable Long recordsId,
			@RequestBody CommentRequest commentRequest,
			@AuthenticationPrincipal UserDetails user) {
		System.out.println("????????");
		CommentResponse commentResponse = commentService.createComment(recordsId, commentRequest, user.getUsername());

		System.out.println("?????" + commentResponse.getId());
		return ResponseEntity.created(URI.create("/api/v1/records/" + recordsId + "comments" +
				commentResponse.getId())).body(Response.success(commentResponse));
	}

	// 댓글 수정
	@PutMapping("/{recordsId}/comments/{id}")
	public ResponseEntity<Response<CommentModifyResponse>> modifyComment(
			@PathVariable Long recordsId,
			@PathVariable Long id,
			@RequestBody CommentRequest commentRequest,
			@AuthenticationPrincipal UserDetails user) {

		CommentModifyResponse modifyResponse = commentService.modifyComment(recordsId, id, commentRequest, user.getUsername());

		return ResponseEntity.created(
				URI.create("api/v1/records/" + recordsId + "/comments/"
						+ id)).body(Response.success(modifyResponse));
	}

	// 댓글 삭제
	@DeleteMapping ("/{recordsId}/comments/{id}")
	public Response<MessageResponse> deleteComment(
			@PathVariable Long recordsId,
			@PathVariable Long id,
			@AuthenticationPrincipal UserDetails user) {

		MessageResponse messageResponse = commentService.deleteComment(recordsId, id, user.getUsername());
		return Response.success(messageResponse);
	}

	// 조회
	// 내가 작성한 댓글만 보이는 기능
	// 포스트아이디, 댓글내용, 작성날짜가 표시된다
	// 목록 기능은 페이징 기능이 포함된다
	// 한 페이지당 피드 갯수는 20개, 총 페이지 갯수 표시, 작성날짜 기준으로 sort
	// 로그인된 유저만의 피드목록을 필터링하는 기능 pageable 사용
	@GetMapping("/comments/my")
	public ResponseEntity<Response<Page<CommentResponse>>> myComments(
			@AuthenticationPrincipal UserDetails user,
			@PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {

		Page<CommentResponse> commentResponses = commentService.getMyComments(user.getUsername(), pageable);

		return ResponseEntity.ok().body(Response.success(commentResponses));
	}


}
