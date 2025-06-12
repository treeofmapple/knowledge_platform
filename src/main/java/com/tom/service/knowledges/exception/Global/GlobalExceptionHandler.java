package com.tom.service.knowledges.exception.Global;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tom.service.knowledges.exception.AlreadyExistsException;
import com.tom.service.knowledges.exception.BadRequestException;
import com.tom.service.knowledges.exception.ConflictException;
import com.tom.service.knowledges.exception.DuplicateException;
import com.tom.service.knowledges.exception.InternalException;
import com.tom.service.knowledges.exception.InvalidDateException;
import com.tom.service.knowledges.exception.NotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ NotFoundException.class })
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException exp, HttpServletRequest request) {
        return buildErrorResponse(exp.getMessage(), HttpStatus.NOT_FOUND, request, null);
    }

    @ExceptionHandler({ AlreadyExistsException.class, DuplicateException.class, ConflictException.class })
    public ResponseEntity<ErrorResponse> handleConflictException(RuntimeException exp, HttpServletRequest request) {
        return buildErrorResponse(exp.getMessage(), HttpStatus.CONFLICT, request, null);
    }
    
    @ExceptionHandler({ InvalidDateException.class })
    public ResponseEntity<ErrorResponse> handleDateException(NumberFormatException exp, HttpServletRequest request) {
        return buildErrorResponse(exp.getMessage(), HttpStatus.EXPECTATION_FAILED, request, null);
    }
    
    @ExceptionHandler({ IllegalStateException.class, BadRequestException.class })
    public ResponseEntity<ErrorResponse> handleIllegalException(IllegalStateException exp, HttpServletRequest request) {
        return buildErrorResponse(exp.getMessage(), HttpStatus.BAD_REQUEST, request, null);
    }

    @ExceptionHandler({ InternalException.class })
    public ResponseEntity<ErrorResponse> handleInternalException(InternalException exp, HttpServletRequest request) {
        return buildErrorResponse(exp.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request, null);
    }
	
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exp, HttpServletRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        exp.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        return buildErrorResponse("Validation failed for one or more fields", HttpStatus.BAD_REQUEST, request, validationErrors);
    }

	private ResponseEntity<ErrorResponse> buildErrorResponse(
			String message, HttpStatus status, HttpServletRequest request, Map<String, String> validationErrors
		) {
		
		ErrorResponse response = new ErrorResponse(
				LocalDateTime.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				request.getRequestURI(),
				validationErrors
		);
			
		return ResponseEntity.status(status).body(response);
	}
	
}