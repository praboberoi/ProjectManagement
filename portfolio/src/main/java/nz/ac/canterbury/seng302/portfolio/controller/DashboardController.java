package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.DashboardService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.sql.Date;
import java.util.List;

@Controller
public class DashboardController {
    @Autowired private DashboardService dashboardService;
    @Autowired private UserAccountClientService userAccountClientService;
    @Value("${apiPrefix}") private String apiPrefix;

    private static final String ERROR_PAGE = "error";
    private static final String DASHBOARD_REDIRECT = "redirect:/dashboard";

    /**
    * Adds common model elements used by all controller methods.
    */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("apiPrefix", apiPrefix);
    }

    /**
     * Maps all the projects, current user and user's role to the dashboard.html page
     * @param model Of type {@link Model}
     * @param principal Current user of type {@link AuthState}
     * @return dashboard.html file
     */
    @GetMapping(path = "/dashboard")
    public String showProjectList( @AuthenticationPrincipal AuthState principal,
                                   Model model) {
        User user = new User(userAccountClientService.getUser(principal));
        try {
            List<Project> listProjects = dashboardService.getAllProjects();
            model.addAttribute("listProjects", listProjects);
            model.addAttribute("user", user);
            return "dashboard";
        } catch (Exception e) {
            model.addAttribute("user",user);
            return ERROR_PAGE;
        }
    }


    /**
     * Maps a new project, current user, user's role and button info to projectForm.html
     * @param model Of type {@link Model}
     * @param principal Of type{@link AuthState}
     * @return projectForm.html file
     */
    @GetMapping(path="/dashboard/newProject")
    public String showNewForm(Model model, @AuthenticationPrincipal AuthState principal) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return DASHBOARD_REDIRECT;
        model.addAttribute("apiPrefix", apiPrefix);
        Project newProject = dashboardService.getNewProject();
        List<Date> dateRange = dashboardService.getProjectDateRange(newProject);
        model.addAttribute("project", newProject);
        model.addAttribute("pageTitle", "Add New Project");
        model.addAttribute("submissionName", "Create");
        model.addAttribute("image", apiPrefix + "/icons/create-icon.svg");
        model.addAttribute("user", new User(userAccountClientService.getUser(principal)));
        model.addAttribute("projectStartDateMin", dateRange.get(0));
        model.addAttribute("projectStartDateMax", Date.valueOf(newProject.getEndDate().toLocalDate().minusDays(1)));
        model.addAttribute("projectEndDateMin", Date.valueOf(newProject.getStartDate().toLocalDate().plusDays(1)));
        model.addAttribute("projectEndDateMax", dateRange.get(1));
        return "projectForm";
    }
}
