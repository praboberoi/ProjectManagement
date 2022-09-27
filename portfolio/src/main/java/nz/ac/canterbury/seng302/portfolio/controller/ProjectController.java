package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.model.dto.ProjectDTO;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.util.List;

/**
 * A controller which accepts api calls and directs it to the correct service
 */
@Controller
public class ProjectController {
    @Autowired 
    private SprintService sprintService;
    @Autowired 
    private ProjectService projectService;
    @Autowired 
    private EventService eventService;
    @Autowired 
    private UserAccountClientService userAccountClientService;
    @Autowired 
    private DeadlineService deadlineService;
    @Autowired 
    private MilestoneService milestoneService;

    @Autowired
    private SimpMessagingTemplate template;

    private Logger logger = LoggerFactory.getLogger(ProjectController.class);

    private static final String PROJECT_PAGE = "project";
    private static final String PROJECT_OBJECT = "project";

    private static final String DASHBOARD_REDIRECT = "redirect:/dashboard";
    private static final String NOTIFICATION_DESTINATION = "/element/project";

    /**
     * Gets all of the sprints and returns it in a ResponseEntity
     * @param projectId The Id of the project to get sprints from
     * @return A ResponseEntity containing a list of sprints
     */
    @GetMapping(path="/project/{projectId}/getAllSprints")
    public ResponseEntity<List<Sprint>> getAllSprints(
            @PathVariable("projectId") int projectId) {
        List<Sprint> listSprints = sprintService.getSprintByProject(projectId);
        return ResponseEntity.status(HttpStatus.OK).body(listSprints);
    }

        /**
     * Return the html component which contains the specified project's events
      * @param projectId Project containing the desired events
      * @return Page fragment containing events
      */
      @GetMapping(path="/projects")
      public ModelAndView projectList() {
          List<Project> listProjects = projectService.getAllProjects();

          ModelAndView mv = new ModelAndView("dashboard::projectList");
          mv.addObject("listProjects", listProjects);
          return mv;
      }

    /**
     * Add project details, sprints, and current user roles (to determine access to add, edit, delete sprints)
     * to the individual project pages.
     * @param projectId ID of the project selected to view
     * @param principal Current User of type {@link AuthState}
     * @param model Of type {@link Model}
     * @param ra Redirect Attribute frontend message object
     * @return - name of the html page to display
     */
    @GetMapping(path="/project/{projectId}")
    public String showProject(
            @PathVariable("projectId") int projectId,
            @AuthenticationPrincipal AuthState principal,
            Model model,
            RedirectAttributes ra) {
        try {
            Project project = projectService.getProjectById(projectId);

            List<Sprint> listSprints = sprintService.getSprintByProject(projectId);

            List<Event> listEvents = eventService.getEventByProjectId(projectId);
            listEvents.forEach(eventService::updateEventColors);

            List<Deadline> listDeadlines = deadlineService.getDeadlineByProject(projectId);
            listDeadlines.forEach(deadlineService::updateDeadlineColors);

            List<Milestone> listMilestones = milestoneService.getMilestonesByProject(project);
            listMilestones.forEach(milestoneService::updateMilestoneColor);

            Event newEvent = eventService.getNewEvent(project);
            Deadline newDeadline = deadlineService.getNewDeadline(project);
            Milestone newMilestone = milestoneService.getNewMilestone(project);

            model.addAttribute("listEvents", listEvents);
            model.addAttribute("listDeadlines", listDeadlines);
            model.addAttribute("listMilestones", listMilestones);
            model.addAttribute("listSprints", listSprints);

            model.addAttribute(PROJECT_OBJECT, project);
            model.addAttribute("event", newEvent);
            model.addAttribute("deadline", newDeadline);
            model.addAttribute("milestone", newMilestone);

            model.addAttribute("projectDateMin", project.getStartDate());
            model.addAttribute("projectDateMax", project.getEndDate());

            return PROJECT_PAGE;
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return DASHBOARD_REDIRECT;
        }
    }

    /**
     * Checks if a sprints dates are valid and returns a Response containing a message
     * @param projectId ID of the project to check
     * @param startDate New start date of the project
     * @param endDate New end date of the project
     * @param principal
     * @return ResponseEntity containing a string message
     */
    @PostMapping("/verifyProject/{projectId}")
    public ResponseEntity<String> verifyProject(
            @PathVariable int projectId,
            String startDate,
            String endDate,
            @AuthenticationPrincipal AuthState principal) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return null;
        try {
            Project project = new Project.Builder()
                    .projectId(projectId)
                    .startDate(Date.valueOf(startDate))
                    .endDate(Date.valueOf(endDate))
                    .build();
            projectService.verifyProject(project);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (IncorrectDetailsException e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    /**
     * Return the html component which contains the specified project's sprints
      * @param projectId Project containing the desired sprints
      * @return Page fragment containing sprints
      */
    @GetMapping(path="/project/{projectId}/sprints")
    public ModelAndView groupsList(@PathVariable("projectId") int projectId) {
        List<Sprint> listSprints = sprintService.getSprintByProject(projectId);
        Project project = new Project();
        project.setProjectId(projectId);
        ModelAndView mv = new ModelAndView("project::sprints");
        mv.addObject(PROJECT_OBJECT, project);
        mv.addObject("listSprints", listSprints);
        return mv;
    }

    /**
     * Deletes the project and all the related sprints
     * @param projectId Of type int
     * @param principal of type {@link AuthState}
     * @return dashboard.html file or error.html file
     */
    @DeleteMapping(path="/project/{projectId}")
    public ResponseEntity<String> deleteProject(
        @PathVariable("projectId") int projectId,
        @AuthenticationPrincipal AuthState principal) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient Permissions");
        }

        Project project;

        try {
            project = projectService.getProject(projectId);
        } catch (IncorrectDetailsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        projectService.deleteProject(projectId);
        notifyProject(projectId, "deleted");

        logger.info("Project {} has been deleted by user {}", project.getProjectId(), PrincipalUtils.getUserId(principal));
        return ResponseEntity.status(HttpStatus.OK).body("Successfully Deleted " + project.getProjectName());
    }

    /**
     * Saves project object to the database and redirects to dashboard page
     * @param project Of type {@link Project}
     * @param model Of type {@link Model}
     * @param ra Of type {@link RedirectAttributes}
     * @param principal Of type {@link AuthState}
     * @return Either the dashboard.html file or error.html file
     */
    @PostMapping(path="/project")
    public ResponseEntity<String> saveProject(
            @ModelAttribute ProjectDTO projectDTO,
            @AuthenticationPrincipal AuthState principal) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient Permissions");
        }
        Project project = new Project(projectDTO);

        try {
            projectService.verifyProject(project);
            String message =  projectService.saveProject(project);

            notifyProject(project.getProjectId(), "edited");
            logger.info("Project {} has been created by user {}", project.getProjectId(), PrincipalUtils.getUserId(principal));
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (IncorrectDetailsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An error occurred while saving a project.", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Sends an update message to all clients connected to the websocket
     * 
     * @param projectId   Id of the event's project updated
     * @param action      The action taken (delete, created, edited)
     */
    private void notifyProject(int projectId, String action) {
        template.convertAndSend(String.format(NOTIFICATION_DESTINATION, projectId),
                String.format("project%d %s", projectId, action));
    }

}
