package com.bmsapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionsAdvice {
	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String userNotFound(UserNotFoundException ex) {
		return ex.getMessage();
	}

	@ExceptionHandler(UserNotCreatedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String userNotCreated(UserNotCreatedException ex) {
		return ex.getMessage();
	}

	

}