package nz.ac.canterbury.seng302.portfolio.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.model.dto.EvidenceDTO;
import nz.ac.canterbury.seng302.portfolio.model.notifications.EvidenceNotification;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.PrincipalUtils;
import nz.ac.canterbury.seng302.portfolio.utils.WebSocketPrincipal;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.*;

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

    @Autowired 
    private UserAccountClientService userAccountClientService;

    @Autowired
    private SimpMessagingTemplate template;

    private final Logger logger = LoggerFactory.getLogger(EvidenceController.class);

    private final HashMap<Integer, EvidenceNotification> editing = new HashMap<>();

    private static final String EVIDENCE = "evidence";

    private static final String LIST_PROJECTS = "listProjects";

    private static final String LIST_EVIDENCE = "listEvidence";

    private static final String NOTIFICATIONS = "notifications";

    private static final String MESSAGE_DANGER = "messageDanger";

    private static final String MESSAGE_SUCCESS = "messageSuccess";

    private static final String IS_CURRENT_USER = "isCurrentUserEvidence";

    public EvidenceController(EvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    /**
     * Updates the model with the correct list of evidence
     * @param userId UserId containing the desired evidence
     * @return Page fragment containing events
     */
    @GetMapping(path="/evidence/{userId}")
    public String getEvidencePage(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("userId") int userId,
            Model model) {
        User user = new User(userAccountClientService.getUser(userId));
        List<Project> listProjects = projectService.getAllProjects();
        List<Evidence> listEvidence = evidenceService.getEvidenceByUserId(userId);
        Evidence newEvidence = evidenceService.getNewEvidence(userId);
        model.addAttribute(EVIDENCE, newEvidence);
        model.addAttribute(LIST_EVIDENCE, listEvidence);
        model.addAttribute(LIST_PROJECTS, listProjects);
        model.addAttribute(EVIDENCE, newEvidence);
        model.addAttribute(LIST_EVIDENCE, listEvidence);
        model.addAttribute(LIST_PROJECTS, listProjects);
        model.addAttribute(IS_CURRENT_USER, PrincipalUtils.getUserId(principal)==userId);
        model.addAttribute(NOTIFICATIONS, editing);
        model.addAttribute("userFirstName", user.getFirstName());
        if (!listEvidence.isEmpty()) {
            model.addAttribute("selectedEvidence", listEvidence.get(0));
        }
        return EVIDENCE;
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
            @PathVariable int evidenceId,
            @AuthenticationPrincipal AuthState principal) {
        ModelAndView mv = new ModelAndView("evidenceFragments::selectedEvidence");
        List<Evidence> listEvidence = evidenceService.getEvidenceByUserId(userId);
        mv.addObject(LIST_EVIDENCE, listEvidence);
        try {
            Evidence selectedEvidence = evidenceService.getEvidence(evidenceId);
            mv.addObject("selectedEvidence", selectedEvidence);
            mv.addObject(IS_CURRENT_USER, PrincipalUtils.getUserId(principal)==userId);
            mv.addObject(NOTIFICATIONS, editing);
        } catch (IncorrectDetailsException e) {
            mv = new ModelAndView("evidence::serverMessages", NOT_FOUND);
            mv.addObject(MESSAGE_DANGER, e.getMessage());
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
            @AuthenticationPrincipal AuthState principal,
            @Header("simpSessionId") String sessionId) {
        Evidence evidence = new Evidence(evidenceDTO);
        try {
            if (PrincipalUtils.getUserId(principal) != userId) {
                throw new IncorrectDetailsException("You may only create evidence on your own evidence page");
            }
            evidence.setOwnerId(userId);
            evidenceService.verifyEvidence(evidence);
            String message = evidenceService.saveEvidence(evidence);
            EvidenceNotification notification = new EvidenceNotification(evidence.getEvidenceId(), "saved",
                    0, PrincipalUtils.getUserName(principal), userId, sessionId);
            notifyEvidence(notification);
            ra.addFlashAttribute(MESSAGE_SUCCESS, message);
        } catch(IncorrectDetailsException e) {
            ra.addFlashAttribute(MESSAGE_DANGER, e.getMessage());
        }
        return "redirect:/evidence/{userId}";
    }

    /**
     * Updates the model with the details of evidence with given evidenceId
     * @param evidenceId ID of the selected evidence
     * @return evidence form fragment
     */
    @GetMapping(path="/evidence/{userId}/{evidenceId}/editEvidence")
    public ModelAndView editEvidence(
            @PathVariable int evidenceId,
            @PathVariable int userId) {
        ModelAndView mv = new ModelAndView("evidence::evidenceForm");
        try {
            Evidence evidence = evidenceService.getEvidence(evidenceId);
            List<Project> listProjects = projectService.getAllProjects();
            mv.addObject(EVIDENCE, evidence);
            mv.addObject(LIST_PROJECTS, listProjects);
            mv.addObject("submissionImg", apiPrefix+"/icons/save-icon.svg");
            mv.addObject("submissionName", "Save");

        } catch (IncorrectDetailsException e) {
            mv = new ModelAndView("evidence::serverMessages", NOT_FOUND);
            mv.addObject(MESSAGE_DANGER, e.getMessage());
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
        mv.addObject(EVIDENCE, evidence);
        mv.addObject(LIST_PROJECTS, listProjects);
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
            @AuthenticationPrincipal AuthState principal,
            @Header("simpSessionId") String sessionId,
            RedirectAttributes ra) {
        try {
            String message = evidenceService.deleteEvidence(evidenceId);
            ra.addFlashAttribute(MESSAGE_SUCCESS, message);
            EvidenceNotification notification = new EvidenceNotification(evidenceId, "deleted",
                    0, PrincipalUtils.getUserName(principal), userId, sessionId);

            notifyEvidence(notification);
        } catch (IncorrectDetailsException e) {
            ra.addFlashAttribute(MESSAGE_DANGER, e.getMessage());
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
            @PathVariable int userId,
            @AuthenticationPrincipal AuthState principal) {
        ModelAndView mv = new ModelAndView("evidence::evidenceList");
        List<Evidence> listEvidence = evidenceService.getEvidenceByUserId(userId);
        mv.addObject(LIST_EVIDENCE, listEvidence);
        mv.addObject(IS_CURRENT_USER, PrincipalUtils.getUserId(principal)==userId);
        mv.addObject(NOTIFICATIONS, editing);
        return mv;
    }

    /**
     * Receives update messages from the client where the changes to the deadlines are made and notifies all the other
     * clients subscribed for updates
     * @param notification Notification containing the deadline ID and project ID that is being edited
     * @param principal Authentication information containing user info
     * @param sessionId Session ID of the websocket communication
     */
    @MessageMapping("/evidence/edit")
    public void editing(EvidenceNotification notification, @AuthenticationPrincipal WebSocketPrincipal principal, @Header("simpSessionId") String sessionId) {
        notification.setUserUpdating(principal.getName());
        notification.setSessionId(sessionId);
        notifyEvidence(notification);
        if (notification.getAction().equals("editing"))
            editing.put(notification.getEvidenceId(), notification);
        else
            editing.remove(notification.getEvidenceId());

    }

    /**
     * Sends an update message to all clients connected to the websocket
     * @param notification encapsulated data to be exchanged between front and back end
     */
    private void notifyEvidence(EvidenceNotification notification) {
        List<Evidence> listEvidence = evidenceService.getEvidenceByUserId(notification.getUserId());
        int firstEvidenceId = listEvidence.isEmpty() ? 0 : listEvidence.get(0).getEvidenceId();
        notification.setFirstEvidenceId(firstEvidenceId);
        try {
            template.convertAndSend("/element/evidence/" + notification.getUserId(), new ObjectMapper().writeValueAsString(notification));
        } catch (JsonProcessingException e) {
            logger.info("Unable to convert and send notification {}", notification);
        }
    }

    /**
     * Detects when a websocket disconnects to remove them from the list of editors
     * @param event Websocket disconnect event
     */
    @EventListener
    public void onApplicationEvent(SessionDisconnectEvent event) {
        for (Map.Entry<Integer, EvidenceNotification> current : editing.entrySet()) {
            EvidenceNotification currentNotification = current.getValue();
            if (currentNotification.getSessionId().equals(event.getSessionId())) {
                currentNotification.setAction("finished");
                notifyEvidence(currentNotification);
                editing.remove(current.getKey());
            }
        }

    }


}
