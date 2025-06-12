package com.globallogic.ejercicio.exception.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.globallogic.ejercicio.dto.ErrorDto;
import com.globallogic.ejercicio.dto.ErrorResponseDto;
import com.globallogic.ejercicio.exception.*;

import io.jsonwebtoken.security.InvalidKeyException;

import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private ResponseEntity<ErrorResponseDto> buildErrorResponse(String mensaje, HttpStatus status) {
        ErrorDto errorDto = new ErrorDto(
            LocalDateTime.now().toString(),
            status.value(),
            mensaje
        );
        return ResponseEntity.status(status).body(new ErrorResponseDto(errorDto));
    }
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        
        return buildErrorResponse("Error de validacion en Request: " + errors.toString(), HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ErrorResponseDto> handleUserDuplicateEntry(UserAlreadyExistsException ex){
		return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponseDto> handleBadCredentials(BadCredentialsException ex){
		return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponseDto> handleUserNotFound(UserNotFoundException ex){
		return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(CustomJwtException.class)
	public ResponseEntity<ErrorResponseDto> handleUserDuplicateEntry(CustomJwtException ex){
		return buildErrorResponse(ex.getMessage(), ex.getStatus());
	}
	
	@ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(Exception ex) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ErrorResponseDto> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
		return buildErrorResponse("El tipo de contenido no es soportado. Usa 'application/json'.", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponseDto> handleInvalidJson(HttpMessageNotReadableException ex) {
		return buildErrorResponse("Error en el formato del JSON: " + ex.getMostSpecificCause().getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(EncryptionException.class)
	public ResponseEntity<ErrorResponseDto> handleEncryptionException(EncryptionException ex) {
	    Throwable cause = ex.getCause();

	    if (cause instanceof InvalidKeyException) {
	        return buildErrorResponse("Clave AES inválida", HttpStatus.BAD_REQUEST);
	    } else if (cause instanceof BadPaddingException) {
	        return buildErrorResponse("Padding incorrecto. La clave o el texto están mal", HttpStatus.BAD_REQUEST);
	    } else if (cause instanceof IllegalBlockSizeException) {
	        return buildErrorResponse("Bloque ilegítimo. El texto no se puede procesar", HttpStatus.BAD_REQUEST);
	    }

	    return buildErrorResponse("Error de encriptación desconocido: " + cause.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
