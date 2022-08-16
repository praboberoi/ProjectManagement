package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.DashboardService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.util.List;

/**
 * A controller which accepts api calls and directs it to the correct service
 */
@Controller
public class ProjectController {
    @Autowired private SprintService sprintService;
    @Autowired private DashboardService dashboardService;
    @Autowired private ProjectService projectService;
    @Autowired private EventService eventService;
    @Autowired private UserAccountClientService userAccountClientService;
    private Logger logger = LoggerFactory.getLogger(ProjectController.class);
    @Value("${apiPrefix}") private String apiPrefix;

    /**
     * Adds common model elements used by all controller methods.
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("apiPrefix", apiPrefix);
    }

    /**
     * Gets all of the sprints and returns it in a ResponseEntity
     * @param projectId The Id of the project to get sprints from
     * @return A ResponseEntity containing a list of sprints
     */
    @GetMapping(path="/project/{projectId}/getAllSprints")
    public ResponseEntity<List<Sprint>> getAllSprints(
            @PathVariable("projectId") int projectId) {
        List<Sprint> listSprints = sprintService.getSprintByProject(projectId);
        return ResponseEntity.status(HttpStatus.OK).body(listSprints);
    }

    /**
     * Add project details, sprints, and current user roles (to determine access to add, edit, delete sprints)
     * to the individual project pages.
     * @param projectId ID of the project selected to view
     * @param principal Current User of type {@link AuthState}
     * @param model Of type {@link Model}
     * @param ra Redirect Attribute frontend message object
     * @return - name of the html page to display
     */
    @GetMapping(path="/project/{projectId}")
    public String showProject(
            @PathVariable("projectId") int projectId,
            @AuthenticationPrincipal AuthState principal,
            Model model,
            RedirectAttributes ra) {
        try {
            List<Sprint> listSprints = sprintService.getSprintByProject(projectId);
            List<Event> listEvents = eventService.getEventByProjectId(projectId);
            Project project = projectService.getProjectById(projectId);
            model.addAttribute("listEvents", listEvents);
            model.addAttribute("listSprints", listSprints);
            model.addAttribute("project", project);
            model.addAttribute("roles", PrincipalUtils.getUserRole(principal));
            model.addAttribute("user", userAccountClientService.getUser(principal));
            return "project";
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/dashboard";
        }
    }

    /**
     * Checks if a sprints dates are valid and returns a Response containing a message
     * @param projectId ID of the project to check
     * @param startDate New start date of the project
     * @param endDate New end date of the project
     * @param principal
     * @return ResponseEntity containing a string message
     */
    @PostMapping("/verifyProject/{projectId}")
    public ResponseEntity<String> verifyProject(
        @PathVariable int projectId,
        String startDate,
        String endDate,
        @AuthenticationPrincipal AuthState principal) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return null;
        try {
            Project project = new Project.Builder()
                    .projectId(projectId)
                    .startDate(Date.valueOf(startDate))
                    .endDate(Date.valueOf(endDate))
                    .build();
            dashboardService.verifyProject(project);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (IncorrectDetailsException e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }
}
