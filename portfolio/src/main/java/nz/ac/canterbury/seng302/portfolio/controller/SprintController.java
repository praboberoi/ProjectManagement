package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/project/{projectId}")
    public String showSprintList(@PathVariable("projectId") int projectId, Model model, RedirectAttributes ra) {
        try {
            List<Sprint> listSprints = sprintService.getSprintByProject(projectId);
            Project project = projectService.getProjectById(projectId);
            model.addAttribute("listSprints", listSprints);
            model.addAttribute("project", project);
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
    public String newSprint(Model model, @PathVariable ("projectId") int projectId, RedirectAttributes ra) {
        try {
            Project currentProject = projectService.getProjectById(projectId);
            Sprint newSprint = sprintService.getNewSprint(currentProject);
            model.addAttribute("pageTitle", "Add New Sprint");
            model.addAttribute("sprint", newSprint);
            model.addAttribute("project", currentProject);
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
    public String saveSprint(@PathVariable int projectId, Sprint sprint, RedirectAttributes ra) {
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
    public String sprintEditForm(@PathVariable("sprintId") int sprintId, @PathVariable("projectId") int projectId, Model model, RedirectAttributes ra) {
        try {
            Project currentProject = projectService.getProjectById(projectId);
            Sprint sprint = sprintService.getSprint(sprintId);
            model.addAttribute("sprint", sprint);
            model.addAttribute("project", currentProject);
            model.addAttribute("pageTitle", "Edit Sprint: " + sprint.getSprintName());
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
    /* Deleting sprints */
    @GetMapping("/project/{projectId}/deleteSprint/{sprintId}")
    public String deleteSprint(@PathVariable("sprintId") int sprintId, Model model, @PathVariable int projectId, RedirectAttributes ra){
        try {
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
