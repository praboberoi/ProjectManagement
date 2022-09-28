package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.dto.MilestoneDTO;
import nz.ac.canterbury.seng302.portfolio.model.notifications.MilestoneNotification;
import nz.ac.canterbury.seng302.portfolio.service.MilestoneService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controller for the milestones
 */
@Controller
public class MilestoneController {
    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SimpMessagingTemplate template;

    private Logger logger = LoggerFactory.getLogger(MilestoneController.class);
    private static Set<MilestoneNotification> editing = new HashSet<>();
    private static final String NOTIFICATION_DESTINATION = "/element/project/%d/milestones";
    private static final String NOTIFICATION_WITHOUT_USERNAME = "milestone%d %s";

    public MilestoneController(MilestoneService milestoneService, ProjectService projectService) {
        this.milestoneService = milestoneService;
        this.projectService = projectService;
    }

    /**
     * Return the html component which contains the specified project's milestones
     * @param projectId Project containing the desired milestones
     * @return Page fragment containing milestones
     */
    @GetMapping(path="/project/{projectId}/milestones")
    public ModelAndView milestones(@PathVariable("projectId") int projectId) {
        Project project;
        ModelAndView mv;
        try {
            project = projectService.getProjectById(projectId);
        } catch (IncorrectDetailsException e) {
            mv = new ModelAndView("error");
            mv.addObject("errorMessage", String.format("Project %s does not exist", projectId));
            mv.setStatus(HttpStatus.NOT_FOUND);
            return mv;
        }

        List<Milestone> listMilestones = milestoneService.getMilestonesByProject(project);
        listMilestones.forEach(milestoneService::updateMilestoneColor);

        mv = new ModelAndView("milestoneFragments::milestoneTab");
        mv.addObject("project", project);
        mv.addObject("listMilestones", listMilestones);
        mv.addObject("editMilestoneNotifications", editing);
        return mv;
    }

    /**
     * Checks if milestone dates are valid and if it is saves the milestone
     * 
     * @param milestoneDTO MilestoneDTO object
     * @param principal    Current user
     * @param projectId    ID of the project
     * @param ra           Redirect Attribute frontend message object
     * @return the project page
     */
    @PostMapping(path = "/project/{projectId}/milestone")
    public ResponseEntity<String> saveMilestone(
            @ModelAttribute MilestoneDTO milestoneDTO,
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("projectId") int projectId,
            RedirectAttributes ra) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient Permissions");
        }

        try {
            milestoneDTO.setProject(projectService.getProjectById(projectId));
            Milestone milestone = new Milestone(milestoneDTO);
            milestoneService.saveMilestone(milestone);
            logger.info("Milestone {} has been edited", milestone.getMilestoneId());
            notifyMilestone(projectId, milestone.getMilestoneId(), "edited");
        } catch (IncorrectDetailsException ex) {
            logger.info("Milestone {} could not be edited", milestoneDTO.getMilestoneId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }

        if (milestoneDTO.getMilestoneId() == 0) {
            return ResponseEntity.status(HttpStatus.OK).body("Successfully Created " + milestoneDTO.getName());
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("Successfully Updated " + milestoneDTO.getName());
        }
    }

    /**
     * Sends an update message to all clients connected to the websocket
     * 
     * @param projectId   Id of the event's project updated
     * @param milestoneId Id of the event edited
     * @param action      The action taken (delete, created, edited)
     */
    private void notifyMilestone(int projectId, int milestoneId, String action) {
        template.convertAndSend(String.format(NOTIFICATION_DESTINATION, projectId),
                String.format(NOTIFICATION_WITHOUT_USERNAME, milestoneId, action));
    }
}