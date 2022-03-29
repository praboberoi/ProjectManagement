package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.DashboardService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
public class DashboardController {
    @Autowired private DashboardService dashboardService;
    @Autowired private UserAccountClientService userAccountClientService;
    @Autowired private SprintService sprintService;


    /**
     * Adds the project list to the Dashboard.
     * Gets the list of the current user's roles
     * Opens dashboard.html
     * @param model
     * @param principal - current user.
     * @return name of the dashboard html file.
     */
    @GetMapping("/dashboard")
    public String showProjectList( @AuthenticationPrincipal AuthState principal,
                                   Model model) {
        List<Project> listProjects = null;
        try {
            listProjects = dashboardService.getAllProjects();
            model.addAttribute("listProjects", listProjects);
            // Add the list of the current users roles to model so they can be used in dashboard.html with thymeleaf.
            model.addAttribute("roles", userAccountClientService.getUserRole(principal));
            return "dashboard";
        } catch (Exception e) {
            model.addAttribute("exception", e);
            model.addAttribute("message", e.getMessage());
            model.addAttribute("timestamp", LocalDate.now());
            model.addAttribute("error", "Invalid Information");
            model.addAttribute("path", "./portfolio/src/main/java/nz/ac/canterbury/seng302/portfolio/controller/DashboardController.java");
            model.addAttribute("trace", "portfolio/src/main/java/nz/ac/canterbury/seng302/portfolio/service/DashboardService.java");
            model.addAttribute("status", "re-run Project");
            return "error";
        }
    }

    /**
     * Opens project.html and populates it with a new Project object
     * @param model
     * @return
     */
    @GetMapping("/dashboard/newProject")
    public String showNewForm(Model model, @AuthenticationPrincipal AuthState principal) {
        if (retrieveUserRoles(principal)) return "redirect:/dashboard";
        dashboardService.setPreviousFeature("Create");
        model.addAttribute("project", new Project());
        model.addAttribute("pageTitle", "Add New Project");
        model.addAttribute("submissionName", "Create");
        return "projectForm";
    }

    /**
     * Saves project object to the database and redirects to dashboard page
     * @param project
     * @return
     */
    @PostMapping("/dashboard/saveProject")
    public String saveProject(
            Project project,
            Model model,
            RedirectAttributes ra,
            @AuthenticationPrincipal AuthState principal) {
        if (retrieveUserRoles(principal)) return "redirect:/dashboard";
        try {
            String msgString;
            dashboardService.saveProject(project);
            msgString = dashboardService.createSuccessMessage(project);
            if (msgString == null) {
                throw new Exception("Error generating success message");
            }
            ra.addFlashAttribute("messageSuccess", msgString);
            return "redirect:/dashboard";
        } catch (Exception e) {
            return "error";
        }
    }

    /**
     * Opens edit page (project.html) and populates with given project
     * @param projectId ID of project selected
     * @param model
     * @return
     */
    @GetMapping("/dashboard/editProject/{projectId}")
    public String showEditForm(
        @PathVariable(
        value = "projectId") int projectId,
        Model model,
        RedirectAttributes ra,
        @AuthenticationPrincipal AuthState principal) {
        if (retrieveUserRoles(principal)) return "redirect:/dashboard";
        try {
            dashboardService.setPreviousFeature("Edit");
            Project project  = dashboardService.getProject(projectId);
            model.addAttribute("project", project);
            model.addAttribute("pageTitle", "Edit Project: " + project.getProjectName());
            model.addAttribute("submissionName", "Save");
            return "projectForm";
        } catch (NullPointerException e) {
            ra.addFlashAttribute("messageDanger", "No Project Found");
            return "redirect:/dashboard";
        }
    }

    /**
     * Deletes project from the database using projectId
     * @param projectId ID for selected project
     * @return
     * @throws Exception If project is not found in the database
     */
    @GetMapping("/dashboard/deleteProject/{projectId}")
    public String deleteProject(
        @PathVariable("projectId") int projectId,
        Model model,
        @AuthenticationPrincipal AuthState principal) {
        if (retrieveUserRoles(principal)) return "redirect:/dashboard";
        try {
            sprintService.deleteAllSprints(projectId);
            dashboardService.deleteProject(projectId);
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("exception", e);
            model.addAttribute("message", e.getMessage());
            model.addAttribute("timestamp", LocalDate.now());
            model.addAttribute("error", "Invalid Information");
            model.addAttribute("path", "./portfolio/src/main/java/nz/ac/canterbury/seng302/portfolio/controller/DashboardController.java");
            model.addAttribute("trace", "portfolio/src/main/java/nz/ac/canterbury/seng302/portfolio/service/DashboardService.java");
            model.addAttribute("status", "re-run Project");
            return "error";
        }
    }

    static boolean retrieveUserRoles(@AuthenticationPrincipal AuthState principal) {
        List<String> userRoles = Arrays.asList(principal.getClaimsList().stream().filter(claim -> claim.getType().equals("role")).findFirst().map(ClaimDTO::getValue).orElse("NOT FOUND").split(","));
        if (!(userRoles.contains(UserRole.TEACHER.name()) || userRoles.contains(UserRole.COURSE_ADMINISTRATOR.name()))) {
            return true;
        }
        return false;
    }
}