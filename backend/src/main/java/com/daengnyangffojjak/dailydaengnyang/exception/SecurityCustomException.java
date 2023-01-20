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
        return errorCode.getMessage()
                + this.message;
    }
}
