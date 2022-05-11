package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.DashboardService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.util.List;

@Controller
public class DashboardController {
    @Autowired private DashboardService dashboardService;
    @Autowired private UserAccountClientService userAccountClientService;
    @Autowired private SprintService sprintService;
    @Value("${apiPrefix}") private String apiPrefix;


    /**
     * Adds the project list to the Dashboard.
     * Gets the list of the current user's roles
     * Opens dashboard.html
     * @param model
     * @param principal - current user.
     * @return name of the dashboard html file.
     */
    @RequestMapping(path = "/dashboard",method = RequestMethod.GET)
    public String showProjectList( @AuthenticationPrincipal AuthState principal,
                                   Model model) {
        try {
            dashboardService.clearCache();
            model.addAttribute("apiPrefix", apiPrefix);
            List<Project> listProjects = dashboardService.getAllProjects();
            model.addAttribute("listProjects", listProjects);
            model.addAttribute("roles", userAccountClientService.getUserRole(principal));
            model.addAttribute("user", userAccountClientService.getUser(principal));
            return "dashboard";
        } catch (Exception e) {
            model.addAttribute("user", userAccountClientService.getUser(principal));
            return "error";
        }
    }

    /**
     * Opens project.html and populates it with a new Project object
     * @param model
     * @return
     */
    @RequestMapping(path="/dashboard/newProject", method = RequestMethod.GET)
    public String showNewForm(Model model, @AuthenticationPrincipal AuthState principal) {
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        model.addAttribute("apiPrefix", apiPrefix);
        Project newProject = dashboardService.getNewProject();
        model.addAttribute("project", newProject);
        model.addAttribute("pageTitle", "Add New Project");
        model.addAttribute("submissionName", "Create");
        model.addAttribute("image", "/icons/create-icon.svg");
        model.addAttribute("user", userAccountClientService.getUser(principal));
        model.addAttribute("projectStartDateMin", dashboardService.getProjectMinDate());
        model.addAttribute("projectStartDateMax", Date.valueOf(newProject.getEndDate().toLocalDate().minusDays(1)));
        model.addAttribute("projectEndDateMin", Date.valueOf(newProject.getStartDate().toLocalDate().plusDays(1)));
        model.addAttribute("projectEndDateMax", dashboardService.getProjectMaxDate());
        return "projectForm";
    }


    /**
     * Saves project object to the database and redirects to dashboard page
     * @param project
     * @return
     */
    @RequestMapping(path="/dashboard/saveProject", method = RequestMethod.POST)
    public String saveProject(
            Project project,
            Model model,
            RedirectAttributes ra,
            @AuthenticationPrincipal AuthState principal) {
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        try {
            model.addAttribute("apiPrefix", apiPrefix);
            dashboardService.verifyProject(project);
            String message =  dashboardService.saveProject(project);
            ra.addFlashAttribute("messageSuccess", message);
            return "redirect:/dashboard";
        } catch (Exception e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/dashboard";
        }
    }

    /**
     * Opens edit page (project.html) and populates with given project
     * @param projectId ID of project selected
     * @param model
     * @return
     */
    @RequestMapping(path="/dashboard/editProject/{projectId}", method = RequestMethod.GET)
    public String showEditForm(
        @PathVariable(
        value = "projectId") int projectId,
        Model model,
        RedirectAttributes ra,
        @AuthenticationPrincipal AuthState principal) {
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        try {
            Project project  = dashboardService.getProject(projectId);
            model.addAttribute("project", project);
            model.addAttribute("pageTitle", "Edit Project: " + project.getProjectName());
            model.addAttribute("submissionName", "Save");
            model.addAttribute("image", "/icons/save-icon.svg");
            model.addAttribute("user", userAccountClientService.getUser(principal));
            model.addAttribute("projectStartDateMin", dashboardService.getProjectMinDate());
            model.addAttribute("projectStartDateMax", Date.valueOf(project.getEndDate().toLocalDate().minusDays(1)));
            model.addAttribute("projectEndDateMin", Date.valueOf(project.getStartDate().toLocalDate().plusDays(1)));
            model.addAttribute("projectEndDateMax", dashboardService.getProjectMaxDate());
            return "/projectForm";
        } catch (Exception e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            model.addAttribute("apiPrefix", apiPrefix);
            return "redirect:/dashboard";
        }
    }

    /**
     * Deletes project from the database using projectId
     * @param projectId ID for selected project
     * @return
     * @throws Exception If project is not found in the database
     */
    @RequestMapping(path="/dashboard/deleteProject/{projectId}", method = RequestMethod.POST)
    public String deleteProject(
        @PathVariable("projectId") int projectId,
        RedirectAttributes ra,
        Model model,
        @AuthenticationPrincipal AuthState principal) {
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        try {
            model.addAttribute("apiPrefix", apiPrefix);
            Project project  = dashboardService.getProject(projectId);
            String message = "Successfully Deleted " + project.getProjectName();
            sprintService.deleteAllSprints(projectId);
            dashboardService.deleteProject(projectId);
            ra.addFlashAttribute("messageSuccess", message);
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("user", userAccountClientService.getUser(principal));
            return "error";
        }
    }



}