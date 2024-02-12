package com.example.registerservice.exception;

import com.example.registerservice.model.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * This is an exception handler class with the annotation
 * {@code @ControllerAdvice} that can handle various types of
 * exceptions, returning specific ErrorDTO's and HTTP responses.
 */
@ControllerAdvice
public class ExceptionHandler {

    /**
     * Method for handling exception.
     *
     * @return a response entity with the appropriate ErrorDTO and HTTP status
     */
    @org.springframework.web.bind.annotation.ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleExceptions() {
        return new ResponseEntity<>(new ErrorDTO("UNKNOWN"), HttpStatus.BAD_REQUEST);
    }
}
