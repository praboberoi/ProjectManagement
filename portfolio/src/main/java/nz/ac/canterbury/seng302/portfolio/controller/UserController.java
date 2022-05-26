package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

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
     * Get method for the first page of the list of users
     * @param principal Authentication information containing user info
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
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
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
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
}
