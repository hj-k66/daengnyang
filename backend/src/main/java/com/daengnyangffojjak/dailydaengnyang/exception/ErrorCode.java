package com.daengnyangffojjak.dailydaengnyang.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

	/**
	 * Common
	 **/
	DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB에러"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에러"),
	INVALID_VALUE(HttpStatus.BAD_REQUEST, "입력값이 잘못되었습니다."),
	INVALID_REQUEST(HttpStatus.CONFLICT, "잘못된 요청입니다."),

	/**
	 * Security
	 **/
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),

	/**
	 * UserException
	 **/
	DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "UserName이 중복됩니다."),
	DUPLICATED_EMAIL(HttpStatus.CONFLICT, "이메일이 중복됩니다."),
	USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "Not founded"),
	EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "가입되지 않은 이메일입니다."),
	INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "패스워드가 잘못되었습니다."),
	INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "사용자가 권한이 없습니다."),
	INVALID_EMAIL(HttpStatus.BAD_REQUEST, "이메일 형식이 잘못되었습니다."),

	/**
	 * Schedule
	 **/
	SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "등록된 일정이 없습니다."),

	/**
	 * Record
	 */
	RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "등록된 일기가 없습니다."),

	/**
	 * Pet
	 **/
	PET_NOT_FOUND(HttpStatus.NOT_FOUND, "등록된 반려동물이 아닙니다."),
	INVALID_BIRTHDAY(HttpStatus.BAD_REQUEST, "잘못된 생일날짜입니다."),

	/**
	 * Comment
	 **/
	COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글이 없습니다."),

	/**
	 * Group
	 **/
	GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 그룹이 존재하지 않습니다."),
	GROUP_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "그룹에 해당 유저가 존재하지 않습니다."),
	GROUP_OWNER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 그룹의 그룹장이 존재하지 않습니다."),

	/**
	 * Monitoring
	 **/
	MONITORING_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 모니터링이 존재하지 않습니다."),
	/**
	 * Tag
	 **/
	TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 태그가 존재하지 않습니다."),
	DUPLICATED_TAG_NAME(HttpStatus.CONFLICT, "이미 존재하는 태그 입니다."),

	/**
	 * Disease
	 **/
	DISEASE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 질병 정보가 존재하지 않습니다."),
	DUPLICATED_DISEASE_NAME(HttpStatus.CONFLICT, "이미 존재하는 질병이름 입니다.");

	private final HttpStatus status;
	private final String message;
}
