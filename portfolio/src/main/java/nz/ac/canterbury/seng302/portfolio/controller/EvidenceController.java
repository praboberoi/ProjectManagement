package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for the evidence page
 */
@Controller
public class EvidenceController {

    @Autowired
    private EvidenceService evidenceService;
    @Value("${apiPrefix}") private String apiPrefix;
    private Logger logger = LoggerFactory.getLogger(EventController.class);
    @Autowired
    private ProjectService projectService;
    /**
     * Adds common model elements used by all controller methods.
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("apiPrefix", apiPrefix);
    }


    /** Checks if evidence variables are valid and if it is then saves the evidence
     * @param evidence Evidence object
     * @param ra Redirect Attribute frontend message object
     * @return link of html page to display
     */
    @PostMapping(path="/evidence/saveEvidence")
    public String saveEvidence(
            @ModelAttribute Evidence evidence,
            RedirectAttributes ra) {
        try {
//            evidence.setProject(projectService.getProjectById(projectId));
            evidenceService.verifyEvidence(evidence);
            evidenceService.saveEvidence(evidence);
            ra.addFlashAttribute("messageSuccess", "Successfully Created " + evidence.getTitle());
        } catch(IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
        }
        return "redirect:/evidence";
    }

//    NOTE: delete not part of this story
//    /**
//     * Deletes the evidence and redirects back to the evidence page
//     * @param evidenceId
//     * @param model
//     * @return
//     */
//    @PostMapping(path="/evidence/{evidenceId}/deleteEvidence")
//    public String deleteEvidence(
//            @PathVariable("evidenceId") int evidenceId,
//            RedirectAttributes ra,
//            Model model
//    ){
//        try {
//            String message = evidenceService.deleteEvidence(evidenceId);
//            ra.addFlashAttribute("messageSuccess", message);
////            List<Evidence> listEvidence = evidenceService
//            model.addAttribute("listEvidence", listEvidence);
//        } catch (IncorrectDetailsException e) {
//            ra.addFlashAttribute("messageDanger", e.getMessage());
//        }
//        return "redirect:/evidence";
//    }



}
