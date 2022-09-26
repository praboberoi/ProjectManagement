package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    @Autowired private SprintService sprintService;
    @Autowired private DashboardService dashboardService;
    @Autowired private ProjectService projectService;
    @Autowired private EventService eventService;
    @Autowired private UserAccountClientService userAccountClientService;
    @Autowired private DeadlineService deadlineService;
    @Autowired private MilestoneService milestoneService;

    private Logger logger = LoggerFactory.getLogger(ProjectController.class);

    private static final String ERROR_PAGE = "error";
    private static final String DASHBOARD_REDIRECT = "redirect:/dashboard";

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
      public ModelAndView events() {
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

            model.addAttribute("project", project);
            model.addAttribute("event", newEvent);
            model.addAttribute("deadline", newDeadline);
            model.addAttribute("milestone", newMilestone);

            model.addAttribute("roles", PrincipalUtils.getUserRole(principal));
            model.addAttribute("user", new User(userAccountClientService.getUser(principal)));
            model.addAttribute("projectDateMin", project.getStartDate());
            model.addAttribute("projectDateMax", project.getEndDate());

            return "project";
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/dashboard";
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
            dashboardService.verifyProject(project);
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
        mv.addObject("project", project);
        mv.addObject("listSprints", listSprints);
        return mv;
    }

    /**
     * Deletes the project and all the related sprints
     * @param projectId Of type int
     * @param ra Of type {@link RedirectAttributes}
     * @param model of type {@link Model}
     * @param principal of type {@link AuthState}
     * @return dashboard.html file or error.html file
     */
    @PostMapping(path="/dashboard/deleteProject/{projectId}")
    public String deleteProject(
        @PathVariable("projectId") int projectId,
        RedirectAttributes ra,
        Model model,
        @AuthenticationPrincipal AuthState principal) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return DASHBOARD_REDIRECT;
        try {
            Project project  = dashboardService.getProject(projectId);
            String message = "Successfully Deleted " + project.getProjectName();
            dashboardService.deleteProject(projectId);
            ra.addFlashAttribute("messageSuccess", message);
            return DASHBOARD_REDIRECT;
        } catch (IncorrectDetailsException e) {
            model.addAttribute("user", userAccountClientService.getUser(principal));
            return ERROR_PAGE;
        }
    }

    /**
     * Saves project object to the database and redirects to dashboard page
     * @param project Of type {@link Project}
     * @param model Of type {@link Model}
     * @param ra Of type {@link RedirectAttributes}
     * @param principal Of type {@link AuthState}
     * @return Either the dashboard.html file or error.html file
     */
    @PostMapping(path="/dashboard/saveProject")
    public String saveProject(
            Project project,
            Model model,
            RedirectAttributes ra,
            @AuthenticationPrincipal AuthState principal) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return DASHBOARD_REDIRECT;
        try {
            dashboardService.verifyProject(project);
            String message =  dashboardService.saveProject(project);
            ra.addFlashAttribute("messageSuccess", message);
            logger.info("Project {} has been created by user {}", project.getProjectId(), PrincipalUtils.getUserId(principal));
            return DASHBOARD_REDIRECT;
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return DASHBOARD_REDIRECT;
        } catch (Exception e) {
            model.addAttribute("user", userAccountClientService.getUser(principal));
            logger.error("An error occured while saving a project.", e);
            return ERROR_PAGE;
        }
    }

    /**
     * Maps an existing project, current user, user's role and button info to projectForm.html
     * @param projectId Of type int
     * @param model Of type {@link Model}
     * @param ra Of type {@link RedirectAttributes}
     * @param principal Of type {@link AuthState}
     * @return projectForm.html file or dashboard.html file
     */
    @GetMapping(path="/dashboard/editProject/{projectId}")
    public String showEditForm(
        @PathVariable(
        value = "projectId") int projectId,
        Model model,
        RedirectAttributes ra,
        @AuthenticationPrincipal AuthState principal) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return DASHBOARD_REDIRECT;
        try {
            Project project  = dashboardService.getProject(projectId);
            List<Date> dateRange = dashboardService.getProjectDateRange(project);
            model.addAttribute("project", project);
            model.addAttribute("pageTitle", "Edit Project: " + project.getProjectName());
            model.addAttribute("submissionName", "Save");
            model.addAttribute("image", "/icons/save-icon.svg");
            model.addAttribute("user", new User(userAccountClientService.getUser(principal)));
            model.addAttribute("projectStartDateMin", dateRange.get(0));
            model.addAttribute("projectStartDateMax", Date.valueOf(project.getEndDate().toLocalDate().minusDays(1)));
            model.addAttribute("projectEndDateMin", Date.valueOf(project.getStartDate().toLocalDate().plusDays(1)));
            model.addAttribute("projectEndDateMax", dateRange.get(1));
            return "projectForm";
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
                return DASHBOARD_REDIRECT;
        }
    }

}
