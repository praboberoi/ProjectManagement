package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.notifications.MilestoneNotification;
import nz.ac.canterbury.seng302.portfolio.service.MilestoneService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.Set;

/**
 * Controller for the milestones
 */
@Controller
public class MilestoneController {
    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SimpMessagingTemplate template;

    private Logger logger = LoggerFactory.getLogger(MilestoneController.class);
    private static Set<MilestoneNotification> editing = new HashSet<>();
    private static final String NOTIFICATION_DESTINATION = "/element/project/%d/milestones";
    private static final String NOTIFICATION_WITHOUT_USERNAME = "milestone%d %s";


    public MilestoneController(MilestoneService milestoneService, ProjectService projectService) {
        this.milestoneService = milestoneService;
        this.projectService = projectService;
    }

    /**
     * Sends an update message to all clients connected to the websocket
     * @param projectId Id of the event's project updated
     * @param milestoneId Id of the event edited
     * @param action The action taken (delete, created, edited)
     */
    private void notifyMilestone(int projectId, int milestoneId, String action) {
        template.convertAndSend(String.format(NOTIFICATION_DESTINATION, projectId), String.format(NOTIFICATION_WITHOUT_USERNAME, milestoneId, action));
    }
}