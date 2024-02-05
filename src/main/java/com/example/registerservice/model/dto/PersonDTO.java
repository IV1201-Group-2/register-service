package com.example.registerservice.model.dto;

import lombok.Data;


/**
 * PersonDTO holds the data transfer object which represents the information
 * required by the registration form.
 * {@code @Data} is used to automatically generate getters and setters.
 */
@Data
public class PersonDTO {

    /**
     * person_id is the primary key in the database.
     */
    private Long person_id;

    /**
     * The first name of the registering user.
     */
    private String name;

    /**
     * The surname of the registering user.
     */
    private String surname;

    /**
     * The social security number of the registering user.
     */
    private String pnr;

    /**
     * The email of the registering user.
     */
    private String email;

    /**
     * The password of the registering user.
     */
    private String password;

    /**
     * The username of the registering user.
     */
    private String username;

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
