package com.example.registerservice.controller;

import com.example.registerservice.model.dto.PersonDTO;
import com.example.registerservice.service.PersonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Registration controller renders different endpoints based on user-input during registration.
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
     * Method containing validation method calls and rendering of different endpoints depending on user input.
     * @param personDTO Data transfer object representing users submitted information.
     * @param model Model used to pass data to the view based on MVC layer.
     * @return Renders the name of the HTMl page for the user.
     */
    @PostMapping("/api/register")
    public String registration(@ModelAttribute PersonDTO personDTO, Model model) {
        // Error messages based on users input.
        String emptyFieldErrorMessage = personService.checEmptyRegistrationFields(personDTO);
        String duplicateFieldErrorMessage = personService.checkRegistrationDuplicate(personDTO);
        String emailFormatErrorMessage = personService.checkEmailFormat(personDTO);
        // Validation process based on if user submitted information with missing field/s
        // Or if user is already registered and is submitting data present in the database
        // Or if the email format is wrong, for example missing any of the 3: (local-part)(@)(domain-part).
        if (emptyFieldErrorMessage != null) {
            model.addAttribute("errorMessage", emptyFieldErrorMessage);
            return "register";
        } else if (duplicateFieldErrorMessage != null) {
            model.addAttribute("errorMessage", duplicateFieldErrorMessage);
            return "register";
        } else if (!("Email has the correct format".equals(emailFormatErrorMessage))) {
            model.addAttribute("errorMessage", emailFormatErrorMessage);
            return "register";
        }
        // User is saved to the database if the validation process is passed with no errors.
        personService.saveApplicant(personDTO);
        model.addAttribute("message", "Successfully registered!");
        return "redirect:/api/registeredSuccessfully";

    }

    /**
     * Renders the registeredSuccessfully page if user registration was completed correctly.
     * @return Renders the name of the HTMl page for the user.
     */
    @GetMapping("/api/registeredSuccessfully")
    public String registeredSuccessfully() {
        return "registeredSuccessfully";
    }

}
