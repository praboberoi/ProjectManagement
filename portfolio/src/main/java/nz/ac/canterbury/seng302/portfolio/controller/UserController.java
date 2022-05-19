package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;

import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for the account page
 */
@Controller
public class UserController {

    private final UserAccountClientService userAccountClientService;
    @Value("${apiPrefix}") private String apiPrefix;

    public UserController (UserAccountClientService userAccountClientService) {
        this.userAccountClientService = userAccountClientService;
    }

    /**
     * Get method for users personal page
     * @param principal Authentication information containing user info
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Account html page
     */
    @GetMapping("/users")
    public String users(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {
        model.addAttribute("user", userAccountClientService.getUser(principal));
        model.addAttribute("apiPrefix", apiPrefix);
        return "userList";
    }
}
