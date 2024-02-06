package com.example.registerservice;

import com.example.registerservice.controller.RegisterController;
import com.example.registerservice.model.dto.PersonDTO;
import com.example.registerservice.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 *                  returns the correct HTTP Status response.
 * 2. Checking if the user attempting to submit a registration where one of the required fields
 *                  are missing  returns the correct HTTP Status response.
 * 3. Checking if the user attempting to submit an email that has an incorrect format
 *                  returns the correct HTTP Status response.
 * {@code @Transactional} ensures application is saved to the database only if
 *                      the transaction is successful.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Transactional
public class RegistrationControllerIntegrationTest {

    /**
     * Mocking a PostgreSQL database for the integration tests.
     * The database is configured with a specific, name, username and
     *              password as well as the latest postgreSQL version.
     * {@code @Container} sets the field as a TestContainer container.
     */
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("postgrestest")
            .withUsername("postgres")
            .withPassword("Qwerty123456!");

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
     * @param dynamicPropertyRegistry adding dynamic properties.
     * {@code @DynamicPropertySource} allows adding properties with dynamic values for test
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
     *          already exists in the database.
     * The test checks to see whether duplicate data is detected in the database
     *          and the correct HTTP Status response is returned, to make sure
     *          duplicate data is not saved in the database.
     */
    @Test
    void isApplicantAlreadyInDatabase() throws Exception {
        //New user registered with unique data fields submitted
        PersonDTO firstRegistration = new PersonDTO(1L, "testName", "testSurname", "123456", "test@test.com", "123", "testUsername");
        ResponseEntity<Object> firstUserRegistration = registerController.registration(firstRegistration, null);
        assertEquals(HttpStatus.OK, firstUserRegistration.getStatusCode());

        //User attempting to register with a username that is taken
        PersonDTO secondRegistration = new PersonDTO(1L, "testName", "testSurname", "1234567", "changeEmail@email.com", "123", "testUsername");
        ResponseEntity<Object> secondUserRegistration = registerController.registration(secondRegistration, null);
        assertEquals(HttpStatus.BAD_REQUEST, secondUserRegistration.getStatusCode());

        //User attempting to register with a Social security number that is taken
        PersonDTO thirdRegistration = new PersonDTO(1L, "testName", "testSurname", "123456", "changeEmailAgain@email.com", "123", "changeUsername");
        ResponseEntity<Object> thirdUserRegistration = registerController.registration(thirdRegistration, null);
        assertEquals(HttpStatus.BAD_REQUEST, thirdUserRegistration.getStatusCode());

        //User attempting to register with an email that is taken
        PersonDTO fourthRegistration = new PersonDTO(1L, "testName", "testSurname", "12345678", "test@test.com", "123", "changeUsernameAgain");
        ResponseEntity<Object> fourthUserRegistration = registerController.registration(fourthRegistration, null);
        assertEquals(HttpStatus.BAD_REQUEST, fourthUserRegistration.getStatusCode());
    }

    /**
     * JUnit test that checks if an empty field is detected and the correct
     *          HTTP Status response is returned so that a registered
     *          user is not saved if any of the required fields are missing.
     */
    @Test
    void isAFieldMissingInRegistrationForm() {
        //User attempting to submit a registration where the name is missing
        PersonDTO personMissingName = new PersonDTO(1L, "", "Eklund", "202203323434", "claraeklund@kth.com", "123", "claraek");
        ResponseEntity<Object> firstUserRegistration = registerController.registration(personMissingName, null);
        assertEquals(HttpStatus.BAD_REQUEST, firstUserRegistration.getStatusCode());

        //User attempting to submit a registration where the surname is missing
        PersonDTO personMissingSurname = new PersonDTO(1L, "Anders", "", "202012123454", "andersbo@kth.com", "123", "anders12");
        ResponseEntity<Object> secondUserRegistration = registerController.registration(personMissingSurname, null);
        assertEquals(HttpStatus.BAD_REQUEST, secondUserRegistration.getStatusCode());

        //User attempting to submit a registration where the Social security number is missing
        PersonDTO personMissingPnr = new PersonDTO(1L, "Karin", "Elbert", "", "karinelb@kth.com", "123", "karin20");
        ResponseEntity<Object> thirdUserRegistration = registerController.registration(personMissingPnr, null);
        assertEquals(HttpStatus.BAD_REQUEST, thirdUserRegistration.getStatusCode());

        //User attempting to submit a registration where the email is missing
        PersonDTO personMissingEmail = new PersonDTO(1L, "Fatima", "Akbari", "200011118989", "", "123", "fatimakbari");
        ResponseEntity<Object> fourthUserRegistration = registerController.registration(personMissingEmail, null);
        assertEquals(HttpStatus.BAD_REQUEST, fourthUserRegistration.getStatusCode());

        //User attempting to submit a registration where the password is missing
        PersonDTO personMissingPassword = new PersonDTO(1L, "Elira", "Ahlborg", "200109088687", "elira21@kth.com", "", "eliraa");
        ResponseEntity<Object> fifthUserRegistration = registerController.registration(personMissingPassword, null);
        assertEquals(HttpStatus.BAD_REQUEST, fifthUserRegistration.getStatusCode());

        //User attempting to submit a registration where the username is missing
        PersonDTO personMissingUsername = new PersonDTO(1L, "Dannie", "Kvist", "", "dankvist@kth.com", "123", "dankvistt");
        ResponseEntity<Object> sixthUserRegistration = registerController.registration(personMissingUsername, null);
        assertEquals(HttpStatus.BAD_REQUEST, sixthUserRegistration.getStatusCode());

    }

    /**
     * JUnit test that tests 7 different data transfer objects from user registration
     *          that contains different combinations of email formats.
     * For instance if the user misses any of the combinations of local-part@domain-part,
     *          the correct HTTP Status response should be returned.
     * Finally, the last data transfer object example contains a correct email format and
     *          checks to see that the correct HTTP Status response is returned.
     */
    @Test
    void doesTheSubmittedEmailHaveCorrectFormat() throws Exception {
        PersonDTO emailMissingLocalPart = new PersonDTO(1L, "Clara", "Eklund", "202203323434", "@kth.com", "123", "claraek");
        PersonDTO emailMissingAtSign = new PersonDTO(1L, "Anders", "Dean", "202012123454", "andersbokth.com", "123", "anders12");
        PersonDTO emailMissingDomainPart = new PersonDTO(1L, "Karin", "Elbert", "200304056787", "karinelb@", "123", "karin20");
        PersonDTO emailMissingLocalAndAtSign = new PersonDTO(1L, "Fatima", "Akbari", "200011118989", "kth.se", "123", "fatimakbari");
        PersonDTO emailMissingLocalPartAndDomainPart =  new PersonDTO(1L, "Elira", "Ahlborg", "200109088687", "@", "123", "eliraa");
        PersonDTO emailMissingAtSignAndDomainPart = new PersonDTO(1L, "Andreas", "Linder", "200112124565", "andreas", "123", "test");
        PersonDTO correctEmailFormat =new PersonDTO(1L, "Dannie", "Kvist", "200103034989", "dankvist@kth.com", "123", "dankvistt");

        //User attempting to submit a registration where the email is missing a local part
        ResponseEntity<Object> firstUserRegistration = registerController.registration(emailMissingLocalPart, null);
        assertEquals(HttpStatus.BAD_REQUEST, firstUserRegistration.getStatusCode());

        //User attempting to submit a registration where the email is missing an at sign
        ResponseEntity<Object> secondUserRegistration = registerController.registration(emailMissingAtSign, null);
        assertEquals(HttpStatus.BAD_REQUEST, secondUserRegistration.getStatusCode());

        //User attempting to submit a registration where the email is missing a domain part
        ResponseEntity<Object> thirdUserRegistration = registerController.registration(emailMissingDomainPart, null);
        assertEquals(HttpStatus.BAD_REQUEST, thirdUserRegistration.getStatusCode());

        //User attempting to submit a registration where the email is missing a local part and atsign
        ResponseEntity<Object> fourthUserRegistration = registerController.registration(emailMissingLocalAndAtSign, null);
        assertEquals(HttpStatus.BAD_REQUEST, fourthUserRegistration.getStatusCode());

        //User attempting to submit a registration where the email is missing a local part and domain part
        ResponseEntity<Object> fifthUserRegistration = registerController.registration(emailMissingLocalPartAndDomainPart, null);
        assertEquals(HttpStatus.BAD_REQUEST, fifthUserRegistration.getStatusCode());

        //User attempting to submit a registration where the email is missing atsign and domain part
        ResponseEntity<Object> sixthUserRegistration = registerController.registration(emailMissingAtSignAndDomainPart, null);
        assertEquals(HttpStatus.BAD_REQUEST, sixthUserRegistration.getStatusCode());

        //User registering with a correct email format
        ResponseEntity<Object> seventhUserRegistration = registerController.registration(correctEmailFormat, null);
        assertEquals(HttpStatus.OK, seventhUserRegistration.getStatusCode());


    }
}