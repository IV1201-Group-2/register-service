package com.example.registerservice.exception;

import com.example.registerservice.model.dto.ErrorDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * This is an exception handler class with the annotation
 * {@code @ControllerAdvice} that can handle various types of
 * exceptions, returning specific ErrorDTO and HTTP status response.
 */
@ControllerAdvice
public class ExceptionHandler {

    /**
     * Logger to log errors caught by the Exception handler.
     */
    private static final Logger logger = LogManager.getLogger(ExceptionHandler.class);


    /**
     * Method for handling exception.
     *
     * @return a response entity with the appropriate ErrorDTO and HTTP status.
     */
    @org.springframework.web.bind.annotation.ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleExceptions(Exception e) {
        logger.error("Log Exception caught: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorDTO("UNKNOWN"), HttpStatus.BAD_REQUEST);
    }
}
