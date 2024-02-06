package com.example.registerservice;

import com.example.registerservice.model.dto.PersonDTO;
import com.example.registerservice.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
 * already exists in the database.
 * 2. Checking if the user submitted a registration where one of the required fields are missing.
 * 3. Checking if the user submitted a correct email format.
 * {@code @Transactional} ensures application is saved to the database only if
 *                      the transaction is successful.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Transactional
public class RegisterControllerIntegrationTest {

    /**
     * Mocking a PostgreSQL database for the integration tests.
     * The database is configured with a specific, name, username and
     * password as well as the latest postgreSQL version.
     * {@code @Container} sets the field as a TestContainer container.
     */
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("postgrestest")
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
     * The test checks to see whether duplicate data is detected to make sure the same user
     * is not registered more than once.
     */
    @Test
    void isApplicantAlreadyInDatabase() throws Exception {
        PersonDTO firstRegistration = new PersonDTO(1L, "test", "test", "00091738559", "test@test.com", "123", "test");
        personService.saveApplicant(firstRegistration);
        PersonDTO secondRegistration = new PersonDTO(2L, "test", "test", "00091738559", "test@test.com", "123", "test");
        String errorDuplicateUser = personService.checkRegistrationDuplicate(secondRegistration);
        assertNotNull(errorDuplicateUser);

        PersonDTO thirdRegistration = new PersonDTO(3L, "newTest", "newTest", "123456", "newTest@test.com", "123", "newTest");
        String newApplicationRegistered = personService.checkRegistrationDuplicate(thirdRegistration);
        assertNull(newApplicationRegistered);

    }

        /**
     * JUnit test that checks if an empty field is detected so that a registered
     * user is not saved if any of the required fields are missing.
     */
    @Test
    void isAFieldMissingInRegistrationForm() {
        PersonDTO person = new PersonDTO(1L, "test", "", "00091738559", "test@test.com", "123", "test");
        assertNotNull(personService.checkEmptyRegistrationFields(person));

    }

    /**
     * JUnit test that tests 7 different data transfer objects from user registration
     * that contains different combinations of email formats.
     * For instance if the user misses any of the combinations of local-part@domain-part,
     * an error message should be thrown.
     * Finally, the last data transfer object example contains a correct email format and
     * checks to see that the correctEMailFormatMessage is returned when.
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

        assertNotEquals("", personService.checkEmailFormat(emailMissingLocalPart));
        assertNotEquals("", personService.checkEmailFormat(emailMissingAtSign));
        assertNotEquals("", personService.checkEmailFormat(emailMissingDomainPart));
        assertNotEquals("", personService.checkEmailFormat(emailMissingLocalAndAtSign));
        assertNotEquals("", personService.checkEmailFormat(emailMissingLocalPartAndDomainPart));
        assertNotEquals("", personService.checkEmailFormat(emailMissingAtSignAndDomainPart));
        assertEquals("CORRECT_EMAIL", personService.checkEmailFormat(correctEmailFormat));
    }
}