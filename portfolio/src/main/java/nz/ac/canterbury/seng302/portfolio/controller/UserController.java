package nz.ac.canterbury.seng302.portfolio.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;

import nz.ac.canterbury.seng302.shared.identityprovider.UserRoleChangeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

/**
 * Controller for user related pages excluding the current
 */
@Controller
public class UserController {

    private final UserAccountClientService userAccountClientService;
    @Value("${apiPrefix}") private String apiPrefix;

    public UserController (UserAccountClientService userAccountClientService) {
        this.userAccountClientService = userAccountClientService;
    }

    @ModelAttribute
    public void addAttributes(@AuthenticationPrincipal AuthState principal, Model model) {
        model.addAttribute("adminOrTeacher", userAccountClientService.checkUserIsTeacherOrAdmin(principal));
        model.addAttribute("apiPrefix", apiPrefix);
    }

    /**
     * Get method for the first page of the list of users
     * @param principal Authentication information containing user info
     * @param mv Parameters sent to thymeleaf template to be rendered into HTML
     * @return The user list page
     */
    @GetMapping("/users")
    public ModelAndView users(
            @AuthenticationPrincipal AuthState principal,
            ModelAndView mv
    ) {
        int limit = 10;
        PaginatedUsersResponse response = userAccountClientService.getUsers(0, limit);
        List<User> usersList = response.getUsersList().stream().map(user -> new User(user)).collect(Collectors.toList());
        User user = new User(userAccountClientService.getUser(principal));
        List<UserRole> roleList = Arrays.asList(UserRole.values())
            .stream().filter(role ->
                role.ordinal() <= Collections.max(user.getRoles()).ordinal())
            .toList();

        mv = new ModelAndView("userList");
        mv.addObject("user", user);
        mv.addObject("usersList", usersList);
        mv.addObject("currentUser", userAccountClientService.getUser(principal));
        mv.addObject("apiPrefix", apiPrefix);
        mv.addObject("page", (Integer) 0);
        mv.addObject("limit", (Integer) 5);
        mv.addObject("pages", (response.getResultSetSize() + limit - 1)/limit);
        mv.addObject("userCount", response.getResultSetSize());
        mv.addObject("roleList", roleList);
        return mv;
    }

    /**
     * Get method for the current page of users from the list
     * @param principal Authentication information containing user info
     * @param mv Parameters sent to thymeleaf template to be rendered into HTML
     * @return The list of user fragment
     */
    @GetMapping("/usersList")
    public ModelAndView usersList(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam Integer page,
            @RequestParam Integer limit,
            ModelAndView mv
    ) {
        PaginatedUsersResponse response = userAccountClientService.getUsers(page, limit);
        List<User> usersList = response.getUsersList().stream().map(user -> new User(user)).collect(Collectors.toList());
        User user = new User(userAccountClientService.getUser(principal));
        List<UserRole> roleList = Arrays.asList(UserRole.values())
            .stream().filter(role ->
                role.ordinal() <= Collections.max(user.getRoles()).ordinal())
            .toList();

        mv = new ModelAndView("userList::userListDataTable");
        mv.addObject("usersList", usersList);
        mv.addObject("currentUser", userAccountClientService.getUser(principal));
        mv.addObject("page", page);
        mv.addObject("limit", limit);
        mv.addObject("pages", (response.getResultSetSize() + limit - 1)/limit);
        mv.addObject("userCount", response.getResultSetSize());
        mv.addObject("roleList", roleList);
        return mv;
    }

    /**
     * Delete method for removing a users role
     *
     * @param userId ID for the user
     * @param role   Type of role being deleted
     * @return Ok (200) response if successful, 500 response if failure.
     */
    @DeleteMapping(value = "/usersList/removeRole")
    public ResponseEntity<String> removeRole(String userId, String role, @AuthenticationPrincipal AuthState principal) {
        UserRole userRole = Enum.valueOf(UserRole.class, role);

        UserResponse loggedInUser = userAccountClientService.getUser(principal);
        UserResponse user = userAccountClientService.getUser(Integer.parseInt(userId));
        AtomicInteger highestUserRole = new AtomicInteger(0);
        loggedInUser.getRolesList().forEach(usersRole ->  {
            if(usersRole.getNumber() > highestUserRole.get())  highestUserRole.set(usersRole.getNumber());});

        if (user == null) {
            return new ResponseEntity("User cannot be found in database", HttpStatus.BAD_REQUEST);
        }

        if (!user.getRolesList().contains(UserRole.valueOf(role))) {
            return new ResponseEntity("User does not have this role.", HttpStatus.BAD_REQUEST);

        }

        if (user.getRolesList().size() == 1) {
            return new ResponseEntity("User must have a role.", HttpStatus.BAD_REQUEST);

        }

        if (highestUserRole.get() < UserRole.valueOf(role).getNumber()) {
            return new ResponseEntity("User cannot delete this " + role + " role", HttpStatus.BAD_REQUEST);

        }

        UserRoleChangeResponse response = userAccountClientService.removeUserRole(parseInt(userId), userRole);
        if (!response.getIsSuccess()) {
            return new ResponseEntity(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity("Role deleted successfully", HttpStatus.OK);
    }

    /**
     * Post request to add a new role to a user
     * @param principal Authentication information containing user info
     * @param userId The id of the user to add the new role to
     * @param newRole The new role to be added to the user
     * @return The updated user row or an error message
     */
    @PostMapping(value = "user/{userId}/addRole")
    public ModelAndView addRole(@AuthenticationPrincipal AuthState principal, @PathVariable int userId, @RequestParam("role") UserRole newRole) {
        ModelAndView mv =  new ModelAndView();
        User user = new User(userAccountClientService.getUser(principal));
        List<UserRole> roleList = Arrays.asList(UserRole.values())
            .stream().filter(role ->
                role.ordinal() <= Collections.max(user.getRoles()).ordinal())
            .toList();

        if (!(userAccountClientService.checkUserIsTeacherOrAdmin(principal) && roleList.contains(newRole))) {
            mv.setViewName("fragments::errorMessage");
            mv.setStatus(HttpStatus.FORBIDDEN);
            mv.addObject("messageDanger", "Insufficient permissions.");
            return mv;
        }

        UserRoleChangeResponse response = userAccountClientService.addRoleToUser(userId, newRole);
        if (!response.getIsSuccess()) {
            mv.setViewName("fragments::errorMessage");
            switch(response.getMessage()) {
            case "User already has this role.":
                mv.setStatus(HttpStatus.CONFLICT);
                break;
            case "User could not be found.":
                mv.setStatus(HttpStatus.NOT_FOUND);
            default:
                mv.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            mv.addObject("messageDanger", response.getMessage());
            return mv;
        }

        mv.setViewName("userList::userFragment");

        User updatedUser = new User(userAccountClientService.getUser(userId));
        mv.addObject("user", updatedUser);
        mv.addObject("roleList", roleList);

        mv.setStatus(HttpStatus.OK);
        return mv;
    }
}
