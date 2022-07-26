package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.util.List;

@Controller
public class SprintController {
    @Autowired
    private SprintService sprintService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserAccountClientService userAccountClientService;
    @Value("${apiPrefix}")
    private String apiPrefix;

    /**
     * Add project details, sprints, and current user roles (to determine access to add, edit, delete sprints)
     * to the individual project pages.
     * @param projectId - ID of the project selected to view.
     * @param principal - Current User.
     * @param model
     * @param ra Redirect Attribute frontend message object
     * @return - name of the html page to display
     */
    @RequestMapping(path="/project/{projectId}", method = RequestMethod.GET)
    public String showSprintList(
            @PathVariable("projectId") int projectId,
            @AuthenticationPrincipal AuthState principal,
            Model model,
            RedirectAttributes ra) {
        try {
            List<Sprint> listSprints = sprintService.getSprintByProject(projectId);
            Project project = projectService.getProjectById(projectId);
            model.addAttribute("apiPrefix", apiPrefix);
            model.addAttribute("listSprints", listSprints);
            model.addAttribute("project", project);
            model.addAttribute("roles", userAccountClientService.getUserRole(principal));
            model.addAttribute("user", userAccountClientService.getUser(principal));
            return "project";
        } catch (Exception e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/dashboard";
        }
    }

    /**
     * Displays page for adding a new sprint
     * @param projectId - ID of the project selected to view.
     * @param principal - Current User.
     * @param model
     * @param ra Redirect Attribute frontend message object
     * @return New sprint form page or redirect to project is error occurs
     */
    @RequestMapping(path="/project/{projectId}/newSprint", method = RequestMethod.GET)
    public String newSprint(
            @PathVariable ("projectId") int projectId,
            @AuthenticationPrincipal AuthState principal,
            Model model,
            RedirectAttributes ra){
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        try {
            Project currentProject = projectService.getProjectById(projectId);
            Sprint newSprint = sprintService.getNewSprint(currentProject);
            model.addAttribute("apiPrefix", apiPrefix);
            model.addAttribute("pageTitle", "Add New Sprint");
            model.addAttribute("sprint", newSprint);
            model.addAttribute("project", currentProject);
            model.addAttribute("user", userAccountClientService.getUser(principal));

            model.addAttribute("sprintDateMin", currentProject.getStartDate());
            model.addAttribute("sprintDateMax", currentProject.getEndDate());

            model.addAttribute("submissionName", "Create");
            model.addAttribute("image", apiPrefix + "/icons/create-icon.svg");
            return "sprintForm";

        } catch (Exception e) {
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
     * @return ResponseEntity containing a string message
     */
    @PostMapping("/project/{projectId}/verifySprint")
    public ResponseEntity<String> verifySprint(
            @PathVariable int projectId,
            String startDate,
            String endDate,
            @AuthenticationPrincipal AuthState principal) {
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return null;
        Sprint currentSprint = new Sprint();
        try {
            Project project = projectService.getProjectById(projectId);
            currentSprint.setProject(project);
            currentSprint.setStartDate(Date.valueOf(startDate));
            currentSprint.setEndDate(Date.valueOf(endDate));
            sprintService.verifySprint(currentSprint);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    /**
     * Saves a sprint and redirects to project page
     * @param projectId ID of the project
     * @param sprint Sprint object to be saved
     * @param principal Current user
     * @param ra Redirect Attribute frontend message object
     * @return Project page of corrosponding projectId
     */
    @PostMapping(path="/project/{projectId}/saveSprint")
    public String saveSprint(
        @PathVariable int projectId,
        @ModelAttribute Sprint sprint,
        @AuthenticationPrincipal AuthState principal,
        RedirectAttributes ra) {
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        try {
            sprint.setProject(projectService.getProjectById(projectId));
            sprintService.verifySprint(sprint);
            String message = sprintService.saveSprint(sprint);
            ra.addFlashAttribute("messageSuccess", message);
        } catch (Exception e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
        }
            return "redirect:/project/{projectId}";
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
    /*make sure to update project.html for path*/
    @RequestMapping(path="/project/{projectId}/editSprint/{sprintId}", method = RequestMethod.GET)
    public String sprintEditForm(
            @PathVariable("sprintId") int sprintId,
            @PathVariable("projectId") int projectId,
            Model model,
            @AuthenticationPrincipal AuthState principal,
            RedirectAttributes ra){
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        try {
            Project currentProject = projectService.getProjectById(projectId);
            Sprint sprint = sprintService.getSprint(sprintId);
            model.addAttribute("apiPrefix", apiPrefix);
            model.addAttribute("sprint", sprint);
            model.addAttribute("project", currentProject);
            model.addAttribute("pageTitle", "Edit Sprint: " + sprint.getSprintName());
            model.addAttribute("user", userAccountClientService.getUser(principal));
            model.addAttribute("sprintDateMin", currentProject.getStartDate());
            model.addAttribute("sprintDateMax", currentProject.getEndDate());
            model.addAttribute("submissionName", "Save");
            model.addAttribute("image", apiPrefix + "/icons/save-icon.svg");
            return "sprintForm";
            } catch (Exception e) {
                ra.addFlashAttribute("messageDanger", e.getMessage());
                return "redirect:/project/{projectId}";
            }
        }


    /**
     * Deletes a sprint and redirects back to project page
     * @param sprintId ID of sprint being deleted
     * @param model
     * @param projectId ID of sprint parent project
     * @param principal Current user
     * @param ra Redirect Attribute frontend message object
     * @return
     */
    @RequestMapping(path="/{projectId}/deleteSprint/{sprintId}", method = RequestMethod.POST)
    public String deleteSprint(
        @PathVariable("sprintId") int sprintId,
        Model model,
        @PathVariable int projectId,
        @AuthenticationPrincipal AuthState principal,
        RedirectAttributes ra){
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        try {
            model.addAttribute("apiPrefix", apiPrefix);
            String message = sprintService.deleteSprint(sprintId);
            ra.addFlashAttribute("messageSuccess", message);
        } catch (Exception e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
        }
        sprintService.updateSprintLabels(sprintService.getSprintByProject(projectId));
        List<Sprint> listSprints = sprintService.getSprintByProject(projectId);
        model.addAttribute("listSprints", listSprints);
        model.addAttribute("apiPrefix", apiPrefix);
        return "redirect:/project/{projectId}";
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
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal))
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


}
