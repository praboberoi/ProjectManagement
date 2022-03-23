package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SprintController {
    @Autowired private SprintService sprintService;

//    @GetMapping("/project/${projectId}")
//    public String showSprintList(@PathVariable("projectId") int projectId, Model model) {
//        List<Sprint> listSprints = sprintService.getAllSprints(); //update list to only show list for project
//        //List<Sprint> listSprints = sprintService.listByProjectId(projectId);
//        model.addAttribute("listSprints", listSprints);
//        return "project";
//    }


    /**
     * Displays page for adding a new sprint
     * @param model
     * @return
     */
    @GetMapping("/project/{projectId}/newSprint")
    public String newSprint(Model model, @PathVariable int projectId) {
        int sprintNo = sprintService.countByProjectId(projectId) + 1;
        Sprint newSprint = new Sprint();
        newSprint.setSprintName("Sprint " + sprintNo);
        model.addAttribute("sprint", newSprint);
        model.addAttribute("pageTitle","Add new sprint");
        return "sprintForm";
    }

    /**
     * Saves a sprint and redirects to project page
     * @param sprint
     * @return
     */
    @PostMapping("/project/{projectId}/saveSprint")
    public String saveSprint(@PathVariable int projectId, Sprint sprint) {
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
    public String sprintEditForm(@PathVariable("sprintId") int sprintId, Model model) throws Exception {
        Sprint sprint = sprintService.getSprintById(sprintId);
        model.addAttribute("sprint", sprint);
        model.addAttribute("pageTitle", "Edit Sprint (Name: " + sprintId + ")");
        return "sprintForm";


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
