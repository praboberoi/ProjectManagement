package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.PersistentSort;
import nz.ac.canterbury.seng302.portfolio.model.PersistentSortRepository;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.portfolio.utils.UserField;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Autowired
    private SimpMessagingTemplate template;

    public UserController (UserAccountClientService userAccountClientService) {
        this.userAccountClientService = userAccountClientService;
    }

    /**
     * Get method for the first page of the list of users
     * @param principal Authentication information containing user info
     * @return The user list page
     */
    @GetMapping("/users")
    public ModelAndView users(@AuthenticationPrincipal AuthState principal) {
        int userId = PrincipalUtils.getUserId(principal);
        int limit = 10;
        Optional<PersistentSort> sortBase = persistentSortRepository.findById(userId);
        PersistentSort sort;
        if (sortBase.isPresent()) {
            sort = sortBase.get();
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
        mv.addObject("currentUser", userAccountClientService.getUser(principal));
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
      * @param page The page of users to retrieve
      * @param limit The number of users to retrieve
      * @param order The field to sort the users by
      * @param asc The direction to sort the users in
      * @return The list of user fragment
      */
    @GetMapping("/usersList")
    public ModelAndView usersList(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam Integer page,
            @RequestParam Integer limit,
            @RequestParam String order,
            @RequestParam Integer asc
    ) {
        int userId = PrincipalUtils.getUserId(principal);

        Optional<PersistentSort> sortBase = persistentSortRepository.findById(userId);
        PersistentSort sort;
        if (sortBase.isPresent()) {
            sort = sortBase.get();
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
        mv.addObject("currentUser", userAccountClientService.getUser(principal));
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
     * Sends an update message to all clients connected to the websocket
     * @param userId Id of user that has been updated
     */
    private void notifyRoleChange(int userId) {
        template.convertAndSend("/element/user/", userId);
        template.convertAndSend("/element/user/" + userId + "/roles", "");
    }

    /**
     * Delete method for removing a users role
     *
     * @param userId ID for the user
     * @param role   Type of role being deleted
     * @param principal Authentication information containing user info
     * @return Ok (200) response if successful, 500 response if failure.
     */
    @DeleteMapping(value = "/usersList/removeRole")
    public ResponseEntity<String> removeRole(String userId, @RequestParam("role") UserRole role, @AuthenticationPrincipal AuthState principal) {
        UserResponse loggedInUser = userAccountClientService.getUser(principal);
        UserResponse user = userAccountClientService.getUser(parseInt(userId));
        AtomicInteger highestUserRole = new AtomicInteger(0);
        loggedInUser.getRolesList().forEach(usersRole ->  {
            if(usersRole.getNumber() > highestUserRole.get())  highestUserRole.set(usersRole.getNumber());
        });

        if (user == null) {
            return new ResponseEntity<>("User cannot be found in database", HttpStatus.NOT_FOUND);
        }
        if (highestUserRole.get() == 0) {
            return new ResponseEntity<>("You do not have these permissions", HttpStatus.FORBIDDEN);
        }

        if (!user.getRolesList().contains(role)) {
            return new ResponseEntity<>("User does not have this role.", HttpStatus.NOT_FOUND);
        }

        if (user.getRolesList().size() == 1) {
            return new ResponseEntity<>("User must have a role.", HttpStatus.METHOD_NOT_ALLOWED);
        }

        if (highestUserRole.get() < role.ordinal()) {
            return new ResponseEntity<>("User cannot delete this " + role + " role", HttpStatus.FORBIDDEN);
        }

        UserRoleChangeResponse response = userAccountClientService.removeUserRole(parseInt(userId), role);
        if (!response.getIsSuccess()) {
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        notifyRoleChange(Integer.parseInt(userId));
        return new ResponseEntity<>("Role deleted successfully", HttpStatus.OK);
    }

    /**
     * Post request to add a new role to a user
     * @param principal Authentication information containing user info
     * @param userId The id of the user to add the new role to
     * @param newRole The new role to be added to the user
     * @return The updated user row or an error message
     */
    @PostMapping(value = "user/{userId}/addRole")
    public ResponseEntity<String> addRole(@AuthenticationPrincipal AuthState principal, @PathVariable int userId, @RequestParam("role") UserRole newRole) {
        User user = new User(userAccountClientService.getUser(principal));
        List<UserRole> roleList = Arrays.asList(UserRole.values())
            .stream().filter(role ->
                role.ordinal() <= Collections.max(user.getRoles()).ordinal())
            .toList();

        if (!(userAccountClientService.checkUserIsTeacherOrAdmin(principal) && roleList.contains(newRole))) {
            return new ResponseEntity<>("Insufficient Permissions", HttpStatus.FORBIDDEN);
        }
        
        UserRoleChangeResponse response = userAccountClientService.addRoleToUser(userId, newRole);
        if (!response.getIsSuccess()) {
            switch(response.getMessage()) {
            case "User already has this role.":
                return new ResponseEntity<>(response.getMessage(), HttpStatus.CONFLICT);
            case "User could not be found.":
                return new ResponseEntity<>(response.getMessage(), HttpStatus.NOT_FOUND);
            default:
                return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        notifyRoleChange(userId);
        return new ResponseEntity<>("Successfully added " + newRole, HttpStatus.OK);
    }

    /**
     * Returns a fragment containing information about a specific user
     * @param userId The id of the user the information is wanted about
     * @return A html fragment containing the user's information.
     */
    @GetMapping(path="/users/{userId}/info")
    public ModelAndView userInfo(@PathVariable("userId") int userId, @AuthenticationPrincipal AuthState principal ) {
        ModelAndView mv = new ModelAndView("userFragment::userFragment");
        UserResponse userResponse = userAccountClientService.getUser(userId);
        User user = new User(userResponse);
        List<UserRole> roleList = Arrays.asList(UserRole.values())
                .stream().filter(role ->
                        role.ordinal() <= Collections.max(user.getRoles()).ordinal())
                .toList();
        mv.addObject("roleList", roleList);
        mv.addObject("user", user);
        mv.addObject("currentUser", userAccountClientService.getUser(principal));
        return mv;
    }

    /**
     * Returns a fragment for a user row in the group settings page
     * @param userId The userId of the requested user
     * @return A fragment containing a row for the changed user
     */
    @GetMapping(path="/group/user/{userId}")
    public ModelAndView groupUser(@PathVariable int userId) {
        ModelAndView mv = new ModelAndView("groupFragments::userFragment");
        UserResponse response = userAccountClientService.getUser(userId);
        User user = new User(response);
        mv.addObject("user", user);
        return mv;
    }

    /**
     * Returns a fragment for a user row in the groups page
     * @param userId The userId of the requested user
     * @return A fragment containing a row for the changed user
     */
    @GetMapping(path="/groups/user/{userId}")
    public ModelAndView groupsUser(@PathVariable int userId) {
        ModelAndView mv = new ModelAndView("groupsFragments::userFragment");
        UserResponse response = userAccountClientService.getUser(userId);
        User user = new User(response);
        mv.addObject("user", user);
        return mv;
    }
}
