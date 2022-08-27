package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.dto.EvidenceDTO;
import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for the evidence page
 */
@Controller
public class EvidenceController {

    @Autowired
    private EvidenceService evidenceService;


    /** Checks if evidence variables are valid and if it is then saves the evidence
     * @param evidenceDTO EvidenceDTO object
     * @param ra Redirect Attribute frontend message object
     * @return link of html page to display
     */
    @PostMapping(path="/evidence/save")
    public String saveEvidence(
            EvidenceDTO evidenceDTO,
            RedirectAttributes ra) {
        Evidence evidence = new Evidence(evidenceDTO);
        String message = "";
        try {
            evidenceService.verifyEvidence(evidence);
            message = evidenceService.saveEvidence(evidence);
            ra.addFlashAttribute("messageSuccess", message);

        } catch(IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
        }
        return "redirect:/evidence";
    }


}
