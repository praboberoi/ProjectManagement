package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


/**
 * Controller for the events page
 */
@Controller
public class EventController {
    @Autowired
    private UserAccountClientService userAccountClientService;
    @Autowired
    private EventService eventService;
    @Autowired
    private ProjectService projectService;
    @Value("${apiPrefix}") private String apiPrefix;
    private Logger logger = LoggerFactory.getLogger(EventController.class);

    /**
     * Adds common model elements used by all controller methods.
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("apiPrefix", apiPrefix);
    }


    /**
     * Opens eventForm.html and populates it with a new Event object
     * Checks for teacher or admin privileges
     * @param projectId ID of the project
     * @param principal Current user
     * @param ra Redirect Attribute frontend message object
     * @param model
     * @return link of the html page to display
     */
    @RequestMapping(path = "/project/{projectId}/newEvent", method = RequestMethod.GET)
    public String newEvent(
            Model model,
            @AuthenticationPrincipal AuthState principal,
            RedirectAttributes ra,
            @PathVariable ("projectId") int projectId) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return "redirect:/project/" + projectId;
        model.addAttribute("apiPrefix", apiPrefix);
        Event newEvent;
        Project currentProject;
        try {
            currentProject = projectService.getProjectById(projectId);
            newEvent = eventService.getNewEvent(currentProject);
            model.addAttribute("project", currentProject);
            model.addAttribute("event", newEvent);
            model.addAttribute("pageTitle", "Add New Event");
            model.addAttribute("submissionName", "Create");
            model.addAttribute("image", apiPrefix + "/icons/create-icon.svg");
            model.addAttribute("user", userAccountClientService.getUser(principal));
            model.addAttribute("projectDateMin", currentProject.getStartDate());
            model.addAttribute("projectDateMax", currentProject.getEndDate());
            return "eventForm";

        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/project/{projectId}";
        }
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
     * @param ra Of type {@link RedirectAttributes}
     * @return project.html or error.html
     */
    @PostMapping(path="/{projectId}/deleteEvent/{eventId}")
    public String deleteEvent(
            @PathVariable("eventId") int eventId,
            Model model,
            @PathVariable int projectId,
            @AuthenticationPrincipal AuthState principal,
            RedirectAttributes ra) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        try {
            String message = eventService.deleteEvent(eventId);
            ra.addFlashAttribute("messageSuccess", message);
            List<Event> listEvents = eventService.getEventByProjectId(projectId);
            model.addAttribute("listEvents", listEvents);
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
        }

        return "redirect:/project/{projectId}";
    }

    /**
     * Directs to page for editing an event
     * @param eventId ID for event being edited
     * @param projectId ID of the project
     * @param model
     * @param principal Current user
     * @param ra Redirect Attribute frontend message object
     * @return Event form page with selected event or redirect to project page on error
     */
    @RequestMapping(path="/project/{projectId}/editEvent/{eventId}", method = RequestMethod.GET)
    public String eventEditForm(
            @PathVariable("eventId") int eventId,
            @PathVariable("projectId") int projectId,
            Model model,
            @AuthenticationPrincipal AuthState principal,
            RedirectAttributes ra){
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        try {
            Project currentProject = projectService.getProjectById(projectId);
            Event event = eventService.getEvent(eventId);
            model.addAttribute("project", currentProject);
            model.addAttribute("event", event);
            model.addAttribute("pageTitle", "Edit Event: " + event.getEventName());
            model.addAttribute("submissionName", "Save");
            model.addAttribute("image", apiPrefix + "/icons/create-icon.svg");
            model.addAttribute("user", userAccountClientService.getUser(principal));
            model.addAttribute("projectDateMin", currentProject.getStartDate());
            model.addAttribute("projectDateMax", currentProject.getEndDate());
            return "eventForm";
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/project/{projectId}";
        }
    }
}


