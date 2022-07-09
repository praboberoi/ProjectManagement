package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * A custom error controller for mapping to the error page
 */
@Controller
public class CustomErrorController implements ErrorController {
    @Value("${apiPrefix}") private String apiPrefix;
    @Autowired private UserAccountClientService userAccountClientService;

    /**
     * Maps the api prefix and the user to the error page inorder to display the appropriate links on the sidebar
     * @param model of type {@link Model}
     * @param principal of type {@link AuthState}
     * @return the error page
     */
    @RequestMapping("/error")
        public String handleError(Model model, @AuthenticationPrincipal AuthState principal) {
            model.addAttribute("apiPrefix", apiPrefix);
            model.addAttribute("user", userAccountClientService.getUser(principal));
            return "error";
        }
    }