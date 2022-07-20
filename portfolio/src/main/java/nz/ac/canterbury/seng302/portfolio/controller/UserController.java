package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.PersistentSort;
import nz.ac.canterbury.seng302.portfolio.model.PersistentSortRepository;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.portfolio.utils.UserField;

import nz.ac.canterbury.seng302.shared.identityprovider.UserRoleChangeResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

import static java.lang.Integer.parseInt;

/**
 * Controller for user related pages excluding the current
 */
@Controller
public class UserController {

    private final UserAccountClientService userAccountClientService;
    @Value("${apiPrefix}") private String apiPrefix;

    @Autowired
    PersistentSortRepository persistentSortRepository;

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
    public ModelAndView users(@AuthenticationPrincipal AuthState principal) {
        int userId = PrincipalUtils.getUserId(principal);
        int limit = 10;
        PersistentSort sort;
        if (persistentSortRepository.findById(userId).isPresent()) {
            sort = persistentSortRepository.findById(userId).get();
        } else {
            sort = persistentSortRepository.save(new PersistentSort(PrincipalUtils.getUserId(principal)));
        }
        PaginatedUsersResponse response = userAccountClientService.getUsers(0, limit, sort.getUserListSortBy(), sort.isUserListAscending());
        List<User> usersList = response.getUsersList().stream().map(User::new).toList();

        User user = new User(userAccountClientService.getUser(principal));
        List<UserRole> roleList = Arrays.asList(UserRole.values())
                .stream().filter(role ->
                        role.ordinal() <= Collections.max(user.getRoles()).ordinal())
                .toList();

        ModelAndView mv = new ModelAndView("userList");
        mv.addObject("user", user);
        mv.addObject("usersList", usersList);
        mv.addObject("apiPrefix", apiPrefix);
        mv.addObject("page", 0);
        mv.addObject("limit", limit);
        mv.addObject("pages", (response.getResultSetSize() + limit - 1)/limit);
        mv.addObject("userCount", response.getResultSetSize());
        mv.addObject("roleList", roleList);
        mv.addObject("order", sort.getUserListSortBy().value);
        mv.addObject("asc", sort.isUserListAscending()?"asc":"desc");

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
            @RequestParam String order,
            @RequestParam Integer asc,
            ModelAndView mv
    ) {
        int userId = PrincipalUtils.getUserId(principal);
        PersistentSort sort;
        if (persistentSortRepository.findById(userId).isPresent()) {
            sort = persistentSortRepository.findById(userId).get();

        } else {
            sort = new PersistentSort(PrincipalUtils.getUserId(principal));
        }

        User user = new User(userAccountClientService.getUser(principal));
        List<UserRole> roleList = Arrays.asList(UserRole.values())
                .stream().filter(role ->
                        role.ordinal() <= Collections.max(user.getRoles()).ordinal())
                .toList();

        sort.setUserListSortBy(UserField.valueOf(order.toUpperCase()));
        sort.setIsAscendingOrder(asc==0);

        sort = persistentSortRepository.save(sort);
        PaginatedUsersResponse response = userAccountClientService.getUsers(page, limit, sort.getUserListSortBy(), sort.isUserListAscending());
        List<User> usersList = response.getUsersList().stream().map(User::new).toList();

        ModelAndView mv = new ModelAndView("userList::userListDataTable");
        mv.addObject("usersList", usersList);
        mv.addObject("page", page);
        mv.addObject("limit", limit);
        mv.addObject("pages", (response.getResultSetSize() + limit - 1)/limit);
        mv.addObject("userCount", response.getResultSetSize());
        mv.addObject("roleList", roleList);
        mv.addObject("order", order);
        mv.addObject("asc", sort.isUserListAscending()?"asc":"desc");
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
    public ResponseEntity<String> removeRole(String userId, String role, RedirectAttributes ra) {
        UserRole userRole = Enum.valueOf(UserRole.class, role);
        UserRoleChangeResponse response = userAccountClientService.removeUserRole(parseInt(userId), userRole);
        if (!response.getIsSuccess()) {
            ra.addFlashAttribute("messageDanger", response.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
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
