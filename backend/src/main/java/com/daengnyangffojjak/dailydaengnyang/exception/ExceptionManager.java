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

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> MethodArgumentNotValidExceptionHandler(
			MethodArgumentNotValidException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(Response.error(new ErrorResponse(ErrorCode.INVALID_REQUEST,
						e.getBindingResult().getAllErrors().get(0).getDefaultMessage())));
	}


	@ExceptionHandler(SecurityCustomException.class)
	public ResponseEntity<?> SecurityCustomExceptionHandler(SecurityCustomException e) {
		return ResponseEntity.status(e.getErrorCode().getStatus())
				.body(Response.error(new ErrorResponse(e.getErrorCode(), e.toString())));
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
	@ExceptionHandler(TagException.class)
	public ResponseEntity<?> tagExceptionHandler(TagException e) {
		return ResponseEntity.status(e.getErrorCode().getStatus())
				.body(Response.error(new ErrorResponse(e.getErrorCode(), e.toString())));
	}
}
