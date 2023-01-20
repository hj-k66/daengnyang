package com.daengnyangffojjak.dailydaengnyang.security;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorResponse;
import com.daengnyangffojjak.dailydaengnyang.exception.SecurityCustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request,response);
        }catch (SecurityCustomException exception){
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, exception);
        }
    }

    private void setErrorResponse(HttpStatus status, HttpServletResponse res, SecurityCustomException exception) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode(), exception.toString());
        Response<ErrorResponse> error = Response.error(errorResponse);


        res.setStatus(status.value());
        res.setContentType("application/json");
        res.setCharacterEncoding("utf-8");
        res.getWriter().write(objectMapper.writeValueAsString(error)); //Response객체를 response의 바디값으로 파싱

    }
}
