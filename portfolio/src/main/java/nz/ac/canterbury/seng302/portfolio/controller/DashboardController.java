package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.DashboardService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.util.List;

@Controller
public class DashboardController {
    @Autowired private DashboardService dashboardService;
    @Autowired private UserAccountClientService userAccountClientService;
    private Logger logger = LoggerFactory.getLogger(DashboardController.class);
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
            model.addAttribute("roles", PrincipalUtils.getUserRole(principal));
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

    /**
     * Saves project object to the database and redirects to dashboard page
     * @param project Of type {@link Project}
     * @param model Of type {@link Model}
     * @param ra Of type {@link RedirectAttributes}
     * @param principal Of type {@link AuthState}
     * @return Either the dashboard.html file or error.html file
     */
    @PostMapping(path="/dashboard/saveProject")
    public String saveProject(
            Project project,
            Model model,
            RedirectAttributes ra,
            @AuthenticationPrincipal AuthState principal) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return DASHBOARD_REDIRECT;
        try {
            dashboardService.verifyProject(project);
            String message =  dashboardService.saveProject(project);
            ra.addFlashAttribute("messageSuccess", message);
            logger.info("Project {} has been created by user {}", project.getProjectId(), PrincipalUtils.getUserId(principal));
            return DASHBOARD_REDIRECT;
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return DASHBOARD_REDIRECT;
        } catch (Exception e) {
            model.addAttribute("user", userAccountClientService.getUser(principal));
            logger.error("An error occured while saving a project.", e);
            return ERROR_PAGE;
        }
    }

    /**
     * Maps an existing project, current user, user's role and button info to projectForm.html
     * @param projectId Of type int
     * @param model Of type {@link Model}
     * @param ra Of type {@link RedirectAttributes}
     * @param principal Of type {@link AuthState}
     * @return projectForm.html file or dashboard.html file
     */
    @GetMapping(path="/dashboard/editProject/{projectId}")
    public String showEditForm(
        @PathVariable(
        value = "projectId") int projectId,
        Model model,
        RedirectAttributes ra,
        @AuthenticationPrincipal AuthState principal) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return DASHBOARD_REDIRECT;
        try {
            Project project  = dashboardService.getProject(projectId);
            List<Date> dateRange = dashboardService.getProjectDateRange(project);
            model.addAttribute("project", project);
            model.addAttribute("pageTitle", "Edit Project: " + project.getProjectName());
            model.addAttribute("submissionName", "Save");
            model.addAttribute("image", apiPrefix + "/icons/save-icon.svg");
            model.addAttribute("user", new User(userAccountClientService.getUser(principal)));
            model.addAttribute("projectStartDateMin", dateRange.get(0));
            model.addAttribute("projectStartDateMax", Date.valueOf(project.getEndDate().toLocalDate().minusDays(1)));
            model.addAttribute("projectEndDateMin", Date.valueOf(project.getStartDate().toLocalDate().plusDays(1)));
            model.addAttribute("projectEndDateMax", dateRange.get(1));
            return "projectForm";
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
                return DASHBOARD_REDIRECT;
        }
    }

    /**
     * Deletes the project and all the related sprints
     * @param projectId Of type int
     * @param ra Of type {@link RedirectAttributes}
     * @param model of type {@link Model}
     * @param principal of type {@link AuthState}
     * @return dashboard.html file or error.html file
     */
    @PostMapping(path="/dashboard/deleteProject/{projectId}")
    public String deleteProject(
        @PathVariable("projectId") int projectId,
        RedirectAttributes ra,
        Model model,
        @AuthenticationPrincipal AuthState principal) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return DASHBOARD_REDIRECT;
        try {
            Project project  = dashboardService.getProject(projectId);
            String message = "Successfully Deleted " + project.getProjectName();
            dashboardService.deleteProject(projectId);
            ra.addFlashAttribute("messageSuccess", message);
            return DASHBOARD_REDIRECT;
        } catch (IncorrectDetailsException e) {
            model.addAttribute("user", userAccountClientService.getUser(principal));
            return ERROR_PAGE;
        }
    }



}
