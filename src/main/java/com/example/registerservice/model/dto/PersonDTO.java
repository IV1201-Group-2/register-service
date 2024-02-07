package com.example.registerservice.model.dto;

import lombok.Getter;


/**
 * PersonDTO holds the data transfer object which represents the information
 * required by the registration form.
 * {@code @Data} is used to automatically generate getters and setters.
 */
@Getter
public class PersonDTO {

    /**
     * person_id is the primary key in the database.
     */
    private final Long person_id;

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

    /**
     * Constructor for PersonDTO
     */
    public PersonDTO(Long person_id, String name, String surname, String pnr, String email, String password, String username) {
        this.person_id = person_id;
        this.name = name;
        this.surname = surname;
        this.pnr = pnr;
        this.email = email;
        this.password = password;
        this.username = username;
    }
}
