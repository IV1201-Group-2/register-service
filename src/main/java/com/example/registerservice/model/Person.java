package com.example.registerservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Person model representing the database structure created for each registered user.
 * {@code @Data} automatically generates getters and setters for this class.
 * {@code @NoArgsConstructor} generates default constructor to instantiate objects required by JPA
 * {@code @AllArgsConstructor} creates and initializes all fields of an object in one class.
 * {@code @Entity} indicates that the following class is an entity that maps to a table in the database.
 * {@code @Builder} creates a builder for the class for a more readable code syntax.
 *
 * @Table(name = "person") hardcoded the table name of this class in the database to "person".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "person")
public class Person {

    /**
     * {@code @Id} marks the person_id as the primary key
     * {@code @GeneratedValue} sets the person_id to be automatically generated and
     * incremented by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long person_id;

    /**
     * {@code @Column} sets a length for the field "name".
     * The first name of the registering person.
     */
    @Column(length = 255)
    private String name;

    /**
     * The surname of the registering person.
     */
    @Column(length = 255)
    private String surname;

    /**
     * The social security number of the registering person.
     */
    @Column(length = 255)
    private String pnr;

    /**
     * The email of the registering person.
     */
    @Column(length = 255)
    private String email;

    /**
     * The password of the registering person.
     */
    @Column(length = 255)
    private String password;

    /**
     * The role_id of the registering person. A person can either be an applicant or a recruiter.
     */
    private int role_id;

    /**
     * The username of the registering person.
     */
    @Column(length = 255)
    private String username;

}
