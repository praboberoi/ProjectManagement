package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.notifications.EditNotification;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.portfolio.utils.WebSocketPrincipal;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

/**
 * Controller for the events page
 */
@Controller
public class EventController {
    @Autowired
    private EventService eventService;
    @Autowired
    private ProjectService projectService;
    private Logger logger = LoggerFactory.getLogger(EventController.class);
    @Autowired
    private SimpMessagingTemplate template;

    private static final String PROJECT_REDIRECT = "redirect:/project/{projectId}";
    private static final String SUCCESS_MESSAGE = "messageSuccess";
    private static final String FAILURE_MESSAGE = "messageDanger";

    private static Set<EditNotification> editing = new HashSet<>();

    /**
     * Return the html component which contains the specified project's events
      * @param projectId Project containing the desired events
      * @return Page fragment containing events
      */
      @GetMapping(path="/project/{projectId}/events")
      public ModelAndView events(@PathVariable("projectId") int projectId) {
          List<Event> listEvents = eventService.getEventByProjectId(projectId);
          ModelAndView mv = new ModelAndView("eventFragments::projectList");
          mv.addObject("listEvents", listEvents);
          mv.addObject("editNotifications", editing);
          return mv;
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
            ra.addFlashAttribute(SUCCESS_MESSAGE, message);

        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute(FAILURE_MESSAGE, e.getMessage());
        }
        return PROJECT_REDIRECT;
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
        template.convertAndSend("/element/project/" + projectId + "/events", ("event" + eventId + " " + action));
    }

    @MessageMapping("/event/edit")
    public void editing(EditNotification notification, @AuthenticationPrincipal WebSocketPrincipal principal, @Header("simpSessionId") String sessionId) {
        notification.setUsername(principal.getName());
        notification.setSessionId(sessionId);
        if (notification.isActive()) {
            template.convertAndSend("/element/project/" + notification.getProjectId() + "/events", ("event" + notification.getEventId() + " editing " + notification.getUsername()));
            editing.add(notification);
        } else {
            template.convertAndSend("/element/project/" + notification.getProjectId() + "/events", ("event" + notification.getEventId() + " finished"));
            editing.remove(notification);
        }
    }

    @EventListener
    public void onApplicationEvent(SessionDisconnectEvent event) {
        editing.stream().filter(notification->notification.getSessionId().equals(event.getSessionId()))
        .forEach(notification->template.convertAndSend("/element/project/" + notification.getProjectId() + "/events", ("event" + notification.getEventId() + " finished")));
        editing.removeIf(notification->notification.getSessionId().equals(event.getSessionId()));
    }
}


