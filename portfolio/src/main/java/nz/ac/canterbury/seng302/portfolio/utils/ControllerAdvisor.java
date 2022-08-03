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

@ControllerAdvice
public class ControllerAdvisor {
    @Autowired 
    private UserAccountClientService userAccountClientService;
    @Value("${apiPrefix}") 
    private String apiPrefix;

    @ModelAttribute
    public void addAttributes(@AuthenticationPrincipal AuthState principal, Model model) {
        model.addAttribute("apiPrefix", apiPrefix);
        if (principal != null) {
            User user = new User(userAccountClientService.getUser(principal));
            model.addAttribute("user", user);
            model.addAttribute("adminOrTeacher", PrincipalUtils.checkUserIsTeacherOrAdmin(principal));
        }
    }
}
