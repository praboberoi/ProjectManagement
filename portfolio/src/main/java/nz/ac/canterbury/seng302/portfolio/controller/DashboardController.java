package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.entity.Project;
import nz.ac.canterbury.seng302.portfolio.entity.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.DashboardService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Controller
public class DashboardController {
    @Autowired private DashboardService dashboardService;
    @Autowired private SprintService sprintService;

    @GetMapping("/dashboard")
    public String showProjectList(Model model) {
        List<Project> listProjects = dashboardService.listAll();
        // Sets a default project if there are no projects
        if (listProjects.isEmpty()) {
            int year = LocalDate.now().getYear();
            int month = LocalDate.now().getMonthValue();
            int day = LocalDate.now().getDayOfMonth();
            System.out.println(day + "CURRENT DAY");
            Project defaultProject = new Project();
            defaultProject.setProjectName("Project " + year); // Project {year}
            String currentDate =  (year + "-" + month + "-" + day);
            defaultProject.setStartDate(currentDate); // Current date

            month += 8;
            if (month > 12) { // Changes year if month goes over 12
                month -= 12;
                year += 1;
            }

            defaultProject.setEndDate(year + "-" + month + "-" + day); // 8 months from start date
            dashboardService.saveProject(defaultProject);
            listProjects.add(defaultProject);
        }
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
        System.out.println(project.getProjectId() +" " + project.getProjectName());
        dashboardService.saveProject(project);
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard/editProject/{projectId}")
    public String showEditForm(@PathVariable(value = "projectId") Long projectId, Model model) {
        Project project  = dashboardService.getProject(projectId);
        model.addAttribute("project", project);
        model.addAttribute("pageTitle", "Edit Project (Name: " + projectId + ")");
        System.out.println(project.getProjectId());
        System.out.println(project.getProjectName());
        return "project_form";
    }

    @GetMapping("/dashboard/deleteProject/{projectId}")
    public String showEditForm(@PathVariable("projectId") Long projectId) throws Exception {
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

    @GetMapping("/project/{projectId}")
    public String showSprintList(@PathVariable("projectId") Long projectId, Model model) {
        List<Sprint> listSprints = sprintService.listAll(); //update list to only show list for project
        //List<Sprint> listSprints = sprintService.listByProjectId(projectId);
        model.addAttribute("listSprints", listSprints);
        return "project";
    }
}
