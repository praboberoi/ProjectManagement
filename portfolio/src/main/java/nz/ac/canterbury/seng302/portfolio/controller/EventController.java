package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for the events page
 */
@Controller
public class EventController {
    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private UserAccountClientService userAccountClientService;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private EventService eventService;
    @Autowired
    private ProjectService projectService;
    @Value("${apiPrefix}")
    private String apiPrefix;


    /**
     * Opens eventForm.html and populates it with a new Event object
     * Checks for teacher or admin privileges
     * @param model
     * @return
     */
    @RequestMapping(path = "/project/{projectId}/newEvent", method = RequestMethod.GET)
    public String showNewForm(Model model, @AuthenticationPrincipal AuthState principal, @PathVariable ("projectId") int projectId) {
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return "redirect:/project/{projectId}";
        model.addAttribute("apiPrefix", apiPrefix);
        Event newEvent = eventService.getNewEvent();
        if (newEvent == null) return "redirect:/project/{projectId}";
        Project currentProject = null;
        try {
            currentProject = projectService.getProjectById(projectId);
        } catch (Exception e) {
            e.getMessage();
//            TODO: redirect to error page when merged
            return "redirect:/project/{projectId}";
        }

        model.addAttribute("event", newEvent);
        model.addAttribute("pageTitle", "Add New Event");
        model.addAttribute("submissionName", "Create");
        model.addAttribute("image", apiPrefix + "/icons/create-icon.svg");
        model.addAttribute("user", userAccountClientService.getUser(principal));
        model.addAttribute("projectDateMin", currentProject.getStartDate());
        model.addAttribute("projectDateMax", currentProject.getEndDate());
        return "eventForm";
    }

    @PostMapping(path = "/project/{projectId}/saveEvent")
    public String saveEvent(
            @PathVariable int projectId,
            @ModelAttribute Event event,
            @AuthenticationPrincipal AuthState principal,
            RedirectAttributes ra) {
        try {
            eventService.verifyEvent(event);
            String message = eventService.saveEvent(event);
            ra.addFlashAttribute("messageSuccess",message);
        } catch (Exception e) {
            ra.addFlashAttribute("messageDanger",message);


        }
        return "redirect:/project/{projectId}";
    }
}


