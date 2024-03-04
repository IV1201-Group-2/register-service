package com.example.registerservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * PersonDTO holds the data transfer object which represents the information
 * required by the registration form.
 * {@code @Data} is used to automatically generate getters and setters.
 * {@code @AllArgsConstructor} creates and initializes all fields of an object in one class.
 */
@Getter
@AllArgsConstructor
public class PersonDTO {

    /**
     * The first name of the registering user.
     */
    private final String name;

    /**
     * The surname of the registering user.
     */
    private final String surname;

    /**
     * The social security number of the registering user.
     */
    private final String pnr;

    /**
     * The email of the registering user.
     */
    private final String email;

    /**
     * The password of the registering user.
     */
    private final String password;

    /**
     * The username of the registering user.
     */
    private final String username;
}
