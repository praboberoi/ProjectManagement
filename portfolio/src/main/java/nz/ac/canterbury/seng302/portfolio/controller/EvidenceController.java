package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for the evidence page
 */
@Controller
public class EvidenceController {
    @Value("${apiPrefix}")
    private String apiPrefix;
    @Autowired
    private EvidenceService evidenceService;

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
            @PathVariable("userId") int userId,
            Model model) {
        List<Evidence> listEvidence = evidenceService.getEvidenceByUserId(userId);
        model.addAttribute("listEvidence", listEvidence);
        return "evidence";
    }

    /**
     * Updates the model with the correct list of evidence and a selected evidence
     * @param userId User ID containing the evidence
     * @param evidenceId ID of the selected evidence object
     * @param ra Redirect Attribute, a frontend message object
     * @return The selected evidence fragment
     */
    @GetMapping(path="/evidence/{userId}/{evidenceId}")
    public ModelAndView selectedEvidence(
            @PathVariable int userId,
            @PathVariable int evidenceId,
            RedirectAttributes ra) {
        ModelAndView mv = new ModelAndView("evidence::evidenceList");
        List<Evidence> listEvidence = evidenceService.getEvidenceByUserId(userId);
        mv.addObject("listEvidence", listEvidence);
        try {
            Evidence selectedEvidence = evidenceService.getEvidence(evidenceId);
            mv.addObject("selectedEvidence", selectedEvidence);
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
        }
        return mv;
    }


    /** Checks if evidence variables are valid and if it is then saves the evidence
     * @param evidence Evidence object
     * @param ra Redirect Attribute frontend message object
     * @return link of html page to display
     */
    @PostMapping(path="/evidence/{userId}/saveEvidence")
    public String saveEvidence(
            @ModelAttribute Evidence evidence,
            @PathVariable("userId") int userId,
            RedirectAttributes ra,
            @AuthenticationPrincipal AuthState principal) {
        try {
            int editingUser = PrincipalUtils.getUserId(principal);
            if (editingUser != userId) {
                throw new IncorrectDetailsException("You may only create evidence on your own evidence page");
            }

            evidence.setOwnerId(userId);
            evidenceService.verifyEvidence(evidence);
            String message = evidenceService.saveEvidence(evidence);
            ra.addFlashAttribute("messageSuccess", message);
        } catch(IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
        }
        return "redirect:/evidence/{userId}";
    }

}
