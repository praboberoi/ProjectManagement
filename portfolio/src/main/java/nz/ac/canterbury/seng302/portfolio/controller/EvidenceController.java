package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Groups;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.dto.EvidenceDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * Controller for the evidence page
 */
@Controller
public class EvidenceController {

    @Autowired
    private EvidenceService evidenceService;

    @Value("${apiPrefix}") private String apiPrefix;

    /**
     * Adds common model elements used by all controller methods.
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("apiPrefix", apiPrefix);
    }

    /**
     * Get message for empty registration page
     * @param request HTTP request sent to this endpoint
     * @param response HTTP response that will be returned by this endpoint
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Registration html page
     */
    @GetMapping(path="/evidence/{userId}")
    public String evidence(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @AuthenticationPrincipal AuthState principal

    ) {
        LocalDate now = LocalDate.now();
        Project project = new Project(1, "Test Project", "test", java.sql.Date.valueOf(now),
                java.sql.Date.valueOf(now.plusDays(50)));
        Evidence evidence1 = new Evidence(1, project, new Date(), "Test Evidence 1", "testing", 1);
        Evidence evidence2 = new Evidence(2, project, new Date(), "Test Evidence 2", "testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing testing ", 1);
        Evidence[] evidences = {evidence1, evidence2};
        List<Evidence> listEvidences = Arrays.asList(evidences);
        model.addAttribute("listEvidence", listEvidences);
        model.addAttribute("selectedEvidence", evidence2);
        return "evidence";
    }

//    /**
//     * Get list of groups
//     * @return List of groups
//     */
//    @GetMapping(path="/groups/list")
//    public ModelAndView evidenceList() {
//        List<Groups> groups = Arrays.asList(groupService.getMembersWithoutAGroup(), groupService.getTeachingStaffGroup());
//        groups = Stream.concat(groups.stream(), groupService.getPaginatedGroups().stream()).toList();
//        ModelAndView mv = new ModelAndView("groups::groupList");
//        mv.addObject("listGroups", groups);
//        return mv;
//    }



    /** Checks if evidence variables are valid and if it is then saves the evidence
     * @param evidence Evidence object
     * @param ra Redirect Attribute frontend message object
     * @return link of html page to display
     */
    @PostMapping(path="/evidence/saveEvidence")
    public String saveEvidence(
            @ModelAttribute Evidence evidence,
            RedirectAttributes ra,
            @AuthenticationPrincipal AuthState principal) {
        try {
            evidence.setOwnerId(PrincipalUtils.getUserId(principal));
            String message = "";
            evidenceService.verifyEvidence(evidence);
            message = evidenceService.saveEvidence(evidence);
            ra.addFlashAttribute("messageSuccess", message);
        } catch(IncorrectDetailsException e) {
            ra.addFlashAttribute("messageDanger", e.getMessage());
        }
        return "redirect:/evidence";
    }
}
