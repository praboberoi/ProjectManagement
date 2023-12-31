package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.dto.MilestoneDTO;
import nz.ac.canterbury.seng302.portfolio.model.notifications.MilestoneNotification;
import nz.ac.canterbury.seng302.portfolio.service.MilestoneService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.portfolio.utils.WebSocketPrincipal;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

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
     * @return the project page
     */
    @PostMapping(path = "/project/{projectId}/milestone")
    public ResponseEntity<String> saveMilestone(
            @ModelAttribute MilestoneDTO milestoneDTO,
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("projectId") int projectId) {
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

    /**
     * Deletes the milestone and redirects back to project page
     * @param projectId Of type int
     * @param milestoneId Of type int
     * @param principal Of type {@link AuthState}
     * @return response entity of request outcome
     */
    @DeleteMapping(path="/project/{projectId}/milestone/{milestoneId}/delete")
    public ResponseEntity<String> deleteMilestone(
            @PathVariable("milestoneId") int milestoneId,
            @PathVariable int projectId,
            @AuthenticationPrincipal AuthState principal) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient Permissions");

        try {
            String message = milestoneService.deleteMilestone(milestoneId);
            logger.info("Milestone {} has been deleted.", milestoneId);
            notifyMilestone(projectId, milestoneId, "deleted");
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (IncorrectDetailsException e) {
            logger.info("Milestone {} could not be deleted.", milestoneId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Receives update messages from the client where the changes to the milestones are made and notifies all the other
     * clients subscribed for updates
     * @param notification Notification containing the milestone ID and project ID that is being edited
     * @param principal Authentication information containing user info
     * @param sessionId Session ID of the websocket communication
     */
    @MessageMapping("/milestone/edit")
    public void editing(MilestoneNotification notification, @AuthenticationPrincipal WebSocketPrincipal principal, @Header("simpSessionId") String sessionId) {
        notification.setUsername(principal.getName());
        notification.setSessionId(sessionId);
        if (notification.isActive()) {
            template.convertAndSend(String.format(NOTIFICATION_DESTINATION, notification.getProjectId()),
                    String.format("milestone%d %s %s", notification.getMilestoneId(),"editing", notification.getUsername()));
            editing.add(notification);
        } else {
            template.convertAndSend(String.format(NOTIFICATION_DESTINATION,notification.getProjectId()),
                    String.format(NOTIFICATION_WITHOUT_USERNAME,notification.getMilestoneId(), "finished"));
            editing.remove(notification);
        }
    }

    /**
     * Detects when a websocket disconnects to remove them from the list of editors for milestones
     * @param event Websocket disconnect event
     */
    @EventListener
    public void onApplicationEvent(SessionDisconnectEvent event) {
        editing.stream().filter(notification -> notification.getSessionId().equals(event.getSessionId()))
                .forEach(notification -> template.convertAndSend("/element/project/" + notification.getProjectId() +
                        "/milestone", ("milestone" + notification.getMilestoneId() + " finished")));
        editing.removeIf(notification -> notification.getSessionId().equals(event.getSessionId()));
    }
}
