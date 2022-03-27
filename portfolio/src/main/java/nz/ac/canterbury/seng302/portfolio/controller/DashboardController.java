package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.DashboardService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

import java.util.List;

// TODO:
@Controller
public class DashboardController {
    @Autowired private DashboardService dashboardService;
    @Autowired private SprintService sprintService;


    /**
     * Adds project list to model and opens dashboard.html
     * @param model
     * @return
     */
    @GetMapping("/dashboard")
    public String showProjectList(Model model) {
        List<Project> listProjects = null;
        try {
            listProjects = dashboardService.getAllProjects();
            model.addAttribute("listProjects", listProjects);
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
    public String showNewForm(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("pageTitle", "Add New Project");
        return "projectForm";
    }

    /**
     * Saves project object to the database and redirects to dashboard page
     * @param project
     * @return
     */
    @PostMapping("/dashboard/saveProject")
    public String saveProject(Project project, Model model, RedirectAttributes ra) {
        try {
            String msgString;
            dashboardService.saveProject(project);
            msgString = String.format("Successfully Saved Project %s", project.getProjectName());
            ra.addFlashAttribute("messageSuccess", msgString);
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

    /**
     * Opens edit page (project.html) and populates with given project
     * @param projectId ID of project selected
     * @param model
     * @return
     */
    @GetMapping("/dashboard/editProject/{projectId}")
    public String showEditForm(@PathVariable(value = "projectId") int projectId, Model model, RedirectAttributes ra) {
        try {
            Project project  = dashboardService.getProject(projectId);
            model.addAttribute("project", project);
            model.addAttribute("pageTitle", "Edit Project (Name: " + project.getProjectName() + ")");
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
    public String deleteProject(@PathVariable("projectId") int projectId, Model model) {
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

}
