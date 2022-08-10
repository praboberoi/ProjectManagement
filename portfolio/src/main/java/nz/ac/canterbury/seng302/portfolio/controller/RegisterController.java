package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.CookieUtil;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.util.ValidationError;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

    @Autowired
    private AuthenticateClientService authenticateClientService;

    @Value("${apiPrefix}") private String apiPrefix;

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
     * Attempts to register a user, creates a cookie and redirects to dashboard if successful
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
    @PostMapping(path="/register")
    public String createUser(
        HttpServletRequest request,
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
                AuthenticateResponse loginReply = authenticateClientService.authenticate(username, password);
                if (loginReply.getSuccess()) {
                    var domain = request.getHeader("host");
                    CookieUtil.create(
                        response,
                        "lens-session-token",
                            loginReply.getToken(),
                        true,
                        5 * 60 * 60, // Expires in 5 hours
                        domain.startsWith("localhost") ? null : domain
                    );
                    return "redirect:/dashboard";
                }
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
