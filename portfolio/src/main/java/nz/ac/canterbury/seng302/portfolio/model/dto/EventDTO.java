package nz.ac.canterbury.seng302.portfolio.model.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import nz.ac.canterbury.seng302.portfolio.model.Project;

/**
 * The repoDTO entity stored in the database for the portfolio application
 */

public class EventDTO {

    private int eventId;

    private Project project;

    private String eventName;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date endDate;

    public int getEventId() {return eventId;}

    public Project getProject() {return project;}

    public String getEventName() {return eventName;}

    public Date getStartDate() {return startDate;}

    public Date getEndDate() {return endDate;}

    /**
     * Creates a new eventDTO object with the provided details
     * @param eventId The ID of the of the event object
     * @param project Project of the event object
     * @param eventName Name of the of the event object
     * @param startDate Start date of the event object
     * @param endDate End date of the event object
     */
    public EventDTO(int eventId, Project project, String eventName, Date startDate, Date endDate) {
        this.eventId = eventId;
        this.project = project;
        this.eventName = eventName;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
