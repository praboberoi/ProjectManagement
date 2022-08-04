package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
import nz.ac.canterbury.seng302.portfolio.service.IncorrectDetailsException;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.PersistenceException;
import java.util.List;

/**
 * Controller for the deadlines
 */
@Controller
public class DeadlineController {

    @Value("${apiPrefix}")
    private String apiPrefix;
    @Autowired
    private DeadlineService deadlineService;
    @Autowired private ProjectService projectService;

    @Autowired private UserAccountClientService userAccountClientService;


    /**
     * Adds common model elements used by all controller methods.
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("apiPrefix", apiPrefix);
    }


    /**
     * Checks if deadline dates are valid and if it is saves the deadline
     * @param deadline Deadline object
     * @return the project page
     */
    @PostMapping(path = "/project/{projectId}/saveDeadline")
    public String saveDeadline(
            @ModelAttribute Deadline deadline,
            RedirectAttributes ra, @PathVariable String projectId) {
        try {
            deadlineService.verifyDeadline(deadline);
            String message = deadlineService.saveDeadline(deadline);
            ra.addFlashAttribute("messageSuccess", message);
        } catch (IncorrectDetailsException ex) {
            ra.addFlashAttribute("messageDanger", ex.getMessage());
        }
        return "redirect:/project/{projectId}";
    }


    /**
     * Deletes the deadline and redirects back to project page
     * @param model Of type {@link Model}
     * @param projectId Of type int
     * @param deadlineId Of type int
     * @param principal Of type {@link AuthState}
     * @param ra Of type {@link RedirectAttributes}
     * @return project.html or error.html
     */
    @PostMapping(path="/{projectId}/deleteDeadline/{deadlineId}")
    public String deleteDeadline(
            @PathVariable("deadlineId") int deadlineId,
            Model model,
            @PathVariable int projectId,
            @AuthenticationPrincipal AuthState principal,
            RedirectAttributes ra) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return "redirect:/dashboard";
        try {
            String message = deadlineService.deleteDeadline(deadlineId);
            ra.addFlashAttribute("messageSuccess", message);
            List<Deadline> listDeadlines = deadlineService.getDeadlineByProject(projectId);
            model.addAttribute("listDeadlines", listDeadlines);
            return "redirect:/project/{projectId}";
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/project/{projectId}";
        }
    }
}
