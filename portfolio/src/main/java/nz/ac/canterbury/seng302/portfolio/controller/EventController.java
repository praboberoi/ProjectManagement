package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for the events page
 */
@Controller
public class EventController {
    @Autowired
    private EventService eventService;
    @Autowired
    private ProjectService projectService;
    @Value("${apiPrefix}") private String apiPrefix;
    private Logger logger = LoggerFactory.getLogger(EventController.class);
    @Autowired
    private SimpMessagingTemplate template;

    /**
     * Adds common model elements used by all controller methods.
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("apiPrefix", apiPrefix);
    }

    /**
     * Checks if event dates are valid and if it is saves the event
     * @param event Event object
     * @param principal Current User
     * @param ra Redirect Attribute frontend message object
     * @return link of html page to display
     */
    @PostMapping(path = "/project/{projectId}/saveEvent")
    public String saveEvent(
            @ModelAttribute Event event,
            RedirectAttributes ra,
            @AuthenticationPrincipal AuthState principal,
            @PathVariable ("projectId") int projectId) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return "redirect:/project/" + projectId;
        String message = "";
        try {
            event.setProject(projectService.getProjectById(projectId));
            eventService.verifyEvent(event);
            message = eventService.saveEvent(event);
            notifyEvent(projectId, event.getEventId(), "edited");
            ra.addFlashAttribute("messageSuccess", message);

        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
        }
        return "redirect:/project/{projectId}";
    }


    /**
     * Deletes the event and redirects back to project page
     * @param model Of type {@link Model}
     * @param projectId Of type int
     * @param eventId Of type int
     * @param principal Of type {@link AuthState}
     * @return Status code and outcome message
     */
    @DeleteMapping(path="project/{projectId}/event/{eventId}/delete")
    public ResponseEntity<String> deleteEvent(
            @PathVariable("eventId") int eventId,
            Model model,
            @PathVariable int projectId,
            @AuthenticationPrincipal AuthState principal) {
        if (!(PrincipalUtils.checkUserIsTeacherOrAdmin(principal))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient Permissions");
        }        
        try {
            String message = eventService.deleteEvent(eventId);
            logger.info("Event {} has been deleted", eventId);
            notifyEvent(projectId, eventId, "deleted");
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (IncorrectDetailsException e) {
            logger.info("Event {} was unable to delete", eventId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Sends an update message to all clients connected to the websocket
     * @param projectId Id of the event's project updated
     * @param eventId Id of the event edited
     * @param action The action taken (delete, created, edited)
     */
    private void notifyEvent(int projectId, int eventId, String action) {
        template.convertAndSend("/element/project" + projectId + "/events", ("event" + eventId + " " + action));
    }
}


