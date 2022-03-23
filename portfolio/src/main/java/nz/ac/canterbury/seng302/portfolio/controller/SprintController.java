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

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Controller
public class SprintController {
    @Autowired private SprintService sprintService;
    @Autowired private ProjectService projectService;

    @GetMapping("/project/{projectId}")
    public String showSprintList(@PathVariable("projectId") int projectId, Model model) {
        List<Sprint> listSprints = sprintService.getSprintByProject(projectId);
        model.addAttribute("listSprints", listSprints);
        return "project";
    }

    /**
     * Displays page for adding a new sprint
     * @param model
     * @return
     */
    @GetMapping("/project/{projectId}/newSprint")
    public String newSprint(Model model, @PathVariable ("projectId") int projectId) throws Exception {
        int sprintNo = sprintService.countByProjectId(projectId) + 1;
        Sprint newSprint = new Sprint();
        List<Sprint> listSprints = sprintService.getSprintByProject(projectId);
        if(listSprints.size() == 0) {

            Project currentProject = projectService.getProjectById(projectId);
            LocalDate startDate = currentProject.getStartDate().toLocalDate();
            newSprint.setStartDate(Date.valueOf(startDate));
            newSprint.setEndDate(Date.valueOf(startDate.plusWeeks(3)));

        } else {
            Sprint last_sprint = listSprints.get(listSprints.size() - 1);
            LocalDate startDate = last_sprint.getEndDate().toLocalDate();
            newSprint.setStartDate(Date.valueOf(startDate));
            newSprint.setEndDate(Date.valueOf(startDate.plusWeeks(3)));
        }
        newSprint.setSprintName("Sprint " + sprintNo);
        model.addAttribute("sprint", newSprint);
        return "sprint_form";
    }

    /**
     * Saves a sprint and redirects to project page
     * @param sprint
     * @return
     */
    @PostMapping("/project/{projectId}/saveSprint")
    public String saveSprint(@PathVariable int projectId, Sprint sprint) throws Exception {
        sprint.setProject(projectService.getProjectById(projectId));
        sprintService.saveSprint(sprint);
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
    public String sprintEditForm(@PathVariable("sprintId") int sprintId, @PathVariable("projectId") int projectId, Model model) throws Exception {
        Sprint sprint = sprintService.getSprint(sprintId);
        model.addAttribute("sprint", sprint);
        model.addAttribute("pageTitle", "Edit Sprint (Name: " + sprintId + ")");
        return "sprint_form";


    }

    /**
     * Deletes a sprint and redirects back to project page
     * @param sprintId ID of sprint being deleted
     * @param model
     * @return
     */
    /* Deleting sprints */
    @GetMapping("/project/{projectId}/deleteSprint/{sprintId}")
    public String deleteSprint(@PathVariable("sprintId") int sprintId, Model model){
        sprintService.deleteSprint(sprintId);
        return "redirect:/project/{projectId}";
    }

}
