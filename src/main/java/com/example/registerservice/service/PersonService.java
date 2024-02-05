package com.example.registerservice.service;

import com.example.registerservice.model.Person;
import com.example.registerservice.model.dto.PersonDTO;
import com.example.registerservice.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PersonService is a service class meant to handle the business-logic specific to person-related operations.
 */
@Service
public class PersonService {

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
     * @param encoder is responsible for encoding passwords before they are inserted into the database.
     * @param personRepository handles handles data retrieval/modification operations.
     */
    @Autowired
    public PersonService(PasswordEncoder encoder, PersonRepository personRepository) {
        this.encoder = encoder;
        this.personRepository = personRepository;
    }

    /**
     * Saves a person when they have correctly registered/passed validation checks to the application.
     * {@code @Transactional} ensures application is saved to the database only if the transaction is successful.
     * @param personDTO Data transfer object representing users submitted information.
     */
    @Transactional
    public void saveApplicant(PersonDTO personDTO) {
        String hashedPassword = encoder.encode(personDTO.getPassword());
        Person person = Person.builder()
                .name(personDTO.getName())
                .surname(personDTO.getSurname())
                .pnr(personDTO.getPnr())
                .email(personDTO.getEmail())
                .password(hashedPassword)
                .role_id(2)
                .username(personDTO.getUsername())
                .build();
        personRepository.save(person);
    }

    /**
     * Checks if the username submitted during registration is already taken by another user in the database.
     * @param username that is compared to other usernames in the database to check for risk of duplication.
     * @return true if no user was found to have the same username.
     */
    public boolean usernameAvailable(String username) {
        return personRepository.findByUsername(username) == null;

    }

    /**
     * Checks if the email submitted during registration is already taken by another user in the database.
     * @param email that is compared to other email in the database to check for risk of duplication.
     * @return true if no user was found to have the same email.
     */
    private boolean emailAvailable(String email) {
        return personRepository.findByEmail(email) == null;
    }

    /**
     * Checks if the social security numbers submitted during registration is already taken by another user in the database.
     * @param pnr that is compared to other social security numbers in the database to check for risk of duplication.
     * @return true if no user was found to have the same social security number.
     */
    private boolean pnrAvailable(String pnr) {
        return personRepository.findByPnr(pnr) == null;
    }

    /**
     * Checks if the email submitted during registration has the correct format containing
     * "local-part@domain-part".
     * @param checkEmail is the submitted email that passes logic checking if the email has
     *                   a local part, @ and a domain part.
     * @return true if the email has the correct format.
     */
    private boolean emailFormatValidation(String checkEmail) {
        if (!checkEmail.contains("@")) {
            return false;
        }
        int checkForAtSign = checkEmail.split("@").length;
        return checkForAtSign == 2 && !checkEmail.split("@")[0].isEmpty() && !checkEmail.split("@")[1].isEmpty();
    }

    /**
     * Checks if the user attempting to register is submitting a username,
     * social security number or an email that is already taken by an older user.
     * @param personDTO Data transfer object representing users submitted information.
     * @return a unique error message if the username is taken, email is taken or
     *         the social security number is taken.
     */
    public String checkRegistrationDuplicate(PersonDTO personDTO) {
        String checkUsername = personDTO.getUsername();
        String checkPnr = personDTO.getPnr();
        String checkEmail = personDTO.getEmail();

        if (!usernameAvailable(checkUsername)) {
            return "This username is already taken";
        } else if (!emailAvailable(checkEmail)) {
            return "This email is already taken";
        } else if (!pnrAvailable(checkPnr)) {
            return "This Social Security number is already taken";
        }
        return null;
    }

    /**
     * Provides logic for checking if any of the fields required during registration is left empty when submitted.
     * @param personDTO Data transfer object representing users submitted information.
     * @return a unique error message specific to each field that might be missing when submitted.
     */
    public String checEmptyRegistrationFields(PersonDTO personDTO) {

        String checkName = personDTO.getName();
        String checkSurname = personDTO.getSurname();
        String checkPnr = personDTO.getPnr();
        String checkPassword = personDTO.getPassword();
        String checkUsername = personDTO.getUsername();
        String checkEmail = personDTO.getEmail();

        if ("".equals(checkName)) {
            return "Name field is empty";
        } else if ("".equals(checkSurname)) {
            return "Surname field is empty";
        } else if ("".equals(checkPnr)) {
            return "Social Security field is empty";
        } else if ("".equals(checkPassword)) {
            return "Password field is empty";
        } else if ("".equals(checkUsername)) {
            return "Username field is empty";
        } else if ("".equals(checkEmail)) {
            return "Email field is empty";
        }
        return null;
    }

    /**
     * Checks the email format submitted during registration.
     * @param personDTO Data transfer object representing users submitted information.
     * @return an error message if the email format is wrong, and a correct email format message if it is right.
     */
    public String checkEmailFormat(PersonDTO personDTO) {
        String checkEmail = personDTO.getEmail();

        if (!emailFormatValidation(checkEmail)) {
            return "Please enter a valid email format";
        }
        return "Email has the correct format";
    }

}
