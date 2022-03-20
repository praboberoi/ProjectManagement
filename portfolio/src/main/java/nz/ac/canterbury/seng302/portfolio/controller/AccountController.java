package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Controller
public class AccountController {

    @Autowired
    private UserAccountClientService userAccountClientService;

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
        model.addAttribute("username", user.username);
        model.addAttribute("firstName", user.firstName);
        model.addAttribute("lastName", user.lastName);
        model.addAttribute("nickname", user.nickname);
        model.addAttribute("pronouns", user.pronouns);
        model.addAttribute("email", user.email);
        model.addAttribute("bio", user.bio);
        model.addAttribute("roles", user.roles.stream().map(UserRole::name).collect(Collectors.joining(",")).toLowerCase());
        return "account";
    }

}
