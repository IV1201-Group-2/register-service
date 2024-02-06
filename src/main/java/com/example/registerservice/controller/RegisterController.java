package com.example.registerservice.controller;


import com.example.registerservice.model.dto.PersonDTO;
import com.example.registerservice.service.PersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.registerservice.model.dto.ErrorDTO;


/**
 * Registration controller handles HTTP requests and
 * presents different JSON objects with HTTP statuses based on
 *                          user-input during registration.
 */
@Controller
public class RegisterController {

    private final PersonService personService;

    /**
     * The constructor for RegisterController
     * @param personService is the service responsible for the business logic
     *                      specific to person related operations.
     */
    RegisterController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Method renders the registration HTML form.
     * @return Renders the name of the HTMl page for the user.
     */
    @GetMapping("/api/register")
    public String presentSignUpHTML() {
        return "register";
    }

    /**
     * Method containing validation method calls and returns different
     *                      JSON objects with corresponding HTTP
     *                      statuses depending on user input.
     * @param personDTO Data transfer object representing users submitted information.
     * @param model Model used to pass data to the view based on MVC layer.
     * {@code @ResponseBody}
     * @return HTTP status and no header.
     */
    @PostMapping("/api/register")
    @ResponseBody
    public ResponseEntity<Object> registration(@ModelAttribute PersonDTO personDTO, Model model) {
        // Error messages based on users input.
        String emptyFieldErrorMessage = personService.checkEmptyRegistrationFields(personDTO);
        String duplicateFieldErrorMessage = personService.checkRegistrationDuplicate(personDTO);
        String emailFormatErrorMessage = personService.checkEmailFormat(personDTO);
      
        // Validation process based on if user submitted information with missing field/s
        // Or if user is already registered and is submitting data present in the database
        // Or if the email format is wrong, for example missing any of the 3: (local-part)(@)(domain-part).

        if (emptyFieldErrorMessage != null) {
            return new ResponseEntity<>(new ErrorDTO(emptyFieldErrorMessage), HttpStatus.BAD_REQUEST);
        } else if (duplicateFieldErrorMessage != null) {
            return new ResponseEntity<>(new ErrorDTO(duplicateFieldErrorMessage), HttpStatus.BAD_REQUEST);
        } else if (!("CORRECT_EMAIL".equals(emailFormatErrorMessage))) {
            return new ResponseEntity<>(new ErrorDTO(emailFormatErrorMessage), HttpStatus.BAD_REQUEST);
        }
        // User is saved to the database if the validation process is passed with no errors.
        personService.saveApplicant(personDTO);
        return new ResponseEntity<>(null, HttpStatus.OK);

    }
}
