package nz.ac.canterbury.seng302.portfolio.model.dto;

import java.util.Date;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Project;

/**
 * The repoDTO entity stored in the database for the portfolio application
 */

public class EventDTO {

    private int eventId;

    private Project project;

    private String eventName;

    private Date startDate;

    private Date endDate;

    public int getEventId() {return eventId;}

    public Project getProject() {return project;}

    public String getEventName() {return eventName;}

    public Date getStartDate() {return startDate;}

    public Date getEndDate() {return endDate;}

    public EventDTO(){}

    /**
     * Creates a new repoDTO object with the provided details
     * @param repoId The ID of the repo object
     * @param groupId GroupId of the repo object
     * @param repoName Name of the repo object
     * @param gitlabProjectId The ID of the gitlab project
     * @param accessToken The access token
     * @param hostAddress The host address
     */
    public EventDTO(int eventId, Project project, String eventName, Date startDate, Date endDate) {
        this.eventId = eventId;
        this.project = project;
        this.eventName = eventName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public EventDTO(Event event) {
        eventId = event.getEventId();
        eventName = event.getEventName();
        project = event.getProject();
        startDate = event.getStartDate();
        endDate = event.getEndDate();
    }
}
