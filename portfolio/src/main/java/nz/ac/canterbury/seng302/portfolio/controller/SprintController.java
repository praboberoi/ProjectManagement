package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.PersistenceException;
import java.sql.Date;
import java.util.List;

@Controller
public class SprintController {
    @Autowired private SprintService sprintService;
    @Autowired private ProjectService projectService;
    @Autowired private UserAccountClientService userAccountClientService;
    @Value("${apiPrefix}") private String apiPrefix;

    @Autowired
    private SimpMessagingTemplate template;

    private final String redirectDashboard =  "redirect:/dashboard";

    /**
     * Adds common model elements used by all controller methods.
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("apiPrefix", apiPrefix);
    }

    /**
     * Displays page for adding a new sprint
     * @param projectId - ID of the project selected to view Of type int
     * @param principal - Current User.
     * @param model Of type {@link Model}
     * @param ra Redirect Attribute frontend message object
     * @return New sprint form page or redirect to project if an error occurs
     */
    @GetMapping(path="/project/{projectId}/newSprint")
    public String newSprint(
            @PathVariable ("projectId") int projectId,
            @AuthenticationPrincipal AuthState principal,
            Model model,
            RedirectAttributes ra){
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return redirectDashboard;
        try {
            Project currentProject = projectService.getProjectById(projectId);
            Sprint newSprint = sprintService.getNewSprint(currentProject);
            model.addAttribute("pageTitle", "Add New Sprint");
            model.addAttribute("sprint", newSprint);
            model.addAttribute("project", currentProject);
            model.addAttribute("user", new User(userAccountClientService.getUser(principal)));
            List<String> dateRange = sprintService.getSprintDateRange(currentProject, newSprint);

            model.addAttribute("sprintDateMin", dateRange.get(0));
            model.addAttribute("sprintDateMax", dateRange.get(1));

            model.addAttribute("submissionName", "Create");
            model.addAttribute("image", apiPrefix + "/icons/create-icon.svg");
            return "sprintForm";

        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/project/{projectId}";
        }
    }


    /**
     * Checks if a sprints dates are valid and returns a Response containing a message
     * @param projectId ID of the project to check
     * @param startDate New start date of the project
     * @param endDate New end date of the project
     * @param principal Current user
     * @param label Sprint label
     * @param id Sprint Id
     * @return ResponseEntity containing a string message
     */
    @PostMapping("/project/{projectId}/verifySprint")
    public ResponseEntity<String> verifySprint(
            @PathVariable int projectId,
            String startDate,
            String endDate,
            String label,
            int id,
            @AuthenticationPrincipal AuthState principal) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return null;
        try {
            Project project = projectService.getProjectById(projectId);
            Sprint currentSprint = new Sprint.Builder()
                    .project(project)
                    .sprintId(id)
                    .sprintLabel(label)
                    .startDate(Date.valueOf(startDate))
                    .endDate(Date.valueOf(endDate))
                    .build();
            sprintService.verifySprint(currentSprint);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (IncorrectDetailsException e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    /**
     * Saves a sprint and redirects to project page
     * @param projectId ID of the project
     * @param sprint Sprint object to be saved
     * @param principal Current user
     * @param model of type {@link Model}
     * @param ra Redirect Attribute frontend message object
     * @return Project page of corrosponding projectId
     */
    @PostMapping(path="/project/{projectId}/saveSprint")
    public String saveSprint(
        @PathVariable int projectId,
        @ModelAttribute Sprint sprint,
        @AuthenticationPrincipal AuthState principal,
        Model model,
        RedirectAttributes ra) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return redirectDashboard;
        try {
            sprint.setProject(projectService.getProjectById(projectId));
            sprintService.verifySprint(sprint);
            String message = sprintService.saveSprint(sprint);
            notifySprint(projectId, sprint.getSprintId(), "edited");
            ra.addFlashAttribute("messageSuccess", message);
            return "redirect:/project/{projectId}";
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/project/{projectId}";
        } catch (PersistenceException e) {
            model.addAttribute("user", new User(userAccountClientService.getUser(principal)));
            return "error";
        }
    }

    /**
     * Directs to page for editing a sprint
     * @param sprintId ID for sprint being edited
     * @param projectId ID of the project
     * @param model
     * @param principal Current user
     * @param ra Redirect Attribute frontend message object
     * @return Sprint form page with selected sprint or redirect to project page on error
     */
    @GetMapping(path="/project/{projectId}/editSprint/{sprintId}")
    public String sprintEditForm(
            @PathVariable("sprintId") int sprintId,
            @PathVariable("projectId") int projectId,
            Model model,
            @AuthenticationPrincipal AuthState principal,
            RedirectAttributes ra){
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) {
            return redirectDashboard;
        }
        try {

            Project currentProject = projectService.getProjectById(projectId);
            Sprint sprint = sprintService.getSprint(sprintId);
            model.addAttribute("sprint", sprint);
            model.addAttribute("project", currentProject);
            model.addAttribute("pageTitle", "Edit Sprint: " + sprint.getSprintName());
            model.addAttribute("user", new User(userAccountClientService.getUser(principal)));
            model.addAttribute("sprintDateMin", currentProject.getStartDate());
            model.addAttribute("sprintDateMax", currentProject.getEndDate());
            model.addAttribute("submissionName", "Save");
            model.addAttribute("image", apiPrefix + "/icons/save-icon.svg");
            return "sprintForm";
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/project/{projectId}";
        }
    }

    /**
     * Deletes a sprint and redirects back to project page
     * @param sprintId ID of sprint being deleted
     * @param model Of type {@link Model}
     * @param projectId ID of sprint parent project
     * @param principal Current user
     * @param ra Redirect Attribute frontend message object
     * @return
     */
    @PostMapping(path="/{projectId}/deleteSprint/{sprintId}")
    public String deleteSprint(
        @PathVariable("sprintId") int sprintId,
        Model model,
        @PathVariable int projectId,
        @AuthenticationPrincipal AuthState principal,
        RedirectAttributes ra){
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return redirectDashboard;
        try {
            String message = sprintService.deleteSprint(sprintId);
            ra.addFlashAttribute("messageSuccess", message);
            sprintService.updateSprintLabels(sprintService.getSprintByProject(projectId));
            List<Sprint> listSprints = sprintService.getSprintByProject(projectId);
            model.addAttribute("listSprints", listSprints);
            return "redirect:/project/{projectId}";
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/project/{projectId}";
        } catch (PersistenceException e) {
            model.addAttribute("user", new User(userAccountClientService.getUser(principal)));
            return "error";
        } catch (Exception e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "error";
        }
    }

    /**
     * Tries to set the selected sprint's start and end date to those provided
     * @param sprintId Sprint to change
     * @param startDate New start date of the sprint
     * @param endDate New end date of the sprint
     * @param principal Current user
     * @return An error message if sprint can't save
     */
    @PostMapping("/sprint/{sprintId}/editSprint")
    public ResponseEntity<String> editSprint(
        @PathVariable("sprintId") int sprintId,
        String startDate,
        String endDate,
        @AuthenticationPrincipal AuthState principal
    ) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal))
            return ResponseEntity.status(HttpStatus.OK).body("Unable to edit sprint. Incorrect permissions.");

        try {
            Date newStartDate = new Date(Long.parseLong(startDate));
            Date newEndDate = new Date(Long.parseLong(endDate));

            Sprint sprint = sprintService.getSprint(sprintId);
            sprint.setStartDate(newStartDate);
            sprint.setEndDate(newEndDate);
            sprintService.verifySprint(sprint);
            sprintService.saveSprint(sprint);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    

    /**
     * Sends an update message to all clients connected to the websocket
     * @param projectId Id of the sprint's project updated
     * @param sprintId Id of the sprint edited
     * @param action The action taken (delete, created, edited)
     */
    public void notifySprint(int projectId, int sprintId, String action) {
        template.convertAndSend("/element/project" + projectId + "/sprint", ("sprint" + sprintId + " " + action));
    }

}
