package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class SprintController {
    @Autowired private SprintService sprintService;
    @Autowired private ProjectService projectService;
    @Autowired private UserAccountClientService userAccountClientService;
    @Value("${apiPrefix}") private String apiPrefix;

    /**
     * Add project details, sprints, and current user roles (to determine access to add, edit, delete sprints)
     * to the individual project pages.
     * @param projectId - ID of the project selected to view.
     * @param principal - Current User.
     * @param model
     * @return - name of the html page to display
     */
    @RequestMapping(value="${apiPrefix}/project/{projectId}", method = RequestMethod.GET)
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
            model.addAttribute("roles", userAccountClientService.getUserRole(principal));
            model.addAttribute("user", userAccountClientService.getUser(principal));
            model.addAttribute("apiPrefix", apiPrefix);
            return "/project";
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
    @RequestMapping(value="${apiPrefix}/project/{projectId}/newSprint", method = RequestMethod.GET)
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
            model.addAttribute("apiPrefix", apiPrefix);
            model.addAttribute("sprint", newSprint);
            model.addAttribute("project", currentProject);
            model.addAttribute("user", userAccountClientService.getUser(principal));
            model.addAttribute("sprintStartDateMin", sprintRange.get(0));
            model.addAttribute("sprintStartDateMax", newSprint.getEndDate());
            model.addAttribute("sprintEndDateMin", newSprint.getStartDate());
            model.addAttribute("sprintEndDateMax", sprintRange.get(1));
            model.addAttribute("submissionName", "Create");
            model.addAttribute("image", "/icons/create-icon.svg");
            return "/sprintForm";

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
    @RequestMapping(value="${apiPrefix}/project/{projectId}/saveSprint",method = RequestMethod.POST)
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
         * @param model
         * @return
         */
    /*make sure to update project.html for path*/
    @RequestMapping(value="${apiPrefix}/project/{projectId}/editSprint/{sprintId}", method = RequestMethod.GET)
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
            model.addAttribute("apiPrefix", apiPrefix);
            model.addAttribute("sprint", sprint);
            model.addAttribute("project", currentProject);
            model.addAttribute("pageTitle", "Edit Sprint: " + sprint.getSprintName());
            model.addAttribute("user", userAccountClientService.getUser(principal));
            model.addAttribute("sprintStartDateMin", sprintRange.get(0));
            model.addAttribute("sprintStartDateMax", sprint.getEndDate());
            model.addAttribute("sprintEndDateMin", sprint.getStartDate());
            model.addAttribute("sprintEndDateMax", sprintRange.get(1));
            model.addAttribute("submissionName", "Save");
            model.addAttribute("image", "/icons/save-icon.svg");
            return "/sprintForm";
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
    @RequestMapping(value="${apiPrefix}/project/{projectId}/deleteSprint/{sprintId}", method = RequestMethod.POST)
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
        sprintService.updateSprintLabels(sprintService.getSprintByProject(projectId));
        List<Sprint> listSprints = sprintService.getSprintByProject(projectId);
        model.addAttribute("listSprints", listSprints);
        model.addAttribute("apiPrefix", apiPrefix);
        return "redirect:/project/{projectId}";
    }

}
