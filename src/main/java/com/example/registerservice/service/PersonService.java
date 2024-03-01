package com.example.registerservice.service;

import com.example.registerservice.model.Person;
import com.example.registerservice.model.dto.PersonDTO;
import com.example.registerservice.repository.PersonRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PersonService is a service class meant to handle the business-logic
 * specific to person-related operations.
 */
@Service
public class PersonService {

    /**
     * Logger to log events for passing the business-logic during registration attempts.
     */
    private static final Logger logger = LogManager.getLogger(PersonService.class);

    /**
     * encoder used to encode passwords before they are inserted into the database.
     */
    private final PasswordEncoder encoder;

    /**
     * personRepository is a repository that handles data retrieval/modification operations.
     */
    private final PersonRepository personRepository;

    /**
     * Constructor for the personService class.
     * {@code @Autowired} provides automatic dependency injection.
     *
     * @param encoder          is responsible for encoding passwords before
     *                         they are inserted into the database.
     * @param personRepository handles handles data retrieval/modification operations.
     */
    @Autowired
    public PersonService(PasswordEncoder encoder, PersonRepository personRepository) {
        this.encoder = encoder;
        this.personRepository = personRepository;
    }

    /**
     * Saves a person when they have correctly registered/passed
     * validation checks to the application.
     * {@code @Transactional} ensures application is saved to the
     * database only if the transaction is successful.
     *
     * @param personDTO Data transfer object
     *                  representing users submitted information.
     */
    @Transactional
    public void saveApplicant(PersonDTO personDTO) {
        String hashedPassword = encoder.encode(personDTO.getPassword());
        Person person = Person.builder().name(personDTO.getName()).surname(personDTO.getSurname()).pnr(personDTO.getPnr()).email(personDTO.getEmail()).password(hashedPassword).role_id(2).username(personDTO.getUsername()).build();
        personRepository.save(person);
        logger.debug("Newly registered person with username: {}", personDTO.getUsername());
    }

    /**
     * Checks if the username submitted during registration is
     * already taken by another user in the database.
     *
     * @param username that is compared to other usernames in
     *                 the database to check for risk of duplication.
     * @return true if no user was found to have the same username.
     */
    public boolean usernameAvailable(String username) {
        boolean usernameAvailable = personRepository.findByUsername(username) == null;
        logger.debug("Checking if username: {} is taken: {}", username, usernameAvailable);
        return usernameAvailable;
    }

    /**
     * Checks if the email submitted during registration
     * is already taken by another user in the database.
     *
     * @param email that is compared to other email in
     *              the database to check for risk of duplication.
     * @return true if no user was found to have the same email.
     */
    private boolean emailAvailable(String email) {
        boolean emailAvailable = personRepository.findByEmail(email) == null;
        logger.debug("Checking if email: {} is taken: {}", email, emailAvailable);
        return emailAvailable;

    }

    /**
     * Checks if the social security numbers submitted during
     * registration is already taken by another user in the database.
     *
     * @param pnr that is compared to other social security
     *            numbers in the database to check for risk of duplication.
     * @return true if no user was found to have the same social security number.
     */
    private boolean pnrAvailable(String pnr) {
        boolean pnrAvailable = personRepository.findByPnr(pnr) == null;
        logger.debug("Checking if pnr: {} is taken: {}", pnr, pnrAvailable);
        return pnrAvailable;
    }

    /**
     * Checks if the email submitted during registration has the correct
     * format containing "local-part@domain-part".
     *
     * @param checkEmail is the submitted email that passes logic checking if the email has
     *                   a local part, @ and a domain part.
     * @return true if the email has the correct format.
     */
    public boolean emailFormatValidation(String checkEmail) {
        if (!checkEmail.contains("@")) {
            return false;
        }

        int checkForAtSign = checkEmail.split("@").length;
        return checkForAtSign == 2 && !checkEmail.split("@")[0].isEmpty() && !checkEmail.split("@")[1].isEmpty();
    }

    /**
     * Checks if the user attempting to register is submitting a username,
     * social security number or an email that is already
     * taken by an older user.
     *
     * @param personDTO Data transfer object representing users
     *                  submitted information.
     * @return a unique error message if the username is taken,
     * email is taken or
     * the social security number is taken.
     */
    public String checkRegistrationDuplicate(PersonDTO personDTO) {
        String checkUsername = personDTO.getUsername();
        String checkPnr = personDTO.getPnr();
        String checkEmail = personDTO.getEmail();

        if (!usernameAvailable(checkUsername)) {
            return "USERNAME_TAKEN";
        } else if (!emailAvailable(checkEmail)) {
            return "EMAIL_TAKEN";
        } else if (!pnrAvailable(checkPnr)) {
            return "PNR_TAKEN";
        }
        return null;
    }

    /**
     * Provides logic for checking if any of the fields required during
     * registration is left empty when submitted.
     *
     * @param personDTO Data transfer object representing users submitted information.
     * @return a unique error message specific to each field that might
     * be missing when submitted.
     */
    public String checkEmptyRegistrationFields(PersonDTO personDTO) {

        String checkName = personDTO.getName();
        String checkSurname = personDTO.getSurname();
        String checkPnr = personDTO.getPnr();
        String checkPassword = personDTO.getPassword();
        String checkUsername = personDTO.getUsername();
        String checkEmail = personDTO.getEmail();

        if ("".equals(checkName)) {
            return "MISSING_PARAMETERS";
        } else if ("".equals(checkSurname)) {
            return "MISSING_PARAMETERS";
        } else if ("".equals(checkPnr)) {
            return "MISSING_PARAMETERS";
        } else if ("".equals(checkPassword)) {
            return "MISSING_PARAMETERS";
        } else if ("".equals(checkUsername)) {
            return "MISSING_PARAMETERS";
        } else if ("".equals(checkEmail)) {
            return "MISSING_PARAMETERS";
        }
        return null;
    }

    /**
     * Checks the email format submitted during registration.
     *
     * @param personDTO Data transfer object representing users submitted information.
     * @return an error message if the email format is wrong, and a
     * correct email format message if it is right.
     */
    public String checkEmailFormat(PersonDTO personDTO) {
        String checkEmail = personDTO.getEmail();
        if (!emailFormatValidation(checkEmail)) {
            return "INVALID_EMAIL";
        }
        return "CORRECT_EMAIL";
    }
}
