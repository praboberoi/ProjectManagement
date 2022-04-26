package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class SprintController {
    @Autowired private SprintService sprintService;
    @Autowired private ProjectService projectService;
    @Autowired private UserAccountClientService userAccountClientService;

    /**
     * Add project details, sprints, and current user roles (to determine access to add, edit, delete sprints)
     * to the individual project pages.
     * @param projectId - ID of the project selected to view.
     * @param principal - Current User.
     * @param model
     * @return - name of the html page to display
     */
    @GetMapping("/project/{projectId}")
    public String showSprintList(
            @PathVariable("projectId") int projectId,
            @AuthenticationPrincipal AuthState principal,
            Model model,
            RedirectAttributes ra) {
        try {
            List<Sprint> listSprints = sprintService.getSprintByProject(projectId);
            Project project = projectService.getProjectById(projectId);
            model.addAttribute("listSprints", listSprints);
            model.addAttribute("project", project);
            model.addAttribute("roles",
                    userAccountClientService.getUserRole(principal));
            model.addAttribute("user", userAccountClientService.getUser(principal));
            return "project";
        } catch (Exception e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/dashboard";
        }
    }

    /**
     * Displays page for adding a new sprint
     * @param model
     * @return
     */
    @GetMapping("/project/{projectId}/newSprint")
    public String newSprint(
            Model model,
            @PathVariable ("projectId") int projectId,
            @AuthenticationPrincipal AuthState principal,
            RedirectAttributes ra){
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        try {
            Project currentProject = projectService.getProjectById(projectId);
            Sprint newSprint = sprintService.getNewSprint(currentProject);
            List<String> sprintRange = sprintService.getSprintDateRange(currentProject, newSprint);
            model.addAttribute("pageTitle", "Add New Sprint");
            model.addAttribute("sprint", newSprint);
            model.addAttribute("project", currentProject);
            model.addAttribute("user", userAccountClientService.getUser(principal));
            model.addAttribute("sprintStartDateMin", sprintRange.get(0));
            model.addAttribute("sprintStartDateMax", newSprint.getEndDate());
            model.addAttribute("sprintEndDateMin", newSprint.getStartDate());
            model.addAttribute("sprintEndDateMax", sprintRange.get(1));
            return "sprintForm";

        } catch (Exception e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/project/{projectId}";
        }
    }

    /**
     * Saves a sprint and redirects to project page
     * @param sprint
     * @return
     */
    @PostMapping("/project/{projectId}/saveSprint")
    public String saveSprint(
        @PathVariable int projectId,
        Sprint sprint,
        @AuthenticationPrincipal AuthState principal,
        RedirectAttributes ra) {
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        try {
            sprint.setProject(projectService.getProjectById(projectId));
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
         * @param model
         * @return
         */
    /*make sure to update project.html for path*/
    @GetMapping("/project/{projectId}/editSprint/{sprintId}")
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
            List<String> sprintRange = sprintService.getSprintDateRange(currentProject, sprint);
            model.addAttribute("sprint", sprint);
            model.addAttribute("project", currentProject);
            model.addAttribute("pageTitle", "Edit Sprint: " + sprint.getSprintName());
            model.addAttribute("user", userAccountClientService.getUser(principal));
            model.addAttribute("sprintStartDateMin", sprintRange.get(0));
            model.addAttribute("sprintStartDateMax", sprint.getEndDate());
            model.addAttribute("sprintEndDateMin", sprint.getStartDate());
            model.addAttribute("sprintEndDateMax", sprintRange.get(1));
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
     * @return
     */
    @PostMapping("/project/{projectId}/deleteSprint/{sprintId}")
    public String deleteSprint(
        @PathVariable("sprintId") int sprintId,
        Model model,
        @PathVariable int projectId,
        @AuthenticationPrincipal AuthState principal,
        RedirectAttributes ra){
        if (userAccountClientService.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        try {
            Project currentProject = projectService.getProjectById(projectId);
            String message = sprintService.deleteSprint(sprintId);
            ra.addFlashAttribute("messageSuccess", message);
        } catch (Exception e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
        }
        sprintService.updateSprintNames(sprintService.getSprintByProject(projectId));
        List<Sprint> listSprints = sprintService.getSprintByProject(projectId);
        model.addAttribute("listSprints", listSprints);
        return "redirect:/project/{projectId}";
    }

}
