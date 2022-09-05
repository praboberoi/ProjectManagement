package nz.ac.canterbury.seng302.portfolio.controller;
import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.notifications.DeadlineNotification;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.portfolio.utils.WebSocketPrincipal;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashSet;
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
    private UserAccountClientService userAccountClientService;
    @Autowired
    private SimpMessagingTemplate template;
    private Logger logger = LoggerFactory.getLogger(DeadlineController.class);
    private static Set<DeadlineNotification> editing = new HashSet<>();
    private static final String PROJECT_REDIRECT = "redirect:/project/{projectId}";
    private static final String SUCCESS_MESSAGE = "messageSuccess";
    private static final String FAILURE_MESSAGE = "messageDanger";


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
        Project project = new Project();
        project.setProjectId(projectId);
        ModelAndView mv = new ModelAndView("deadlineFragments::deadlineTab");
        mv.addObject("project", project);
        mv.addObject("listDeadlines", listDeadlines);
        mv.addObject("editDeadlineNotifications", editing);
        return mv;
    }

    private void notifyDeadline(int projectId, int deadlineId, String action) {
        template.convertAndSend("/element/project/" + projectId + "/deadlines", ("deadline" + deadlineId + " " + action));
    }

    /**
     * Checks if deadline dates are valid and if it is saves the deadline
     * @param deadline Deadline object
     * @param principal Current user
     * @param projectId ID of the project
     * @param ra Redirect Attribute frontend message object
     * @return the project page
     */
    @PostMapping(path = "/project/{projectId}/saveDeadline")
    public String saveDeadline(
            @ModelAttribute Deadline deadline,
            @AuthenticationPrincipal AuthState principal,
            @PathVariable ("projectId") int projectId,
            RedirectAttributes ra) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return "redirect:/project/" + projectId;
        String message = "";
        try {
            deadline.setProject(projectService.getProjectById(projectId));
            deadlineService.verifyDeadline(deadline);
            message = deadlineService.saveDeadline(deadline);
            logger.info("Deadline {} has been edited", deadline.getDeadlineId());
            notifyDeadline(projectId, deadline.getDeadlineId(), "edited");
            ra.addFlashAttribute(SUCCESS_MESSAGE, message);
        } catch (IncorrectDetailsException ex) {
            logger.info("Deadline {} could not be edited", deadline.getDeadlineId());
            ra.addFlashAttribute(FAILURE_MESSAGE, ex.getMessage());
        }
        return PROJECT_REDIRECT;
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
     * Sends an update message to every client subscribed to /deadline/edit when a user starts editing a deadline
     * @param notification Notification containing the deadline ID and project ID that is being edited
     * @param principal Authentication information containing user info
     * @param sessionId Session ID of the websocket communication
     */
    @MessageMapping("/deadline/edit")
    public void editing(DeadlineNotification notification, @AuthenticationPrincipal WebSocketPrincipal principal, @Header("simpSessionId") String sessionId) {
        notification.setUsername(principal.getName());
        notification.setSessionId(sessionId);
        if (notification.isActive()) {
            template.convertAndSend("/element/project/" + notification.getProjectId() + "/deadlines",
                    ("deadline" + notification.getDeadlineId() + " editing " + notification.getUsername()));
            editing.add(notification);
        } else {
            template.convertAndSend("/element/project/" + notification.getProjectId() + "/deadlines",
                    ("deadline" + notification.getDeadlineId() + " finished"));
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