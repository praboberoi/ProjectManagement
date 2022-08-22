package nz.ac.canterbury.seng302.portfolio.controller;
import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
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
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserAccountClientService userAccountClientService;
    private final static String RedirectToProjectPage = "redirect:/project/{projectId}";
    public DeadlineController(DeadlineService deadlineService, ProjectService projectService) {
        this.deadlineService = deadlineService;
        this.projectService = projectService;
    }
    /**
     * Adds common model elements used by all controller methods.
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("apiPrefix", apiPrefix);
    }
    /**
     * Opens eventForm.html and populates it with a new Event object
     * Checks for teacher or admin privileges
     * @param projectId ID of the project
     * @param principal Current user
     * @param ra Redirect Attribute frontend message object
     * @param model
     * @return link of the html page to display
     */
    @RequestMapping(path = "/project/{projectId}/newDeadline", method = RequestMethod.GET)
    public String newDeadline(
            Model model,
            @AuthenticationPrincipal AuthState principal,
            RedirectAttributes ra,
            @PathVariable ("projectId") int projectId) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return "redirect:/project/" + projectId;
        model.addAttribute("apiPrefix", apiPrefix);
        Deadline newDeadline;
        Project currentProject;
        try {
            currentProject = projectService.getProjectById(projectId);
            newDeadline = deadlineService.getNewDeadline(currentProject);
            model.addAttribute("project", currentProject);
            model.addAttribute("deadline", newDeadline);
            model.addAttribute("pageTitle", "Add New Deadline");
            model.addAttribute("submissionName", "Create");
            model.addAttribute("image", apiPrefix + "/icons/create-icon.svg");
            model.addAttribute("user", userAccountClientService.getUser(principal));
            model.addAttribute("projectDateMin", currentProject.getStartDate().toString() + "T00:00");
            model.addAttribute("projectDateMax", currentProject.getEndDate().toString() + "T00:00");
            return "deadlineForm";
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return "redirect:/project/{projectId}";
        }
    }
    /**
     * Checks if deadline dates are valid and if it is saves the deadline
     * @param deadline Deadline object
     * @param principal Current user
     * @param projectId ID of the project
     * @param ra Redirect Attribute frontend message object
     * @return the project page
     */
    @PostMapping(path = "/project/{projectId}/saveDeadline")
    public String saveDeadline(
            @ModelAttribute Deadline deadline,
            @AuthenticationPrincipal AuthState principal,
            @PathVariable ("projectId") int projectId,
            RedirectAttributes ra) {
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return "redirect:/project/" + projectId;
        String message = "";
        try {
            deadline.setProject(projectService.getProjectById(projectId));
            deadlineService.verifyDeadline(deadline);
            message = deadlineService.saveDeadline(deadline);
            ra.addFlashAttribute("messageSuccess", message);
        } catch (IncorrectDetailsException ex) {
            ra.addFlashAttribute("messageDanger", ex.getMessage());
        }
        return RedirectToProjectPage;
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
            return RedirectToProjectPage;
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return RedirectToProjectPage;
        }
    }
    /**
     * Maps an existing deadline, current user, user's role and button info to the deadline form
     * @param deadlineId Of type int
     * @param projectId Of type int
     * @param model Of type {@link Model}
     * @param principal Of type {@link AuthState}
     * @param ra Of type {@link RedirectAttributes}
     * @return deadlineForm.html or project.html
     */
    @GetMapping(path="/project/{projectId}/editDeadline/{deadlineId}")
    public String deadlineEditForm(
            @PathVariable("deadlineId") int deadlineId,
            @PathVariable("projectId") int projectId,
            Model model,
            @AuthenticationPrincipal AuthState principal,
            RedirectAttributes ra){
        if (!PrincipalUtils.checkUserIsTeacherOrAdmin(principal)) return RedirectToProjectPage;
        try {
            Project currentProject = projectService.getProjectById(projectId);
            Deadline deadline = deadlineService.getDeadline(deadlineId);
            model.addAttribute("deadline", deadline);
            model.addAttribute("project", currentProject);
            model.addAttribute("pageTitle", "Edit Deadline: " + deadline.getName());
            model.addAttribute("deadlineMin", currentProject.getStartDate());
            model.addAttribute("deadlineMax", currentProject.getEndDate());
            model.addAttribute("submissionName", "Save");
            model.addAttribute("image", apiPrefix + "/icons/save-icon.svg");
            return "deadlineForm";
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
            return RedirectToProjectPage;
        }
    }
}