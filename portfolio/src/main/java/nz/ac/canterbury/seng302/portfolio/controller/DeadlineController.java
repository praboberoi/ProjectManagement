package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.dto.DeadlineDTO;
import nz.ac.canterbury.seng302.portfolio.model.notifications.DeadlineNotification;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * Controller for the deadlines
 */
@Controller
public class DeadlineController {
    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SimpMessagingTemplate template;

    private Logger logger = LoggerFactory.getLogger(DeadlineController.class);
    private static Set<DeadlineNotification> editing = new HashSet<>();
    private static final String NOTIFICATION_DESTINATION = "/element/project/%d/deadlines";
    private static final String NOTIFICATION_WITHOUT_USERNAME = "deadline%d %s";


    public DeadlineController(DeadlineService deadlineService, ProjectService projectService) {
        this.deadlineService = deadlineService;
        this.projectService = projectService;
    }

    /**
     * Return the html component which contains the specified project's deadlines
     * @param projectId Project containing the desired deadlines
     * @return Page fragment containing deadlines
     */
    @GetMapping(path="/project/{projectId}/deadlines")
    public ModelAndView deadlines(@PathVariable("projectId") int projectId) {
        List<Deadline> listDeadlines = deadlineService.getDeadlineByProject(projectId);
        Hashtable<Integer, String> deadlineDateMapping = deadlineService.getSprintOccurringOnDeadlines(listDeadlines);
        Project project = new Project();
        project.setProjectId(projectId);
        ModelAndView mv = new ModelAndView("deadlineFragments::deadlineTab");
        mv.addObject("project", project);
        mv.addObject("listDeadlines", listDeadlines);
        mv.addObject("editDeadlineNotifications", editing);
        mv.addObject("deadlineDateMapping", deadlineDateMapping);
        return mv;
    }

    /**
     * Sends an update message to all clients connected to the websocket
     * @param projectId Id of the event's project updated
     * @param deadlineId Id of the event edited
     * @param action The action taken (delete, created, edited)
     */
    private void notifyDeadline(int projectId, int deadlineId, String action) {
        template.convertAndSend(String.format(NOTIFICATION_DESTINATION,projectId),String.format(NOTIFICATION_WITHOUT_USERNAME,deadlineId,action));
    }

    /**
     * Checks if deadline dates are valid and if it is saves the deadline
     * @param deadlineDTO DeadlineDTO object
     * @param principal Current user
     * @param projectId ID of the project
     * @param ra Redirect Attribute frontend message object
     * @return the project page
     */
    @PostMapping(path = "/project/{projectId}/saveDeadline")
    public ResponseEntity<String> saveDeadline(
            @ModelAttribute DeadlineDTO deadlineDTO,
            @AuthenticationPrincipal AuthState principal,
            @PathVariable ("projectId") int projectId,
            RedirectAttributes ra) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient Permissions");
        }
        Deadline deadline = new Deadline(deadlineDTO);
        String message = "";
        try {
            deadline.setProject(projectService.getProjectById(projectId));
            deadlineService.verifyDeadline(deadline);
            message = deadlineService.saveDeadline(deadline);
            logger.info("Deadline {} has been edited", deadline.getDeadlineId());
            notifyDeadline(projectId, deadline.getDeadlineId(), "edited");
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (IncorrectDetailsException ex) {
            logger.info("Deadline {} could not be edited", deadline.getDeadlineId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    /**
     * Deletes the deadline and redirects back to project page
     * @param model Of type {@link Model}
     * @param projectId Of type int
     * @param deadlineId Of type int
     * @param principal Of type {@link AuthState}
     * @param ra Of type {@link RedirectAttributes}
     * @return project.html or error.html
     */
    @DeleteMapping(path="/{projectId}/deleteDeadline/{deadlineId}")
    public ResponseEntity<String> deleteDeadline(
            @PathVariable("deadlineId") int deadlineId,
            Model model,
            @PathVariable int projectId,
            @AuthenticationPrincipal AuthState principal,
            RedirectAttributes ra) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient Permissions");

        try {
            String message = deadlineService.deleteDeadline(deadlineId);
            logger.info("Deadline {} has been deleted.", deadlineId);
            notifyDeadline(projectId, deadlineId, "deleted");
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (IncorrectDetailsException e) {
            logger.info("Deadline {} could not be deleted.", deadlineId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Receives update messages from the client where the changes to the deadlines are made and notifies all the other
     * clients subscribed for updates
     * @param notification Notification containing the deadline ID and project ID that is being edited
     * @param principal Authentication information containing user info
     * @param sessionId Session ID of the websocket communication
     */
    @MessageMapping("/deadline/edit")
    public void editing(DeadlineNotification notification, @AuthenticationPrincipal WebSocketPrincipal principal, @Header("simpSessionId") String sessionId) {
        notification.setUsername(principal.getName());
        notification.setSessionId(sessionId);
        if (notification.isActive()) {
            template.convertAndSend(String.format(NOTIFICATION_DESTINATION,notification.getProjectId()),
                    String.format("deadline%d %s %s", notification.getDeadlineId(),"editing", notification.getUsername()));
            editing.add(notification);
        } else {
            template.convertAndSend(String.format(NOTIFICATION_DESTINATION,notification.getProjectId()),
                    String.format(NOTIFICATION_WITHOUT_USERNAME,notification.getDeadlineId(), "finished"));
            editing.remove(notification);
        }
    }

    /**
     * Detects when a websocket disconnects to remove them from the list of editors
     * @param event Websocket disconnect event
     */
    @EventListener
    public void onApplicationEvent(SessionDisconnectEvent event) {
        editing.stream().filter(notification -> notification.getSessionId().equals(event.getSessionId()))
                .forEach(notification -> template.convertAndSend("/element/project/" + notification.getProjectId() +
                        "/deadlines", ("deadline" + notification.getDeadlineId() + " finished")));
        editing.removeIf(notification -> notification.getSessionId().equals(event.getSessionId()));
    }

}