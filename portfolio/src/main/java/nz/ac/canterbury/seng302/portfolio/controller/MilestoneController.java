package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.service.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.service.MilestoneService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.PersistenceException;
import java.util.List;

/**
 * Controller for milestones
 */
@Controller
public class MilestoneController {

    @Value("${apiPrefix}")
    private String apiPrefix;
    @Autowired
    private MilestoneService milestoneService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserAccountClientService userAccountClientService;

    /**
     * Adds common model elements used by all controller methods.
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("apiPrefix", apiPrefix);
    }

    /**
     * Checks if milestone values are valid and if it is, then saves the milestone
     * @param milestone Milestone object being saved
     * @param ra Flash attribute for displaying conformation/error messages
     * @return Redirects back to the project page
     */
    @PostMapping(path = "/project/{projectId}/saveMilestone")
    public String saveMilestone(
            @PathVariable int projectId,
            @ModelAttribute Milestone milestone,
            RedirectAttributes ra) {
        try {
            milestone.setProject(projectService.getProjectById(projectId));
            milestoneService.verifyMilestone(milestone);
            String message = milestoneService.saveMilestone(milestone);
            ra.addFlashAttribute("messageSuccess", message);
        } catch (IncorrectDetailsException ex) {
            ra.addFlashAttribute("messageDanger", ex.getMessage());
        }
        return "redirect:/project/{projectId}";
    }

    /**
     * Deletes the milestone and redirects back to the project page
     * @param milestoneId ID of milestone being deleted of type int
     * @param model Of type {@link Model}
     * @param projectId ID of project containing the milestone of type int
     * @param principal Of type {@link AuthState}
     * @param ra Of type {@link RedirectAttributes}
     * @return project.html or error.html
     */
    @RequestMapping(path="/{projectId}/deleteMilestone/{milestoneId}", method = RequestMethod.DELETE)
    public String deleteMilestone(
            @PathVariable("milestoneId") int milestoneId,
            Model model,
            @PathVariable int projectId,
            @AuthenticationPrincipal AuthState principal,
            RedirectAttributes ra) {

        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) {
            ra.addFlashAttribute("messageDanger", "You do not have permissions to perform this action");
            return "redirect:/dashboard";
        }

        try {
            String message = milestoneService.deleteMilestone(milestoneId);
            ra.addFlashAttribute("messageSuccess", message);
            List<Milestone> milestoneList = milestoneService.getMilestoneByProject(projectId);
            model.addAttribute("listMilestones", milestoneList);
            return "redirect:/project/{projectId}";

        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/project/{projectId}";

        } catch (PersistenceException e) {
            model.addAttribute("user", userAccountClientService.getUser(principal));
            return "error";

        } catch (Exception e) {
            ra.addFlashAttribute("messageDanger", "Internal Server Error");
            return "error";
        }
    }

}
