package com.daengnyangffojjak.dailydaengnyang.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SecurityCustomException extends RuntimeException{
    private ErrorCode errorCode;
    private String message;

    @Builder
    public SecurityCustomException(ErrorCode errorCode){
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        if (message == null) {
            return errorCode.getMessage();
        }
        return String.format("%s %s", errorCode.getMessage(), message);
    }
}
