package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.DashboardService;
import nz.ac.canterbury.seng302.portfolio.service.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.PersistenceException;
import java.sql.Date;
import java.util.List;

@Controller
public class DashboardController {
    @Autowired private DashboardService dashboardService;
    @Autowired private UserAccountClientService userAccountClientService;
    @Autowired private SprintService sprintService;

    /**
     * Maps all the projects, current user and user's role to the dashboard.html page
     * @param model Of type {@link Model}
     * @param principal Current user of type {@link AuthState}
     * @return dashboard.html file
     */
    @GetMapping("/dashboard")
    public String showProjectList( @AuthenticationPrincipal AuthState principal,
                                   Model model) {
        List<Project> listProjects = dashboardService.getAllProjects();
        model.addAttribute("listProjects", listProjects);
        model.addAttribute("roles", userAccountClientService.getUserRole(principal));
        model.addAttribute("user", userAccountClientService.getUser(principal));
        return "dashboard";
    }


    /**
     * Maps a new project, current user, user's role and button info to projectForm.html
     * @param model Of type {@link Model}
     * @param principal Of type{@link AuthState}
     * @return projectForm.html file
     */
    @GetMapping("/dashboard/newProject")
    public String showNewForm(Model model, @AuthenticationPrincipal AuthState principal) {
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        Project newProject = dashboardService.getNewProject();
        List<Date> dateRange = dashboardService.getProjectDateRange(newProject);
        model.addAttribute("project", newProject);
        model.addAttribute("pageTitle", "Add New Project");
        model.addAttribute("submissionName", "Create");
        model.addAttribute("image", "/icons/create-icon.svg");
        model.addAttribute("user", userAccountClientService.getUser(principal));
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
    @PostMapping("/dashboard/saveProject")
    public String saveProject(
            Project project,
            Model model,
            RedirectAttributes ra,
            @AuthenticationPrincipal AuthState principal) {
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        try {
            dashboardService.verifyProject(project);
            String message =  dashboardService.saveProject(project);
            ra.addFlashAttribute("messageSuccess", message);
            return "redirect:/dashboard";
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/dashboard";
        } catch (PersistenceException e) {
            model.addAttribute("user", userAccountClientService.getUser(principal));
            return "error";
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
    @GetMapping("/dashboard/editProject/{projectId}")
    public String showEditForm(
        @PathVariable(
        value = "projectId") int projectId,
        Model model,
        RedirectAttributes ra,
        @AuthenticationPrincipal AuthState principal) {
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        try {
            Project project  = dashboardService.getProject(projectId);
            List<Date> dateRange = dashboardService.getProjectDateRange(project);
            model.addAttribute("project", project);
            model.addAttribute("pageTitle", "Edit Project: " + project.getProjectName());
            model.addAttribute("submissionName", "Save");
            model.addAttribute("image", "/icons/save-icon.svg");
            model.addAttribute("user", userAccountClientService.getUser(principal));
            model.addAttribute("projectStartDateMin", dateRange.get(0));
            model.addAttribute("projectStartDateMax", Date.valueOf(project.getEndDate().toLocalDate().minusDays(1)));
            model.addAttribute("projectEndDateMin", Date.valueOf(project.getStartDate().toLocalDate().plusDays(1)));
            model.addAttribute("projectEndDateMax", dateRange.get(1));
            return "projectForm";
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/dashboard";
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
    @PostMapping("/dashboard/deleteProject/{projectId}")
    public String deleteProject(
        @PathVariable("projectId") int projectId,
        RedirectAttributes ra,
        Model model,
        @AuthenticationPrincipal AuthState principal) {
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        try {
            Project project  = dashboardService.getProject(projectId);
            String message = "Successfully Deleted " + project.getProjectName();
            sprintService.deleteAllSprints(projectId);
            dashboardService.deleteProject(projectId);
            ra.addFlashAttribute("messageSuccess", message);
            return "redirect:/dashboard";
        } catch (IncorrectDetailsException e) {
            model.addAttribute("user", userAccountClientService.getUser(principal));
            return "error";
        }
    }



}