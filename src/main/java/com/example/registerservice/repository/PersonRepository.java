package com.example.registerservice.repository;

import com.example.registerservice.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The personRepository is a repository that contains methods for data
 * retrieval/modification operations.
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    /**
     * Retrieves the email from the database.
     *
     * @param email represents the email that is being searched
     *              for in the database.
     * @return Object of the type Person class.
     */
    Person findByEmail(String email);

    /**
     * Retrieves the username from the database.
     *
     * @param username represents the username that is being
     *                 searched for in the database.
     * @return Object of the type Person class.
     */
    Person findByUsername(String username);

    /**
     * Retrieves the social security number from the database.
     *
     * @param pnr represents the social security number that is
     *            being searched for in the database.
     * @return Object of the type Person class.
     */
    Person findByPnr(String pnr);

}
