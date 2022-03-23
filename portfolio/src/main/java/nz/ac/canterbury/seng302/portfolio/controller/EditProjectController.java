package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;


/**
 * Controller for the edit project details page
 */
@Controller
public class EditProjectController {
    /* Create default project. TODO: use database to check for this*/
    Project project = new Project();

    @GetMapping("/edit-project")
    public String projectForm(Model model) {
        /* Add project details to the model */
        model.addAttribute("projectName", project.getProjectName());
        model.addAttribute("projectStartDate", project.getStartDate());
        model.addAttribute("projectEndDate", project.getEndDate());
        model.addAttribute("projectDescription", project.getDescription());


        /* Return the name of the Thymeleaf template */
        return "editProject";
    }

    @PostMapping("/edit-project")
    public String projectSave(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="projectName") String projectName,
            @RequestParam(value="projectStartDate") Date projectStartDate,
            @RequestParam(value="projectEndDate") Date projectEndDate,
            @RequestParam(value="projectDescription") String projectDescription,
            Model model
    ) {
        project.setProjectName(projectName);
        project.setStartDate(projectStartDate);
        project.setEndDate(projectEndDate);
        project.setDescription(projectDescription);
        return "redirect:/edit-project";
    }

}
