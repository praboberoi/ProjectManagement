package nz.ac.canterbury.seng302.portfolio.utils;

import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Global utility class to perform any actions that are required by all controller classes
 */
@ControllerAdvice
public class ControllerAdvisor {
    @Autowired 
    private UserAccountClientService userAccountClientService;
    @Value("${apiPrefix}") 
    private String apiPrefix;

    /**
     * Adds attributes that are required by all controllers in our application
     * @param principal Information about the currently logged in user
     * @param model The model to add sttributes to
     */
    @ModelAttribute
    public void addAttributes(@AuthenticationPrincipal AuthState principal, Model model) {
        model.addAttribute("apiPrefix", apiPrefix);
        User guestUser = new User(0, "Guest", null, null, null, null, null, null, null, null, null);
        if (principal != null) {
            User user = new User(userAccountClientService.getUser(principal));
            model.addAttribute("user", user);
            model.addAttribute("adminOrTeacher", PrincipalUtils.checkUserIsTeacherOrAdmin(principal));
        } else {
            model.addAttribute("user", guestUser);
            model.addAttribute("adminOrTeacher", false);
        }
    }
}
