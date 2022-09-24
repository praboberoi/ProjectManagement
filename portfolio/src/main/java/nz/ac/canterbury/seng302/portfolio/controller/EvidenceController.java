package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.model.dto.EvidenceDTO;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.springframework.http.HttpStatus. *;

/**
 * Controller for the evidence page
 */
@Controller
public class EvidenceController {

    @Value("${apiPrefix}")
    private String apiPrefix;

    @Autowired
    private EvidenceService evidenceService;

    @Autowired
    private ProjectService projectService;
    @Autowired private UserAccountClientService userAccountClientService;

    @Autowired
    private SimpMessagingTemplate template;

    public EvidenceController(EvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    /**
     * Adds common model elements used by all controller methods.
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("apiPrefix", apiPrefix);
    }

    /**
     * Updates the model with the correct list of evidence
     * @param userId UserId containing the desired evidence
     * @return Page fragment containing events
     */
    @GetMapping(path="/evidence/{userId}")
    public String evidenceList(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("userId") int userId,
            Model model) {
        User user = new User(userAccountClientService.getUser(principal));
        List<Project> listProjects = projectService.getAllProjects();
        List<Evidence> listEvidence = evidenceService.getEvidenceByUserId(userId);
        Evidence newEvidence = evidenceService.getNewEvidence(userId);
        model.addAttribute("evidence", newEvidence);
        model.addAttribute("listEvidence", listEvidence);
        model.addAttribute("listProjects", listProjects);
        model.addAttribute("isCurrentUserEvidence", user.getUserId()==userId);
        if (!listEvidence.isEmpty()) {
            model.addAttribute("selectedEvidence", listEvidence.get(0));
        }
        return "evidence";
    }


    /**
     * Updates the model with the correct list of evidence and a selected evidence
     * @param userId User ID containing the evidence
     * @param evidenceId ID of the selected evidence object
     * @return The selected evidence fragment
     */
    @GetMapping(path="/evidence/{userId}/{evidenceId}")
    public ModelAndView selectedEvidence(
            @PathVariable int userId,
            @PathVariable int evidenceId) {
        ModelAndView mv = new ModelAndView("evidence::selectedEvidence");
        List<Evidence> listEvidence = evidenceService.getEvidenceByUserId(userId);
        mv.addObject("listEvidence", listEvidence);
        try {
            Evidence selectedEvidence = evidenceService.getEvidence(evidenceId);
            mv.addObject("selectedEvidence", selectedEvidence);
        } catch (IncorrectDetailsException e) {
            mv = new ModelAndView("evidence::serverMessages", NOT_FOUND);
            mv.addObject("messageDanger", e.getMessage());
        }
        return mv;
    }

    /** Checks if evidence variables are valid and if it is then saves the evidence
     * @param evidenceDTO EvidenceDTO object
     * @param ra Redirect Attribute frontend message object
     * @return link of html page to display
     */
    @PostMapping(path="/evidence/{userId}/saveEvidence")
    public String saveEvidence(
            @ModelAttribute EvidenceDTO evidenceDTO,
            @PathVariable("userId") int userId,
            RedirectAttributes ra,
            @AuthenticationPrincipal AuthState principal) {
        Evidence evidence = new Evidence(evidenceDTO);
        try {
            int editingUser = PrincipalUtils.getUserId(principal);
            if (editingUser != userId) {
                throw new IncorrectDetailsException("You may only create evidence on your own evidence page");
            }

            evidence.setOwnerId(userId);
            evidenceService.verifyEvidence(evidence);
            String message = evidenceService.saveEvidence(evidence);
            notifyEvidence(evidence.getEvidenceId(), "saved", userId);
            ra.addFlashAttribute("messageSuccess", message);
        } catch(IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
        }
        return "redirect:/evidence/{userId}";
    }

    /**
     * Updates the model with the details of evidence with given evidenceId
     * @param evidenceId ID of the selected evidence
     * @return evidence form fragment
     */
    @GetMapping(path="/evidence/{userId}/{evidenceId}/editEvidence")
    public ModelAndView getEvidence(
            @PathVariable int evidenceId) {
        ModelAndView mv = new ModelAndView("evidence::evidenceForm");
        try {
            Evidence evidence = evidenceService.getEvidence(evidenceId);
            List<Project> listProjects = projectService.getAllProjects();
            mv.addObject("evidence", evidence);
            mv.addObject("listProjects", listProjects);
            mv.addObject("submissionImg", apiPrefix+"/icons/save-icon.svg");
            mv.addObject("submissionName", "Save");

        } catch (IncorrectDetailsException e) {
            mv = new ModelAndView("evidence::serverMessages", NOT_FOUND);
            mv.addObject("messageDanger", e.getMessage());
        }
        return mv;

    }

    /**
     * Updates the form with the newly created evidence
     * @param userId of user to create a new Evidence
     * @return evidence form fragment
     */
    @GetMapping(path="/evidence/{userId}/getNewEvidence")
    public ModelAndView createEvidence(
            @PathVariable int userId) {
        ModelAndView mv = new ModelAndView("evidence::evidenceForm");
        Evidence evidence = evidenceService.getNewEvidence(userId);
        List<Project> listProjects = projectService.getAllProjects();
        mv.addObject("evidence", evidence);
        mv.addObject("listProjects", listProjects);
        mv.addObject("submissionImg", apiPrefix+"/icons/create-icon.svg");
        mv.addObject("submissionName", "Create");
        return mv;
    }

    /**
     * Deletes the evidence with the given evidence ID
     * @param evidenceId of the evidence to be deleted
     * @param ra redirect attribute of for displaying the message
     * @return the link to the HTML page
     */
    @PostMapping(path="/evidence/{userId}/{evidenceId}/deleteEvidence")
    public String deleteEvidence(
            @PathVariable int evidenceId,
            @PathVariable int userId,
            RedirectAttributes ra) {
        try {
            String message = evidenceService.deleteEvidence(evidenceId);
            ra.addFlashAttribute("messageSuccess", message);
            notifyEvidence(evidenceId, "deleted", userId);
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
        }

        return "redirect:/evidence/{userId}";
    }

    /**
     * Updates the evidence list of the user with the given user ID
     * @param userId the ID of user whose evidence list is requested
     * @return evidenceList fragment
     */
    @GetMapping(path="/evidence/{userId}/getEvidenceList")
    public ModelAndView getEvidenceList(
            @PathVariable int userId) {
        ModelAndView mv = new ModelAndView("evidence::evidenceList");
        List<Evidence> listEvidence = evidenceService.getEvidenceByUserId(userId);
        mv.addObject("listEvidence", listEvidence);
        return mv;
    }

    /**
     * Sends an update message to all clients connected to the websocket
     * @param evidenceId the ID of the evidence to be updated
     * @param action the action performed on the evidence
     * @param userId the ID of the user making the changes to the evidence
     */
    private void notifyEvidence(int evidenceId, String action, int userId) {
        List<Evidence> listEvidence = evidenceService.getEvidenceByUserId(userId);
        int firstEvidenceId = listEvidence.isEmpty() ? 0 : listEvidence.get(0).getEvidenceId();
        template.convertAndSend("/element/evidence/" + userId ,
                ("evidence " + evidenceId + " "  + action + " " + firstEvidenceId));
    }



}
