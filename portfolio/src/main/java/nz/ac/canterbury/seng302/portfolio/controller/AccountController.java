package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Controller for the account page
 */
@Controller
public class AccountController {

    private UserAccountClientService userAccountClientService;

    @Value("${apiPrefix}")
    private String apiPrefix;

    private SimpMessagingTemplate template;

    private static final String EDIT_ACCOUNT_PAGE = "editAccount";
    private static final String ACCOUNT_PAGE = "account";

    public AccountController (UserAccountClientService userAccountClientService, SimpMessagingTemplate template) {
        this.userAccountClientService = userAccountClientService;
        this.template = template;
    }

    /**
     * Get method for users personal page
     * @param principal Authentication information containing user info
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Account html page
     */
    @GetMapping(path="/account")
    public String account(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {
        this.addAttributesToModel(principal, model);
        return ACCOUNT_PAGE;
    }

    /**
     * This method returns a html fragment containing the list of roles a user has
     * @param principal The authentication information containing user info
     * @return A html fragment containing the roles
     */
    @GetMapping(path="/account/roles")
    public ModelAndView roles(@AuthenticationPrincipal AuthState principal) {
        UserResponse idpResponse = userAccountClientService.getUser(principal);

        User user = new User(idpResponse);

        StringBuilder roles = new StringBuilder();

        ModelAndView mv = new ModelAndView("accountFragments::rolesList");

        user.getRoles().forEach(role -> roles.append(formatRoleName(role.toString() + ", ")));
        mv.addObject("roles",
                !user.getRoles().isEmpty() ? roles.substring(0, roles.length() - 2): user.getRoles());
        return mv;
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
    @GetMapping(path="/editAccount")
    public String showNewForm(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {
        this.addAttributesToModel(principal, model);

        return EDIT_ACCOUNT_PAGE;
    }



    /**
     * The mapping for a Post request relating to editing a user
     * @param principal  Authentication information containing user info
     * @param multipartFile  The image file of the user
     * @param firstName The first name of the user
     * @param lastName The last name of the user
     * @param nickname The nickname of the user
     * @param bio The bio of the user
     * @param pronouns The pronouns of the user
     * @param email The email of the user
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param deleteImage Boolean to run the delete image functionality or not
     * @return Html account editing page
     */
    @PostMapping(path="/editAccount")
    public String editUser(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam("image")MultipartFile multipartFile,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String nickname,
            @RequestParam String bio,
            @RequestParam String pronouns,
            @RequestParam String email,
            boolean deleteImage,
            Model model,
            RedirectAttributes ra
    ) throws IOException {
        int userId = PrincipalUtils.getUserId(principal);
        EditUserResponse idpResponse = userAccountClientService.edit(userId, firstName, lastName, nickname,
            bio,
            pronouns,
            email);
        if (!multipartFile.isEmpty()) {
            String extension = multipartFile.getContentType();
            ArrayList<String> acceptedFileTypes = new ArrayList<>(Arrays.asList(MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE,MediaType.IMAGE_PNG_VALUE));
            if (acceptedFileTypes.contains(extension)) {
                if (MediaType.IMAGE_GIF_VALUE.equals(multipartFile.getContentType())) {
                    extension = "gif";
                } else if (MediaType.IMAGE_PNG_VALUE.equals(multipartFile.getContentType())) {
                    extension = "png";
                } else {
                    extension = "jpeg";
                }
                userAccountClientService.uploadImage(userId, extension, multipartFile);
            } else {
                String msgString;
                msgString = "File must be an image of type jpg, jpeg or png";
                ra.addFlashAttribute("messageDanger", msgString);
                return EDIT_ACCOUNT_PAGE;
            }
        }
        if (deleteImage) deleteUserProfilePhoto(principal);

        addAttributesToModel(principal, model);
        if (idpResponse.getIsSuccess()) {
            String msgString;
            notifyUserInfoChange(userId);
            msgString = "Successfully updated details";
            ra.addFlashAttribute("messageSuccess", msgString);
            return "redirect:" + ACCOUNT_PAGE;
        }
        List<ValidationError> validationErrors = idpResponse.getValidationErrorsList();
        validationErrors.stream().forEach(error -> model.addAttribute(error.getFieldName(), error.getErrorText()));

        return EDIT_ACCOUNT_PAGE;
    }

    /**
     * Sends an update message to all clients connected to the websocket
     * @param userId Id of the changed user
     */
    private void notifyUserInfoChange(int userId) {
        template.convertAndSend("/element/user/", userId);
    }


    /**
     * Add attributes to the model to be used with Thymeleaf
     * @param principal the current user
     * @param model the thymeleaf model
     */
    private void addAttributesToModel(AuthState principal, Model model) {
        UserResponse idpResponse = userAccountClientService.getUser(principal);

        User user = new User(idpResponse);

        StringBuilder roles = new StringBuilder();
        user.getRoles().forEach(role -> roles.append(formatRoleName(role.toString() + ", ")));
        model.addAttribute("roles",
                !user.getRoles().isEmpty() ? roles.substring(0, roles.length() - 2): user.getRoles());

        LocalDate creationDate = user.getDateCreated()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        model.addAttribute("creationDate",
                creationDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
        model.addAttribute("timePassed", getTimePassed(creationDate));
    }

    /**
     * Calls the delete profile image functionality in the UserAccountClientService
     * @param principal
     * @return Response status
     */
    public ResponseEntity<Long> deleteUserProfilePhoto(@AuthenticationPrincipal AuthState principal) {
        ClaimDTO id = principal.getClaims(2);
        int userId = Integer.parseInt(id.getValue());
        DeleteUserProfilePhotoResponse idpResponse = userAccountClientService.deleteUserProfilePhoto(userId);

        if (idpResponse.getIsSuccess()) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        if (("Could not find user").equals(idpResponse.getMessage())
                || ("Could not find a profile photo to delete").equals(idpResponse.getMessage())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Formats the roles of a user
     * @param name name of the role
     * @return The name of the role correctly formatted
     */
    static String formatRoleName(String name) {
        if (name.contains("_")) {
            String[] role = name.split("_");
            return formatRoleName(role[0]) + " " + formatRoleName(role[1]);
        } else
            return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

}
