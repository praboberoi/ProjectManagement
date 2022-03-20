package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.stream.Collectors;

/**
 * Controller for the account page
 */
@Controller
public class AccountController {

    private final UserAccountClientService userAccountClientService;

    public AccountController (UserAccountClientService userAccountClientService) {
        this.userAccountClientService = userAccountClientService;
    }

    /**
     * Get method for users personal page
     * @param principal Authentication information containing user info
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Account html page
     */
    @GetMapping("/account")
    public String account(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {
        int id = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        UserResponse idpResponse = userAccountClientService.getUser(id);

        User user = new User(idpResponse);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("pronouns", user.getPersonalPronouns());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("bio", user.getBio());
        model.addAttribute("roles", user.getRoles().stream().map(UserRole::name).collect(Collectors.joining(",")).toLowerCase());
        return "account";
    }

}
