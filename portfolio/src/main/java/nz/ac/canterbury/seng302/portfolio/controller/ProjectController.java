package nz.ac.canterbury.seng302.portfolio.controller;


import nz.ac.canterbury.seng302.portfolio.entity.Project;
import nz.ac.canterbury.seng302.portfolio.entity.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.JsonPath;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ProjectController {
    @Autowired private SprintService sprintService;
/*
    @GetMapping("/project/{projectId}")
    public String showSprintList(@PathVariable("projectId") Long projectId, Model model) {
         List<Sprint> listSprints = sprintService.listAll(); //update list to only show list for project
        //List<Sprint> listSprints = sprintService.listByProjectId(projectId);
        model.addAttribute("listSprints", listSprints);
        return "project";
    }
*/

    /**
     * Displays page for adding a new sprint
     * @param model
     * @return
     */
    @GetMapping("/project/{projectId}/newSprint")
    public String newSprint(Model model, @PathVariable Long projectId) {
        int sprintNo = sprintService.countByProjectId(projectId) + 1;
        Sprint newSprint = new Sprint();
        newSprint.setSprintName("Sprint " + sprintNo);
        model.addAttribute("sprint", newSprint);
        model.addAttribute("pageTitle","Add new sprint");
        return "sprint_form";
    }

    /**
     * Saves a sprint and redirects to project page
     * @param sprint
     * @return
     */
    @PostMapping("/project/saveSprint")
    public String saveSprint(Sprint sprint) {
    @PostMapping("/project/{projectId}/saveSprint")
    public String saveSprint(@PathVariable Long projectId, Sprint sprint) {
        sprintService.saveSprint(sprint);
        /*model.addAttribute("sprint", sprint);*/
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
    public String sprintEditForm(@PathVariable("sprintId") Long sprintId, Model model){
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
    public String deleteSprint(@PathVariable("sprintId") Long sprintId, Model model){
        sprintService.deleteSprint(sprintId);
        return "redirect:/project/{projectId}";
    }




    /*
    @GetMapping
    String getProject(Model model){
        model.addAttribute("newproject", "this is a new project");
            return "project";

    }
    */

}
