package nz.ac.canterbury.seng302.portfolio.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;

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
        User user = new User(userAccountClientService.getUser(principal));
        model.addAttribute("user", user);
        if (principal != null) {
            model.addAttribute("adminOrTeacher", PrincipalUtils.checkUserIsTeacherOrAdmin(principal));
        }
    }
}
