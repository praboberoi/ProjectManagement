package nz.ac.canterbury.seng302.portfolio.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        mv = new ModelAndView("userList");
        mv.addObject("usersList", usersList);
        mv.addObject("adminOrTeacher", userAccountClientService.checkUserIsTeacherOrAdmin(principal));
        mv.addObject("user", userAccountClientService.getUser(principal));
        mv.addObject("apiPrefix", apiPrefix);
        mv.addObject("page", (Integer) 0);
        mv.addObject("limit", (Integer) 5);
        mv.addObject("pages", (response.getResultSetSize() + limit - 1)/limit);
        mv.addObject("userCount", response.getResultSetSize());
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

        mv = new ModelAndView("userList::userListDataTable");
        mv.addObject("usersList", usersList);
        mv.addObject("page", page);
        mv.addObject("limit", limit);
        mv.addObject("pages", (response.getResultSetSize() + limit - 1)/limit);
        mv.addObject("userCount", response.getResultSetSize());
        return mv;
    }

    /**
     * Delete method for removing a users role
     *
     * @param userId ID for the user
     * @param role   Type of role being deleted
     * @return Ok (200) response if successful, 417 response if failure.
     */
    @DeleteMapping(value = "/usersList/removeRole")
    public ResponseEntity removeRole(String userId, String role,  @AuthenticationPrincipal AuthState principal) {
        UserRole userRole = Enum.valueOf(UserRole.class, role);

        UserResponse user = userAccountClientService.getUser(principal);
        AtomicInteger highestUserRole = new AtomicInteger(0);
        user.getRolesList().forEach(usersRole ->  {
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
        System.out.println(highestUserRole.get() + "/" + UserRole.valueOf(role).getNumber());
        if (highestUserRole.get() < UserRole.valueOf(role).getNumber()) {
            return new ResponseEntity("User cannot delete this " + role + " role", HttpStatus.BAD_REQUEST);

        }

        UserRoleChangeResponse response = userAccountClientService.removeUserRole(parseInt(userId), userRole);
        if (!response.getIsSuccess()) {
            return new ResponseEntity(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity("Role deleted successfully", HttpStatus.OK);
    }
}
