package com.example.registerservice.controller;


import com.example.registerservice.model.dto.ErrorDTO;
import com.example.registerservice.model.dto.PersonDTO;
import com.example.registerservice.service.PersonService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Registration controller handles HTTP requests and
 * presents different JSON objects with HTTP statuses based on
 * user-input during registration.
 */
@Controller
public class RegisterController {

    /**
     * Logger to log events in the Controller layer.
     */
    private static final Logger logger = LogManager.getLogger(RegisterController.class);

    /**
     * An instance of PersonService handling business
     * logic specific to person related operations.
     */
    private final PersonService personService;

    /**
     * The constructor for RegisterController
     *
     * @param personService is the service responsible for the business logic
     *                      specific to person related operations.
     */
    RegisterController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Method containing validation method calls and returns different
     * JSON objects with corresponding HTTP
     * statuses depending on user input.
     *
     * @param personDTO Data transfer object representing users submitted information.
     *                  {@code @ResponseBody} writes the return type to HTTP response body.
     * @return HTTP status and an empty LinkedMultiValueMap as the body.
     */
    @PostMapping(value = "/api/register", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Object> registration(@RequestBody PersonDTO personDTO, HttpServletRequest request) {
        // IP of the person attempting to register
        String IP = request.getRemoteAddr();

        // Error messages based on users input.
        String emptyFieldErrorMessage = personService.checkEmptyRegistrationFields(personDTO);
        String duplicateFieldErrorMessage = personService.checkRegistrationDuplicate(personDTO);
        String emailFormatErrorMessage = personService.checkEmailFormat(personDTO);

        // Validation process based on if user submitted information with missing field/s
        // Or if user is already registered and is submitting data present in the database
        // Or if the email format is wrong, for example missing any of the 3: (local-part)(@)(domain-part)
        if (emptyFieldErrorMessage != null) {
            logger.warn("Registration failed, person with IP: {} has one/more required fields left empty " + " name: {} surname: {} pnr: {} email: {} password: {} username {} ", IP, personDTO.getName(), personDTO.getSurname(), personDTO.getPnr(), personDTO.getEmail(), personDTO.getPassword(), personDTO.getUsername());
            return new ResponseEntity<>(new ErrorDTO(emptyFieldErrorMessage), HttpStatus.BAD_REQUEST);
        } else if (duplicateFieldErrorMessage != null) {

            if ("EMAIL_TAKEN".equals(emailFormatErrorMessage)) {
                logger.warn("Person with IP: {} failed registration due to taken email: {}", IP, personDTO.getEmail());
            } else if ("PNR_TAKEN".equals(emailFormatErrorMessage)) {
                logger.warn("Person with IP: {} failed registration due to taken pnr: {}", IP, personDTO.getPnr());
            } else if ("USERNAME_TAKEN".equals(emailFormatErrorMessage)) {
                logger.warn("Person with IP: {} failed registration due to taken username: {}", IP, personDTO.getUsername());
            }

            return new ResponseEntity<>(new ErrorDTO(duplicateFieldErrorMessage), HttpStatus.BAD_REQUEST);
        } else if (!("CORRECT_EMAIL".equals(emailFormatErrorMessage))) {
            logger.warn("Person with IP: {} failed registration due to invalid email format: {} ", IP, personDTO.getEmail());
            return new ResponseEntity<>(new ErrorDTO(emailFormatErrorMessage), HttpStatus.BAD_REQUEST);
        }
        // User is saved to the database if the validation process is passed with no errors.
        personService.saveApplicant(personDTO);
        logger.info("Registration successful for " + "person with IP: {} name: {} surname: {} pnr: {} email: {} password: {} username {} ", IP, personDTO.getName(), personDTO.getSurname(), personDTO.getPnr(), personDTO.getEmail(), personDTO.getPassword(), personDTO.getUsername());
        return new ResponseEntity<>(new LinkedMultiValueMap<>(), HttpStatus.OK);
    }
}
