package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.dto.SprintDTO;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.PersistenceException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

@Controller
public class SprintController {
    @Autowired
    private SprintService sprintService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private EventService eventService;
    @Autowired
    private DeadlineService deadlineService;
    @Value("${apiPrefix}")
    private String apiPrefix;

    private static final String PROJECT_OBJECT = "project";
    private static final String SPRINT_OBJECT = "sprint";
    private static final String LIST_EVENTS_OBJECT = "listEvents";
    private static final String LIST_DEADLINES_OBJECT = "listDeadlines";

    private static final String ERROR = "error";
    private static final String DANGER_MESSAGE = "messageDanger";
    private static final String PROJECT_REDIRECT = "redirect:/project/";

    @Autowired
    private SimpMessagingTemplate template;

    private final String redirectDashboard = "redirect:/dashboard";

    /**
     * Return the html component which contains the specified project's sprints
     * 
     * @param projectId Project containing the desired sprints
     * @return Page fragment containing sprints
     */
    @GetMapping(path = "/project/{projectId}/sprints")
    public ModelAndView sprintList(@PathVariable("projectId") int projectId) {
        List<Sprint> listSprints = sprintService.getSprintsByProject(projectId);
        Project project = new Project();
        project.setProjectId(projectId);
        ModelAndView mv = new ModelAndView("projectFragments::sprints");
        mv.addObject(PROJECT_OBJECT, project);
        mv.addObject("listSprints", listSprints);
        return mv;
    }

    /**
     * Return the html component which contains the specified project's sprints
     * 
     * @param projectId Project containing the desired sprints
     * @return Page fragment containing sprints
     */
    @GetMapping(path = "/project/{projectId}/sprint/{sprintId}/accordion")
    public ModelAndView sprintAccordion(@PathVariable("projectId") int projectId,
            @PathVariable("sprintId") int sprintId) {
        Sprint sprint;
        ModelAndView mv;
        try {
            sprint = sprintService.getSprint(sprintId);
        } catch (IncorrectDetailsException e) {
            mv = new ModelAndView(ERROR);
            mv.setStatus(HttpStatus.NOT_FOUND);
            mv.addObject("errorMessage", String.format("Sprint %d doesn't exist", sprintId));
            return mv;
        }
        List<Event> listEvents = eventService.getEventsBySprintId(sprintId);
        listEvents.forEach(eventService::updateEventColors);
        Map<Integer, List<String>> eventDateMappingDictionary = eventService
                .getSprintLabelsForStartAndEndDates(listEvents);

        List<Deadline> listDeadlines = deadlineService.getDeadlinesBySprintId(sprintId);
        listDeadlines.forEach(deadlineService::updateDeadlineColors);
        Map<Integer, String> deadlineDateMapping = deadlineService.getSprintOccurringOnDeadlines(listDeadlines);
        

        mv = new ModelAndView("projectFragments::sprintAccordion");
        mv.addObject(SPRINT_OBJECT, sprint);
        mv.addObject(LIST_EVENTS_OBJECT, listEvents);
        mv.addObject(LIST_DEADLINES_OBJECT, listDeadlines);

        mv.addObject("eventDateMappingDictionary", eventDateMappingDictionary);
        mv.addObject("deadlineDateMapping", deadlineDateMapping);

        return mv;
    }

    /**
     * Displays page for adding a new sprint
     * 
     * @param projectId - ID of the project selected to view Of type int
     * @param principal - Current User.
     * @param model     Of type {@link Model}
     * @param ra        Redirect Attribute frontend message object
     * @return New sprint form page or redirect to project if an error occurs
     */
    @GetMapping(path = "/project/{projectId}/newSprint")
    public String newSprint(
            @PathVariable("projectId") int projectId,
            @AuthenticationPrincipal AuthState principal,
            Model model,
            RedirectAttributes ra) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal))
            return redirectDashboard;
        try {
            Project currentProject = projectService.getProjectById(projectId);
            Sprint newSprint = sprintService.getNewSprint(currentProject);
            model.addAttribute("pageTitle", "Add New Sprint");
            model.addAttribute(SPRINT_OBJECT, newSprint);
            model.addAttribute(PROJECT_OBJECT, currentProject);
            List<String> dateRange = sprintService.getSprintDateRange(currentProject, newSprint);

            model.addAttribute("sprintDateMin", dateRange.get(0));
            model.addAttribute("sprintDateMax", dateRange.get(1));

            model.addAttribute("submissionName", "Create");
            model.addAttribute("image", apiPrefix + "/icons/create-icon.svg");
            return "sprintForm";

        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute(DANGER_MESSAGE, e.getMessage());
            return "redirect:/project/{projectId}";
        }
    }

    /**
     * Checks if a sprints dates are valid and returns a Response containing a
     * message
     * 
     * @param projectId ID of the project to check
     * @param principal Current user
     * @param sprint    Current sprint
     * @return ResponseEntity containing a string message
     */
    @PostMapping("/project/{projectId}/verifySprint")
    public ResponseEntity<String> verifySprint(
            @PathVariable int projectId,
            @ModelAttribute SprintDTO sprintDTO,
            @AuthenticationPrincipal AuthState principal) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient Permissions");
        }

        try {
            Project project = projectService.getProjectById(projectId);
            Sprint sprint = new Sprint(sprintDTO);

            sprint.setProject(project);
            sprintService.verifySprint(sprint);

            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (IncorrectDetailsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Saves a sprint and redirects to project page
     * 
     * @param projectId ID of the project
     * @param sprint    Sprint object to be saved
     * @param principal Current user
     * @param model     of type {@link Model}
     * @param ra        Redirect Attribute frontend message object
     * @return Project page of corrosponding projectId
     */
    @PostMapping(path = "/project/{projectId}/sprint")
    public String saveSprint(
            @PathVariable int projectId,
            @ModelAttribute SprintDTO sprintDTO,
            @AuthenticationPrincipal AuthState principal,
            Model model,
            RedirectAttributes ra) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) {
            return redirectDashboard;
        }

        Sprint sprint = new Sprint(sprintDTO);
        try {
            sprint.setProject(projectService.getProjectById(projectId));
            sprintService.verifySprint(sprint);

            String message = sprintService.saveSprint(sprint);
            notifySprint(projectId, sprint.getSprintId(), "edited");

            List<Event> listEvents = eventService.getEventByProjectId(projectId);
            listEvents.forEach(eventService::updateEventColors);

            List<Deadline> listDeadlines = deadlineService.getDeadlineByProject(projectId);
            listDeadlines.forEach(deadlineService::updateDeadlineColors);

            ra.addFlashAttribute(LIST_EVENTS_OBJECT, listEvents);
            ra.addFlashAttribute(LIST_DEADLINES_OBJECT, listDeadlines);
            ra.addFlashAttribute("messageSuccess", message);
            return PROJECT_REDIRECT + projectId;
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute(DANGER_MESSAGE, e.getMessage());
            return PROJECT_REDIRECT + projectId;
        } catch (PersistenceException e) {
            return ERROR;
        }
    }

    /**
     * Tries to set the selected sprint's start and end date to those provided
     * 
     * @param sprintId  Sprint to change
     * @param startDate New start date of the sprint
     * @param endDate   New end date of the sprint
     * @param principal Current user
     * @return An error message if sprint can't save
     */
    @PostMapping("/sprint/{sprintId}/editSprint")
    public ResponseEntity<String> editSprint(
            @PathVariable("sprintId") int sprintId,
            Date startDate,
            Date endDate,
            @AuthenticationPrincipal AuthState principal) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unable to edit sprint. Incorrect permissions.");
        }

        try {
            Sprint sprint = sprintService.getSprint(sprintId);
            sprint.setStartDate(startDate);
            sprint.setEndDate(endDate);
            sprintService.verifySprint(sprint);
            sprintService.saveSprint(sprint);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
    /**
     * Deletes a sprint and redirects back to project page
     * 
     * @param sprintId  ID of sprint being deleted
     * @param model     Of type {@link Model}
     * @param projectId ID of sprint parent project
     * @param principal Current user
     * @param ra        Redirect Attribute frontend message object
     * @return
     */
    @PostMapping(path = "project/{projectId}/deleteSprint/{sprintId}")
    public String deleteSprint(
            @PathVariable("sprintId") int sprintId,
            Model model,
            @PathVariable int projectId,
            @AuthenticationPrincipal AuthState principal,
            RedirectAttributes ra) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) {
            return redirectDashboard;
        }

        try {
            String responseMessage = sprintService.deleteSprint(sprintId);
            sprintService.updateSprintLabelsAndColor(sprintService.getSprintsByProject(projectId));
            List<Sprint> listSprints = sprintService.getSprintsByProject(projectId);

            List<Event> listEvents = eventService.getEventByProjectId(projectId);
            listEvents.forEach(eventService::updateEventColors);

            List<Deadline> listDeadlines = deadlineService.getDeadlineByProject(projectId);
            listDeadlines.forEach(deadlineService::updateDeadlineColors);

            ra.addFlashAttribute("messageSuccess", responseMessage);
            ra.addFlashAttribute(LIST_EVENTS_OBJECT, listEvents);
            ra.addFlashAttribute(LIST_DEADLINES_OBJECT, listDeadlines);
            ra.addAttribute("listSprints", listSprints);

            return PROJECT_REDIRECT + projectId;
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute(DANGER_MESSAGE, e.getMessage());
            return PROJECT_REDIRECT + projectId;
        } catch (PersistenceException e) {
            return ERROR;
        } catch (Exception e) {
            ra.addFlashAttribute(DANGER_MESSAGE, e.getMessage());
            return ERROR;
        }
    }

    /**
     * Directs to page for editing a sprint
     * 
     * @param sprintId  ID for sprint being edited
     * @param projectId ID of the project
     * @param model
     * @param principal Current user
     * @param ra        Redirect Attribute frontend message object
     * @return Sprint form page with selected sprint or redirect to project page on
     *         error
     */
    @GetMapping(path = "/project/{projectId}/editSprint/{sprintId}")
    public String sprintEditForm(
            @PathVariable("sprintId") int sprintId,
            @PathVariable("projectId") int projectId,
            Model model,
            @AuthenticationPrincipal AuthState principal,
            RedirectAttributes ra) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) {
            return redirectDashboard;
        }
        try {

            Project currentProject = projectService.getProjectById(projectId);
            Sprint sprint = sprintService.getSprint(sprintId);
            model.addAttribute(SPRINT_OBJECT, sprint);
            model.addAttribute(PROJECT_OBJECT, currentProject);
            model.addAttribute("pageTitle", "Edit Sprint: " + sprint.getSprintName());
            model.addAttribute("sprintDateMin", currentProject.getStartDate());
            model.addAttribute("sprintDateMax", currentProject.getEndDate());
            model.addAttribute("submissionName", "Save");
            model.addAttribute("image", apiPrefix + "/icons/save-icon.svg");
            return "sprintForm";
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute(DANGER_MESSAGE, e.getMessage());
            return "redirect:/project/{projectId}";
        }
    }

    /**
     * Sends an update message to all clients connected to the websocket
     * 
     * @param projectId Id of the sprint's project updated
     * @param sprintId  Id of the sprint edited
     * @param action    The action taken (delete, created, edited)
     */
    public void notifySprint(int projectId, int sprintId, String action) {
        template.convertAndSend("/element/project" + projectId + "/sprint", ("sprint" + sprintId + " " + action));
    }

}
