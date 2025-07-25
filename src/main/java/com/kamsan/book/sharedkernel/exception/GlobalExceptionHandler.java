package com.kamsan.book.sharedkernel.exception;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ProblemDetail> handleApiException(ApiException ex) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
		return ResponseEntity.of(problemDetail).build();
	}
	
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ProblemDetail> handleNoSuchElementException(NoSuchElementException ex) {
	    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
	    return ResponseEntity.of(problemDetail).build();
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
		String errors = ex.getBindingResult().getFieldErrors().stream()
			.map(err -> err.getDefaultMessage())
			.collect(Collectors.joining(", "));
		
		ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation error(s): " + errors);
		return ResponseEntity.badRequest().body(detail);
	}
}
 