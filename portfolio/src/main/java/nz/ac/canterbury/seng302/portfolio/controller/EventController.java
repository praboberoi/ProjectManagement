package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;

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

    @Value("${apiPrefix}") private String apiPrefix;

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
            eventService.verifyEvent(event);
            message = eventService.saveEvent(event);
            ra.addFlashAttribute("messageSuccess", message);

        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
        }
        return "redirect:/project/{projectId}";
    }
}


