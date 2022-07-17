package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;

import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the registration page
 */
@Controller
public class RegisterController {

    private final UserAccountClientService userAccountClientService;
    @Value("${apiPrefix}") private String apiPrefix;
    
    /**
    * Adds common model elements used by all controller methods.
    */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("apiPrefix", apiPrefix);
    }

    public RegisterController(UserAccountClientService userAccountClientService) {
        this.userAccountClientService = userAccountClientService;
    }

    /**
     * Get message for empty registration page
     * @param request HTTP request sent to this endpoint
     * @param response HTTP response that will be returned by this endpoint
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Registration html page
     */
    @RequestMapping(path="/register", method = RequestMethod.GET)
    public String register(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        return "register";
    }

    /**
     * Attempts to register a user and redirects to login if successful
     * @param response HTTP response that will be returned by this endpoint
     * @param username New user's username (required)
     * @param firstName New user's first name (required)
     * @param lastName New user's last name (required)
     * @param nickname New user's nickname
     * @param bio New user's bio
     * @param pronouns New user's pronouns
     * @param email New user's email (required)
     * @param password New user's password (required)
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return HTML page corresponding to correct handling
     */
    @RequestMapping(path="/register", method = RequestMethod.POST)
    public String createUser(
            HttpServletResponse response,
            @RequestParam String username,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String nickname,
            @RequestParam String bio,
            @RequestParam String pronouns,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model
    ) {
        UserRegisterResponse idpResponse = null;
        List<ValidationError> validationErrors = new ArrayList<>();
        if (password.equals(confirmPassword)) {
            idpResponse = userAccountClientService.register(username, firstName, lastName, nickname, bio, pronouns, email, password);
            if (idpResponse.getIsSuccess()) {
                        return "redirect:/login";
            } else {
                validationErrors = idpResponse.getValidationErrorsList();
            }

        } else {
            validationErrors.add(ValidationError.newBuilder()
                    .setFieldName("confirmPasswordError")
                    .setErrorText("Passwords don't match")
                    .build());
        }

        validationErrors.stream().forEach(error -> model.addAttribute(error.getFieldName(), error.getErrorText()));

        model.addAttribute("username", username);

        model.addAttribute("firstName", firstName);

        model.addAttribute("lastName", lastName);

        model.addAttribute("nickname", nickname);

        model.addAttribute("bio", bio);

        model.addAttribute("personalPronouns", pronouns);

        model.addAttribute("email", email);
        return "register";
    }

}
