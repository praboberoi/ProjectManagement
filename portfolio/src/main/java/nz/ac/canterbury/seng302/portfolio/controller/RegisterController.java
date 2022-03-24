package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;

import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for the registration page
 */
@Controller
public class RegisterController {

    private final UserAccountClientService userAccountClientService;

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
    @GetMapping("/register")
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
    @PostMapping("/register")
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
            Model model
    ) {
        UserRegisterResponse idpResponse = userAccountClientService.register(username, firstName, lastName, nickname, bio, pronouns, email, password);
        if (idpResponse.getIsSuccess()) {
            return "redirect:/login";
        } else {
            System.out.println("HELP");
            model.addAttribute("error", idpResponse.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("firstName", firstName);
            model.addAttribute("lastName", lastName);
            model.addAttribute("nickname", nickname);
            model.addAttribute("bio", bio);
            model.addAttribute("pronouns", pronouns);
            model.addAttribute("email", email);
            return "register";
        }
    }

}
