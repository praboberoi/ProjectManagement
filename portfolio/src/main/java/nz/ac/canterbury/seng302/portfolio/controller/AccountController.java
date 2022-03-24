package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.GreeterClientService;
//import nz.ac.canterbury.seng302.shared.enums.Roles;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;

import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
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

        UserResponse idpResponse = userAccountClientService.getUser(principal);

        User user = new User(idpResponse);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("pronouns", user.getPersonalPronouns());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("bio", user.getBio());
        model.addAttribute("roles", user.getRoles().stream().map(UserRole::name).collect(Collectors.joining(",")).toLowerCase());

        // Convert Date into LocalDate
        LocalDate creationDate = user.getDateCreated()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        model.addAttribute("creationDate",
                creationDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
        model.addAttribute("timePassed", getTimePassed(creationDate));

        return "account";
    }

    private String getTimePassed(LocalDate creationDate) {
        String timePassed = "(";
        LocalDate currentDate = new Date()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        Period period = Period.between(creationDate, currentDate);
        if (period.getYears() != 0) {
            if (period.getYears() == 1) {
                timePassed += period.getYears() + " Year, ";
            } else {
                timePassed += period.getYears() + " Years, ";
            }
        }

        if (period.getMonths() != 0 || period.getYears() != 0) {
            if (period.getMonths() == 1) {
                timePassed += period.getMonths() + " Month";
            } else {
                timePassed += period.getMonths() + " Months";
            }
        } else {
            if (period.getDays() == 1) {
                timePassed += period.getDays() + " Day";
            } else {
                timePassed += period.getDays() + " Days";
            }
        }

        return timePassed + ")";
    }




    @GetMapping("/editAccount")
    public String showNewForm(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(name="userId", required=false) String favouriteColour,
            Model model
    ) {
        UserResponse idpResponse = userAccountClientService.getUser(principal);

        User user = new User(idpResponse);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("pronouns", user.getPersonalPronouns());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("bio", user.getBio());
        model.addAttribute("roles", user.getRoles().stream().map(UserRole::name).collect(Collectors.joining(",")).toLowerCase());
        return "editAccount";
    }


}
