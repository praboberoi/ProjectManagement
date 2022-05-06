package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.List;
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
        this.addAttributesToModel(principal, model);
        return "account";
    }

    /**
     * A function that returns the time passed since the creation date (usually of a user)
     * @param creationDate the date at which some entity was created
     * @return A string containing the time passed since the creation date
     */
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

    /**
     * A mapping to a get request to edit the user, which returns the current details of the user to be edited
     * @param principal Authentication information containing user info
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Html account editing page
     */
    @GetMapping("/editAccount")
    public String showNewForm(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {
        this.addAttributesToModel(principal, model);

        return "editAccount";
    }


    /**
     * The mapping for a Post request relating to editing a user
     * @param principal  Authentication information containing user info
     * @param firstName The first name of the user
     * @param lastName The last name of the user
     * @param nickname The nickname of the user
     * @param bio The bio of the user
     * @param pronouns The pronouns of the user
     * @param email The email of the user
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Html account editing page
     */
    @PostMapping("/editAccount")
    public String editUser(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String nickname,
            @RequestParam String bio,
            @RequestParam String pronouns,
            @RequestParam String email,
            Model model,
            RedirectAttributes ra
    ) {
        Integer userId = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst().map(ClaimDTO::getValue).orElse("-1"));
        EditUserResponse idpResponse = userAccountClientService.edit(userId, firstName, lastName, nickname,
                bio,
                pronouns,
                email);

        addAttributesToModel(principal, model);
        if (idpResponse.getIsSuccess()) {
            String msgString;
            msgString = String.format("Successfully updated details");
            ra.addFlashAttribute("messageSuccess", msgString);
            return "redirect:account";
        }
        List<ValidationError> validationErrors = idpResponse.getValidationErrorsList();
        validationErrors.stream().forEach(error -> model.addAttribute(error.getFieldName(), error.getErrorText()));
        
        return "editAccount";
    }

    private void addAttributesToModel(AuthState principal, Model model) {
        UserResponse idpResponse = userAccountClientService.getUser(principal);

        User user = new User(idpResponse);
        if (user.getProfileImagePath() == null) {
            user.setProfileImagePath("portfolio/resources/static/icons/user.png");
        } else {
            user.setProfileImagePath("portfolio/resources/cachedProfilePicture/" + user.getProfileImagePath());
        }
        model.addAttribute("user", user);
        model.addAttribute("roles", user.getRoles().stream().map(UserRole::name).collect(Collectors.joining(",")).toLowerCase());

        // Convert Date into LocalDate
        LocalDate creationDate = user.getDateCreated()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        model.addAttribute("creationDate",
                creationDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
        model.addAttribute("timePassed", getTimePassed(creationDate));
    }
}
