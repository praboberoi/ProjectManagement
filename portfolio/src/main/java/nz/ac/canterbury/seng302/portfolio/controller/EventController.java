package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.*;
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
    @Value("${apiPrefix}")
    private String apiPrefix;
    private Logger logger = LoggerFactory.getLogger(EventController.class);


    /**
     * Opens eventForm.html and populates it with a new Event object
     * Checks for teacher or admin privileges
     * @param projectId ID of the project
     * @param principal Current user
     * @param model
     * @return link of the html page to display
     */
    @RequestMapping(path = "/project/{projectId}/newEvent", method = RequestMethod.GET)
    public String newEvent(
            Model model,
            @AuthenticationPrincipal AuthState principal,
            @PathVariable ("projectId") int projectId) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return "redirect:/project/" + projectId;
        model.addAttribute("apiPrefix", apiPrefix);
        Event newEvent = eventService.getNewEvent();
        if (newEvent == null) return "redirect:/project/{projectId}";
        Project currentProject;
        try {
            currentProject = projectService.getProjectById(projectId);
        } catch (Exception e) {
            e.getMessage();
//            TODO: redirect to error page when merged
            return "redirect:/project/{projectId}";
        }

        model.addAttribute("project", currentProject);
        model.addAttribute("event", newEvent);
        model.addAttribute("pageTitle", "Add New Event");
        model.addAttribute("submissionName", "Create");
        model.addAttribute("image", apiPrefix + "/icons/create-icon.svg");
        model.addAttribute("user", userAccountClientService.getUser(principal));
        model.addAttribute("projectDateMin", currentProject.getStartDate());
        model.addAttribute("projectDateMax", currentProject.getEndDate());
        return "eventForm";
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
            message = eventService.verifyEvent(event);
            if (message != "Event has been verified") {
                ra.addFlashAttribute("messageDanger", message);
            } else {
                message = eventService.saveEvent(event);
                logger.info("Event {} has been created by user {}", event.getEventId(), PrincipalUtils.getUserId(principal));
                ra.addFlashAttribute("messageSuccess", message);
            }
        } catch (Exception e) {
            logger.error("An error occured while creating an event.", e);
            ra.addFlashAttribute("messageDanger", "Internal Server Error: The event could not be saved, please try again later.");
        }
        return "redirect:/project/{projectId}";
    }
}


