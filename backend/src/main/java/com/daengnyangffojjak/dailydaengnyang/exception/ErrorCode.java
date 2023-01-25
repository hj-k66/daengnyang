package com.daengnyangffojjak.dailydaengnyang.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "UserName이 중복됩니다."),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "이메일이 중복됩니다."),
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND,"Not founded"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "패스워드가 잘못되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "사용자가 권한이 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 포스트가 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글이 없습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB에러"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에러"),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "입력값이 잘못되었습니다."),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "이메일 형식이 잘못되었습니다."),
    INVALID_REQUEST(HttpStatus.CONFLICT, "잘못된 요청입니다."),
    PET_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 반려동물이 없습니다."),
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 그룹이 없습니다.");
    private final HttpStatus status;
    private final String message;
}
