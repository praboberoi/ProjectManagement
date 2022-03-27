package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

/**
 * Controller for the display project details page
 */
@Controller
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private SprintService sprintService;

    @Autowired private UserAccountClientService userAccountClientService;

    @GetMapping("/details")
    public String details(@AuthenticationPrincipal AuthState principal, Model model) throws Exception {
        /* Add project details to the model */
        // Gets the project with id 0 to plonk on the page
        Project project = projectService.getProjectById(0);
        model.addAttribute("project", project);
        
        List<Sprint> sprintList = sprintService.getAllSprints();
        model.addAttribute("sprints", sprintList);

        List<UserRole> roles = userAccountClientService.getUserRole(principal);
        model.addAttribute("roles", roles);

        return "project";
        }
}

