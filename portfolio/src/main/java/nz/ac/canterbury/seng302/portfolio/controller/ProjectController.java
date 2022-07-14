package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.DashboardService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.Date;
import java.util.List;

/**
 * A controller which accepts api calls and directs it to the correct service
 */
@Controller
public class ProjectController {
    @Autowired private SprintService sprintService;
    @Value("${apiPrefix}") private String apiPrefix;
    @Autowired private DashboardService dashboardService;
    @Autowired private UserAccountClientService userAccountClientService;

    /**
     * Gets all of the sprints and returns it in a ResponseEntity
     * @param projectId The Id of the project to get sprints from
     * @return A ResponseEntity containing a list of sprints
     */
    @RequestMapping(path="/project/{projectId}/getAllSprints", method = RequestMethod.GET)
    public ResponseEntity<List<Sprint>> getAllSprints(
            @PathVariable("projectId") int projectId) {
        List<Sprint> listSprints = sprintService.getSprintByProject(projectId);
        return ResponseEntity.status(HttpStatus.OK).body(listSprints);
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
        if (!userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return null;
        try {
            Project project = new Project();
            project.setProjectId(projectId);
            project.setStartDate(Date.valueOf(startDate));
            project.setEndDate(Date.valueOf(endDate));
            dashboardService.verifyProject(project);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }
}
