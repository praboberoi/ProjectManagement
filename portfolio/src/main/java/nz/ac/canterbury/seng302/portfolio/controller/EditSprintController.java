package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;



/**
 * Controller for the edit sprint details page
 */
@Controller
public class EditSprintController {
    @Autowired private SprintService sprintService;
    private Sprint sprint = new Sprint();

    @GetMapping("/edit-sprint")
    public String sprintForm(Model model) {
        /* Add sprint details to the model */

        model.addAttribute("sprintName", sprint.getSprintId());
        model.addAttribute("sprintStartDate", sprint.getStartDateString());
        model.addAttribute("sprintEndDate", sprint.getEndDateString());
        model.addAttribute("sprintDescription", sprint.getDescription());
        return "editSprint";
    }

    @PostMapping("/edit-sprint")
    public String sprintSave(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="sprintName") String sprintName,
            @RequestParam(value="sprintDescription") String sprintDescription,
            Model model
    ) {
        sprint.setSprintName(sprintName);
        sprint.setDescription(sprintDescription);
        return "redirect:/edit-project";
    }

}
