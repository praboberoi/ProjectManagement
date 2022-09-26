package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.DashboardService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {
    @Autowired private DashboardService dashboardService;
    @Autowired private UserAccountClientService userAccountClientService;
    @Value("${apiPrefix}") private String apiPrefix;

    private static final String ERROR_PAGE = "error";

    /**
     * Maps all the projects, current user and user's role to the dashboard.html page
     * @param model Of type {@link Model}
     * @param principal Current user of type {@link AuthState}
     * @return dashboard.html file
     */
    @GetMapping(path = "/dashboard")
    public String showProjectList( @AuthenticationPrincipal AuthState principal,
                                   Model model) {
        try {
            List<Project> listProjects = dashboardService.getAllProjects();

            model.addAttribute("listProjects", listProjects);
            return "dashboard";
        } catch (Exception e) {
            return ERROR_PAGE;
        }
    }
}
