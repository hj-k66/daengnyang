package com.daengnyangffojjak.dailydaengnyang.exception;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionManager {

	@ExceptionHandler(SecurityCustomException.class)
	public ResponseEntity<?> SecurityCustomExceptionHandler(SecurityCustomException e) {
		return ResponseEntity.status(e.getErrorCode().getStatus())
				.body(Response.error(new ErrorResponse(e.getErrorCode(), e.toString())));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> argumentExceptionHandler(MethodArgumentNotValidException e) {
		String errorMsg = e.getBindingResult().getFieldError().getDefaultMessage();
		return ResponseEntity.status(e.getStatusCode())
				.body(Response.error(new ErrorResponse(ErrorCode.INVALID_REQUEST, errorMsg)));
	}

	@ExceptionHandler(UserException.class)
	public ResponseEntity<?> userExceptionHandler(UserException e) {
		return ResponseEntity.status(e.getErrorCode().getStatus())
				.body(Response.error(new ErrorResponse(e.getErrorCode(), e.toString())));
	}

	@ExceptionHandler(SQLException.class)
	public ResponseEntity<?> sqlExceptionHandler(SQLException e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Response.error(new ErrorResponse(ErrorCode.DATABASE_ERROR, e.getMessage())));
	}

	@ExceptionHandler(ScheduleException.class)
	public ResponseEntity<?> scheduleExceptionHandler(ScheduleException e) {
		return ResponseEntity.status(e.getErrorCode().getStatus())
				.body(Response.error(new ErrorResponse(e.getErrorCode(), e.toString())));
	}

	@ExceptionHandler(GroupException.class)
	public ResponseEntity<?> groupExceptionHandler(GroupException e) {
		return ResponseEntity.status(e.getErrorCode().getStatus())
				.body(Response.error(new ErrorResponse(e.getErrorCode(), e.toString())));
	}

	@ExceptionHandler(PetException.class)
	public ResponseEntity<?> petExceptionHandler(PetException e) {
		return ResponseEntity.status(e.getErrorCode().getStatus())
				.body(Response.error(new ErrorResponse(e.getErrorCode(), e.toString())));
	}
	@ExceptionHandler(MonitoringException.class)
	public ResponseEntity<?> monitoringExceptionHandler(MonitoringException e) {
		return ResponseEntity.status(e.getErrorCode().getStatus())
				.body(Response.error(new ErrorResponse(e.getErrorCode(), e.toString())));
	}

//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<?> runtimeExceptionHandler(RuntimeException e){
//        log.error(e.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(Response.error(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage())));
//    }
}
