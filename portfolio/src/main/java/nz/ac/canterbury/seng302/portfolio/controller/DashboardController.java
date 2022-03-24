package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.DashboardService;
import nz.ac.canterbury.seng302.portfolio.service.RoleClientService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.Date;
import java.time.LocalDate;

import java.util.List;


@Controller
public class DashboardController {
    @Autowired private DashboardService dashboardService;
    private RoleClientService roleClientService;



    /**
     * Adds project list to model and opens dashboard.html
     * @param model
     * @return
     */
    @GetMapping("/dashboard")
    public String showProjectList(Model model) {
        List<Project> listProjects = dashboardService.listAll();
        // Sets a default project if there are no projects
        if (listProjects.isEmpty()) {

            LocalDate now = LocalDate.now();
            Project defaultProject = new Project();
            defaultProject.setProjectName("Project " + now.getYear()); // Project {year}
            defaultProject.setStartDate(Date.valueOf(now)); // Current date

            defaultProject.setEndDate(Date.valueOf(now.plusMonths(8))); // 8 months from start date
            dashboardService.saveProject(defaultProject);
            listProjects.add(defaultProject);
        }
        model.addAttribute("listProjects", listProjects);
        if (roleClientService.getUserRole().contains(UserRole.STUDENT)) {
            return "account";
        }
        return "dashboard";
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
    public String saveProject(Project project) {
        dashboardService.saveProject(project);
        return "redirect:/dashboard";
    }

    /**
     * Opens edit page (project.html) and populates with given project
     * @param projectId ID of project selected
     * @param model
     * @return
     */
    @GetMapping("/dashboard/editProject/{projectId}")
    public String showEditForm(@PathVariable(value = "projectId") int projectId, Model model) {
        Project project  = dashboardService.getProject(projectId);
        model.addAttribute("project", project);
        model.addAttribute("pageTitle", "Edit Project (Name: " + projectId + ")");
        return "projectForm";
    }

    /**
     * Deletes project from the database using projectId
     * @param projectId ID for selected project
     * @return
     * @throws Exception If project is not found in the database
     */
    @GetMapping("/dashboard/deleteProject/{projectId}")
    public String showEditForm(@PathVariable("projectId") int projectId) throws Exception {
        SprintService sprintService = new SprintService();
        try {
            List<Sprint> sprintList = sprintService.getAllSprints();
            for (Sprint i: sprintList) {
                if (i.getProject().getProjectId() == projectId) {
                    sprintService.deleteSprint(i.getSprintId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dashboardService.deleteProject(projectId);
        return "redirect:/dashboard";
    }

}
