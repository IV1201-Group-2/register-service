package com.example.registerservice;

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

import static org.junit.jupiter.api.Assertions.*;

/**
 * RegisterControllerIntegrationTest uses TestContainers and demonstrates integration testing.
 * The tests included are:
 * 1. Checking if a user is registering with a username, social security number or email that
 *              already exists in the database.
 * 2. Checking if the user submitted a registration where one of the required fields are missing.
 * 3.           Checking if the user submitted a correct email format.
 * {@code @Transactional} ensures application is saved to the database only if
 *              the transaction is successful.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Transactional
public class PersonRegistrationIntegrationTest {

    /**
     * Mocking a PostgreSQL database for the integration tests.
     * The database is configured with a specific, name, username and
     *          password as well as the latest postgreSQL version.
     * {@code @Container} sets the field as a TestContainer container.
     */
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("postgresg2")
            .withUsername("postgres")
            .withPassword("Qwerty123456!");

    /**
     * PersonService is an autowired instance contains business-logic for person-related operations.
     * {@code @Autowired} provides automatic dependency injection.
     */
    @Autowired
    private PersonService personService;

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
     * The test checks to see whether duplicate data is detected to make sure the same user
     *          is not registered more than once.
     */
    @Test
    void isApplicantAlreadyInDatabase() throws Exception {
        //New user registered with unique data fields submitted
        PersonDTO firstRegistration = new PersonDTO(1L, "Clara", "Eklund", "202203323434", "claraeklund@kth.com", "123", "claraek");
        personService.saveApplicant(firstRegistration);

        //User attempting to register with a username that is taken
        PersonDTO secondRegistration = new PersonDTO(1L, "Anders", "Dean", "202012123454", "andersbo@kth.com", "123", "claraek");
        String errorUsernameTaken = personService.checkRegistrationDuplicate(secondRegistration);
        assertNotNull(errorUsernameTaken);

        //User attempting to register with a Social security number that is taken
        PersonDTO thirdRegistration = new PersonDTO(1L, "Karin", "Elbert", "202203323434", "karinelb@kth.com", "123", "karin20");
        String errorPnrTaken = personService.checkRegistrationDuplicate(thirdRegistration);
        assertNotNull(errorPnrTaken);

        //User attempting to register with an email that is taken
        PersonDTO fourthRegistration = new PersonDTO(1L, "Fatima", "Akbari", "200011118989", "claraeklund@kth.com", "123", "fatimakbari");
        String errorEmailTaken = personService.checkRegistrationDuplicate(fourthRegistration);
        assertNotNull(errorEmailTaken);

    }

        /**
     * JUnit test that checks if an empty field is detected so that a registered
     *          user is not saved if any of the required fields are missing.
     */
    @Test
    void isAFieldMissingInRegistrationForm() {
        //User attempting to submit a registration where the name is missing
        PersonDTO personMissingName = new PersonDTO(1L, "", "Eklund", "202203323434", "claraeklund@kth.com", "123", "claraek");
        assertNotNull(personService.checkEmptyRegistrationFields(personMissingName));

        //User attempting to submit a registration where the surname is missing
        PersonDTO personMissingSurname = new PersonDTO(1L, "Anders", "", "202012123454", "andersbo@kth.com", "123", "anders12");
        assertNotNull(personService.checkEmptyRegistrationFields(personMissingSurname));

        //User attempting to submit a registration where the Social security number is missing
        PersonDTO personMissingPnr = new PersonDTO(1L, "Karin", "Elbert", "", "karinelb@kth.com", "123", "karin20");
        assertNotNull(personService.checkEmptyRegistrationFields(personMissingPnr));

        //User attempting to submit a registration where the email is missing
        PersonDTO personMissingEmail = new PersonDTO(1L, "Fatima", "Akbari", "200011118989", "", "123", "fatimakbari");
        assertNotNull(personService.checkEmptyRegistrationFields(personMissingEmail));

        //User attempting to submit a registration where the password is missing
        PersonDTO personMissingPassword = new PersonDTO(1L, "Elira", "Ahlborg", "200109088687", "elira21@kth.com", "", "eliraa");
        assertNotNull(personService.checkEmptyRegistrationFields(personMissingPassword));

        //User attempting to submit a registration where the username is missing
        PersonDTO personMissingUsername = new PersonDTO(1L, "Dannie", "Kvist", "200401012343", "dankvist@kth.com", "123", "");
        assertNotNull(personService.checkEmptyRegistrationFields(personMissingUsername));


    }

    /**
     * JUnit test that tests 7 different data transfer objects from user registration
     *          that contains different combinations of email formats.
     * For instance if the user misses any of the combinations of local-part@domain-part,
     *          an error message should be thrown.
     * Finally, the last data transfer object example contains a correct email format and
     *          checks to see that the correctEMailFormatMessage is returned when.
     */
    @Test
    void doesTheSubmittedEmailHaveCorrectFormat() throws Exception {
        PersonDTO emailMissingLocalPart = new PersonDTO(1L, "test", "", "00091738559", "@test.com", "123", "test");
        PersonDTO emailMissingAtSign = new PersonDTO(1L, "test", "", "00091738559", "testtest.com", "123", "test");
        PersonDTO emailMissingDomainPart = new PersonDTO(1L, "test", "", "00091738559", "test@", "123", "test");
        PersonDTO emailMissingLocalAndAtSign = new PersonDTO(1L, "test", "", "00091738559", "test.com", "123", "test");
        PersonDTO emailMissingLocalPartAndDomainPart = new PersonDTO(1L, "test", "", "00091738559", "@", "123", "test");
        PersonDTO emailMissingAtSignAndDomainPart = new PersonDTO(1L, "test", "", "00091738559", "test", "123", "test");
        PersonDTO correctEmailFormat = new PersonDTO(1L, "test", "", "00091738559", "test@test.com", "123", "test");

        //User attempting to submit a registration where the email is missing a local part
        assertNotEquals("", personService.checkEmailFormat(emailMissingLocalPart));
        //User attempting to submit a registration where the email is missing an at sign
        assertNotEquals("", personService.checkEmailFormat(emailMissingAtSign));
        //User attempting to submit a registration where the email is missing a domain part
        assertNotEquals("", personService.checkEmailFormat(emailMissingDomainPart));
        //User attempting to submit a registration where the email is missing a local part and atsign
        assertNotEquals("", personService.checkEmailFormat(emailMissingLocalAndAtSign));
        //User attempting to submit a registration where the email is missing a local part and domain part
        assertNotEquals("", personService.checkEmailFormat(emailMissingLocalPartAndDomainPart));
        //User attempting to submit a registration where the email is missing atsign and domain part
        assertNotEquals("", personService.checkEmailFormat(emailMissingAtSignAndDomainPart));
        //User registering with a correct email format
        assertEquals("CORRECT_EMAIL", personService.checkEmailFormat(correctEmailFormat));

    }
}