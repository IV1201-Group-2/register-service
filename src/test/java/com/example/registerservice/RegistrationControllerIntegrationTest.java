package com.example.registerservice;

import com.example.registerservice.controller.RegisterController;
import com.example.registerservice.model.dto.PersonDTO;
import com.example.registerservice.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * RegistrationControllerIntegration test uses TestContainers and demonstrates integration testing.
 * The tests included are:
 * 1. Checking if a user attempting to submit a username, email or social security number
 * returns the correct HTTP Status response.
 * 2. Checking if the user attempting to submit a registration where one of the required fields
 * are missing  returns the correct HTTP Status response.
 * 3. Checking if the user attempting to submit an email that has an incorrect format
 * returns the correct HTTP Status response.
 * {@code @Transactional} ensures application is saved to the database only if
 * the transaction is successful.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Transactional
public class RegistrationControllerIntegrationTest {

    /**
     * Mocking a PostgreSQL database for the integration tests.
     * The database is configured with a specific, name, username and
     * password as well as the latest postgreSQL version.
     * {@code @Container} sets the field as a TestContainer container.
     */
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest").withDatabaseName("postgrestest").withUsername("postgres").withPassword("Qwerty123456!");

    /**
     * PersonService is an autowired instance containing business-logic for person-related operations.
     * {@code @Autowired} provides automatic dependency injection.
     */
    @Autowired
    private PersonService personService;

    /**
     * RegisterController is an autowired instance that handles HTTP requests.
     * {@code @Autowired} provides automatic dependency injection.
     */
    @Autowired
    RegisterController registerController;

    /**
     * The method sets the property JDBC URL spring.datasource.url
     * dynamically for the postgreSQL container.
     *
     * @param dynamicPropertyRegistry adding dynamic properties.
     *                                {@code @DynamicPropertySource} allows adding properties with dynamic values for test
     */
    @DynamicPropertySource
    public static void testProps(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgres::getJdbcUrl);

    }

    /**
     * JUnit test annotated with {@code @Test} to see if the container is running.
     */
    @Test
    void mockTest() {
        System.out.println("Is container running?");
    }

    /**
     * JUnit test to simulate a situation where a user is registering with data that
     * already exists in the database.
     * The test checks to see if a user is registering with a unique username, email and
     * social security number and the correct HTTP Status response is returned, to make sure
     * duplicate data is not saved in the database.
     */
    @Test
    void newUserRegistration() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "127.0.0.1");

        //New user registered with unique data fields submitted
        PersonDTO firstRegistration = new PersonDTO(1L, "Clara", "Eklund", "202203323434", "claraeklund@kth.com", "123", "claraek");
        ResponseEntity<Object> firstUserRegistration = registerController.registration(firstRegistration, request);
        assertEquals(HttpStatus.OK, firstUserRegistration.getStatusCode());
    }

    /**
     * JUnit test to simulate a situation where a user is registering with data that
     * already exists in the database.
     * The test checks to see whether the username is detected in the database
     * and the correct HTTP Status response is returned, to make sure
     * duplicate data is not saved in the database.
     */
    @Test
    void isUsernameAlreadyInDatabase() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "127.0.0.1");

        //Saving a unique user to the database
        registerController.registration(new PersonDTO(1L, "Anders", "Dean", "202012123454", "andersbo@kth.com", "123", "Anders"), request);
        //User attempting to register with a username that is taken
        PersonDTO secondRegistration = new PersonDTO(1L, "Tiana", "Bell", "200101014565", "tiana@kth.com", "123", "Anders");
        ResponseEntity<Object> secondUserRegistration = registerController.registration(secondRegistration, request);
        assertEquals(HttpStatus.BAD_REQUEST, secondUserRegistration.getStatusCode());
    }

    /**
     * JUnit test to simulate a situation where a user is registering with data that
     * already exists in the database.
     * The test checks to see whether the Social security number is detected in the database
     * and the correct HTTP Status response is returned, to make sure
     * duplicate data is not saved in the database.
     */
    @Test
    void isSocialSecurityNumberAlreadyInDatabase() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "127.0.0.1");

        //Saving a unique user to the database
        registerController.registration(new PersonDTO(1L, "Anders", "Dean", "202012123454", "andersbo@kth.com", "123", "Anders"), request);
        //User attempting to register with a Social security number that is taken
        PersonDTO thirdRegistration = new PersonDTO(1L, "Karin", "Elbert", "202012123454", "karinelb@kth.com", "123", "karin20");
        ResponseEntity<Object> thirdUserRegistration = registerController.registration(thirdRegistration, request);
        assertEquals(HttpStatus.BAD_REQUEST, thirdUserRegistration.getStatusCode());

    }

    /**
     * JUnit test to simulate a situation where a user is registering with data that
     * already exists in the database.
     * The test checks to see whether the email is detected in the database
     * and the correct HTTP Status response is returned, to make sure
     * duplicate data is not saved in the database.
     */
    @Test
    void isEmailAlreadyInDatabase() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "127.0.0.1");

        //Saving a unique user to the database
        registerController.registration(new PersonDTO(1L, "Anders", "Dean", "202012123454", "andersbo@kth.com", "123", "Anders"), request);
        //User attempting to register with an email that is taken
        PersonDTO fourthRegistration = new PersonDTO(1L, "Fatima", "Akbari", "200011118989", "andersbo@kth.com", "123", "fatimakbari");
        ResponseEntity<Object> fourthUserRegistration = registerController.registration(fourthRegistration, request);
        assertEquals(HttpStatus.BAD_REQUEST, fourthUserRegistration.getStatusCode());
    }

    /**
     * JUnit test missing the name field to check if the correct
     * HTTP Status response is returned so that a registered
     * user is not saved if any of the required fields are missing.
     */
    @Test
    void isNameMissingInRegistrationForm() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "127.0.0.1");

        //User attempting to submit a registration where the name is missing
        PersonDTO personMissingName = new PersonDTO(1L, "", "Eklund", "202203323434", "claraeklund@kth.com", "123", "claraek");
        ResponseEntity<Object> firstUserRegistration = registerController.registration(personMissingName, request);
        assertEquals(HttpStatus.BAD_REQUEST, firstUserRegistration.getStatusCode());

    }

    /**
     * JUnit test missing the surname field to check if the correct
     * HTTP Status response is returned so that a registered
     * user is not saved if any of the required fields are missing.
     */
    @Test
    void isSurnameMissingInRegistrationForm() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "127.0.0.1");

        //User attempting to submit a registration where the surname is missing
        PersonDTO personMissingSurname = new PersonDTO(1L, "Anders", "", "202012123454", "andersbo@kth.com", "123", "anders12");
        ResponseEntity<Object> secondUserRegistration = registerController.registration(personMissingSurname, request);
        assertEquals(HttpStatus.BAD_REQUEST, secondUserRegistration.getStatusCode());
    }

    /**
     * JUnit test missing the Social security number field to check if the correct
     * HTTP Status response is returned so that a registered
     * user is not saved if any of the required fields are missing.
     */
    @Test
    void isSocialSecurityNumberMissingInRegistrationForm() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "127.0.0.1");

        //User attempting to submit a registration where the Social security number is missing
        PersonDTO personMissingPnr = new PersonDTO(1L, "Karin", "Elbert", "", "karinelb@kth.com", "123", "karin20");
        ResponseEntity<Object> thirdUserRegistration = registerController.registration(personMissingPnr, request);
        assertEquals(HttpStatus.BAD_REQUEST, thirdUserRegistration.getStatusCode());
    }

    /**
     * JUnit test that checks if an email field is detected and the correct
     * HTTP Status response is returned so that a registered
     * user is not saved if any of the required fields are missing.
     */
    @Test
    void isEmailMissingInRegistrationForm() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "127.0.0.1");

        //User attempting to submit a registration where the email is missing
        PersonDTO personMissingEmail = new PersonDTO(1L, "Fatima", "Akbari", "200011118989", "", "123", "fatimakbari");
        ResponseEntity<Object> fourthUserRegistration = registerController.registration(personMissingEmail, request);
        assertEquals(HttpStatus.BAD_REQUEST, fourthUserRegistration.getStatusCode());
    }

    /**
     * JUnit test missing the password field to check if the correct
     * HTTP Status response is returned so that a registered
     * user is not saved if any of the required fields are missing.
     */
    @Test
    void isPasswordMissingInRegistrationForm() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "127.0.0.1");

        //User attempting to submit a registration where the password is missing
        PersonDTO personMissingPassword = new PersonDTO(1L, "Elira", "Ahlborg", "200109088687", "elira21@kth.com", "", "eliraa");
        ResponseEntity<Object> fifthUserRegistration = registerController.registration(personMissingPassword, request);
        assertEquals(HttpStatus.BAD_REQUEST, fifthUserRegistration.getStatusCode());
    }

    /**
     * JUnit test that checks if an empty field is detected and the correct
     * HTTP Status response is returned so that a registered
     * user is not saved if any of the required fields are missing.
     */
    @Test
    void isUsernameMissingInRegistrationForm() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "127.0.0.1");

        //User attempting to submit a registration where the username is missing
        PersonDTO personMissingUsername = new PersonDTO(1L, "Dannie", "Kvist", "200403030909", "dankvist@kth.com", "123", "");
        ResponseEntity<Object> sixthUserRegistration = registerController.registration(personMissingUsername, request);
        assertEquals(HttpStatus.BAD_REQUEST, sixthUserRegistration.getStatusCode());

    }

    /**
     * JUnit test verifies that an email missing a local part returns the expected HTTP Status response.
     */
    @Test
    void isEmailMissingLocalPart() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "127.0.0.1");

        //User attempting to submit a registration where the email is missing a local part
        PersonDTO emailMissingLocalPart = new PersonDTO(1L, "Clara", "Eklund", "202203323434", "@kth.com", "123", "claraek");
        ResponseEntity<Object> firstUserRegistration = registerController.registration(emailMissingLocalPart, request);
        assertEquals(HttpStatus.BAD_REQUEST, firstUserRegistration.getStatusCode());

    }

    /**
     * JUnit test verifies that an email missing at sign returns the expected HTTP Status response.
     */
    @Test
    void isEmailMissingAtSign() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "127.0.0.1");

        //User attempting to submit a registration where the email is missing an at sign
        PersonDTO emailMissingAtSign = new PersonDTO(1L, "Anders", "Dean", "202012123454", "andersbokth.com", "123", "anders12");
        ResponseEntity<Object> secondUserRegistration = registerController.registration(emailMissingAtSign, request);
        assertEquals(HttpStatus.BAD_REQUEST, secondUserRegistration.getStatusCode());
    }

    /**
     * JUnit test verifies that an email missing a domain part returns the expected HTTP Status response.
     */
    @Test
    void isEmailMissingDomainPart() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "127.0.0.1");

        //User attempting to submit a registration where the email is missing a domain part
        PersonDTO emailMissingDomainPart = new PersonDTO(1L, "Karin", "Elbert", "200304056787", "karinelb@", "123", "karin20");
        ResponseEntity<Object> thirdUserRegistration = registerController.registration(emailMissingDomainPart, request);
        assertEquals(HttpStatus.BAD_REQUEST, thirdUserRegistration.getStatusCode());
    }

    /**
     * JUnit test verifies that an email missing a local part and at sign returns the expected HTTP Status response.
     */
    @Test
    void isEmailMissingLocalAndAtSign() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "127.0.0.1");

        //User attempting to submit a registration where the email is missing a local part and atsign
        PersonDTO emailMissingLocalAndAtSign = new PersonDTO(1L, "Fatima", "Akbari", "200011118989", "kth.se", "123", "fatimakbari");
        ResponseEntity<Object> fourthUserRegistration = registerController.registration(emailMissingLocalAndAtSign, request);
        assertEquals(HttpStatus.BAD_REQUEST, fourthUserRegistration.getStatusCode());

    }

    /**
     * JUnit test verifies that an email missing a local part and a domain part returns the expected HTTP Status response.
     */
    @Test
    void isEmailMissingLocalPartAndDomainPart() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "127.0.0.1");

        //User attempting to submit a registration where the email is missing a local part and domain part
        PersonDTO emailMissingLocalPartAndDomainPart = new PersonDTO(1L, "Elira", "Ahlborg", "200109088687", "@", "123", "eliraa");
        ResponseEntity<Object> fifthUserRegistration = registerController.registration(emailMissingLocalPartAndDomainPart, request);
        assertEquals(HttpStatus.BAD_REQUEST, fifthUserRegistration.getStatusCode());
    }

    /**
     * JUnit test verifies that an email missing aat sign and a domain part returns the expected HTTP Status response.
     */
    @Test
    void isEmailMissingAtSignAndDomainPart() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "127.0.0.1");

        //User attempting to submit a registration where the email is missing atsign and domain part
        PersonDTO emailMissingAtSignAndDomainPart = new PersonDTO(1L, "Andreas", "Linder", "200112124565", "andreas", "123", "test");
        ResponseEntity<Object> sixthUserRegistration = registerController.registration(emailMissingAtSignAndDomainPart, request);
        assertEquals(HttpStatus.BAD_REQUEST, sixthUserRegistration.getStatusCode());


    }

    /**
     * JUnit test verifies that a correct email format returns the expected HTTP Status response.
     */
    @Test
    void isEmailCorrect() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "127.0.0.1");

        //User registering with a correct email format
        PersonDTO correctEmailFormat = new PersonDTO(1L, "Dannie", "Kvist", "200103034989", "dankvist@kth.com", "123", "dankvistt");
        ResponseEntity<Object> seventhUserRegistration = registerController.registration(correctEmailFormat, request);
        assertEquals(HttpStatus.OK, seventhUserRegistration.getStatusCode());

    }
}