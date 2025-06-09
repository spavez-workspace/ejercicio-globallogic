package com.globallogic.ejercicio.exception;

import org.springframework.http.HttpStatus;

public class CustomJwtException extends RuntimeException {

	private final HttpStatus status;

    public CustomJwtException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
	
}
