package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.PersistentSort;
import nz.ac.canterbury.seng302.portfolio.model.PersistentSortRepository;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.portfolio.utils.UserField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

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

    /**
     * Get method for the first page of the list of users
     * @param principal Authentication information containing user info
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

        ModelAndView mv = new ModelAndView("userList");
        mv.addObject("usersList", usersList);
        mv.addObject("user", userAccountClientService.getUser(principal));
        mv.addObject("apiPrefix", apiPrefix);
        mv.addObject("page", 0);
        mv.addObject("limit", limit);
        mv.addObject("pages", (response.getResultSetSize() + limit - 1)/limit);
        mv.addObject("userCount", response.getResultSetSize());
        mv.addObject("order", sort.getUserListSortBy().value);
        mv.addObject("asc", sort.isUserListAscending()?"asc":"desc");

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
            @RequestParam String order,
            @RequestParam Integer asc
    ) {
        int userId = PrincipalUtils.getUserId(principal);
        PersistentSort sort;
        if (persistentSortRepository.findById(userId).isPresent()) {
            sort = persistentSortRepository.findById(userId).get();

        } else {
            sort = new PersistentSort(PrincipalUtils.getUserId(principal));
        }

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
        mv.addObject("order", order);
        mv.addObject("asc", sort.isUserListAscending()?"asc":"desc");
        return mv;
    }
}
