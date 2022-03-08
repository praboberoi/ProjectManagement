package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.entity.Project;
import nz.ac.canterbury.seng302.portfolio.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.Console;
import java.util.List;

@Controller
public class DashboardController {
    @Autowired private DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String showProjectList(Model model) {
        List<Project> listProjects = dashboardService.listAll();
        model.addAttribute("listProjects", listProjects);
        return "dashboard";
    }

    @GetMapping("/dashboard/newProject")
    public String showNewForm(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("pageTitle", "Add New Project");
        return "project_form";
    }

    @PostMapping("/dashboard/saveProject")
    public String saveProject(Project project) {
        dashboardService.saveProject(project);
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard/editProject/{projectId}")
    public String showEditForm(@PathVariable("projectId") Integer projectId, Model model) {
        Project project = dashboardService.getProject(projectId);
        model.addAttribute("project", project);
        model.addAttribute("pageTitle", "Edit Project (Name: " + projectId + ")");
        return "project_form";
    }

    @GetMapping("/dashboard/deleteProject/{projectId}")
    public String showEditForm(@PathVariable("projectId") Integer projectId) throws Exception {
        dashboardService.deleteProject(projectId);
/*
        try {
            dashboardService.deleteProject(projectId);
        } catch (Exception e) { // Change exception to ProjectNotFoundException
            System.out.println(e.getMessage());
        }

 */
        return "redirect:/dashboard";
    }
}
