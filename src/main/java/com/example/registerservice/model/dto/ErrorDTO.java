package com.example.registerservice.model.dto;

import lombok.Data;

/**
 * Error data transfer object class presents specific errors based on:
 * 1. If user enters a username, social security number or email that is already taken.
 * 2. If any of the fields were missing when registration was submitted.
 * 3. If the email format was wrong or missed any of the (local-part(@)(domain-part)).
 */
@Data
public class ErrorDTO {
    private String error;
    private String details;

    /**
     * Constructor for the Error data transfer object.
     * @param error responsible for providing specific
     *              error messages during user registration.
     */
    public ErrorDTO(String error) {
        this.error = error;
    }

}
