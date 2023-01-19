package com.daengnyangffojjak.dailydaengnyang.domain.dto.user;

import com.daengnyangffojjak.dailydaengnyang.exception.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Response<T> {
    private String resultCode;
    private T result;

    public static Response<ErrorResponse> error(ErrorResponse errorResponse){
        return new Response<>("ERROR", errorResponse);
    }
    public static <T> Response<T> success(T result){
        return new Response<>("SUCCESS", result);
    }
}