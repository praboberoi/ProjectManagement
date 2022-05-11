package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * A controller which accepts api calls and directs it to the correct service
 */
@Controller
public class ProjectController {
    @Autowired private SprintService sprintService;
    @Value("${apiPrefix}") private String apiPrefix;

    /**
     * Gets all of the sprints and returns it in a ResponseEntity
     * @param projectId The Id of the project to get sprints from
     * @return A ResponseEntity containing a list of sprints
     */
    @GetMapping("${apiPrefix}/project/{projectId}/getAllSprints")
    public ResponseEntity<List<Sprint>> getAllSprints(
            @PathVariable("projectId") int projectId) {
        List<Sprint> listSprints = sprintService.getSprintByProject(projectId);
        return ResponseEntity.status(HttpStatus.OK).body(listSprints);
    }
}
