package nz.ac.canterbury.seng302.portfolio.model;

import com.google.type.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

//import java.sql.Date;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Objects;

import javax.persistence.*;

/**
 * The data class for Events. Contains the id, name, and dates of the event for storage in the db. 
 */
@Entity
public class Event {

    /**
     * ID for the event
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eventId;

    /**
     * Project that the event is contained in
     * Foreign key
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "projectId", nullable = false)
    private Project project;

    /**
     * Name for the event
     */
    @Column(nullable = false)
    private String eventName;

    /**
     * Start date for the event
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm")
    private Date startDate;
    /**
     * End date and time for the event
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm")
    private Date endDate;

    private Date date;

    /**
     * No args Constructor of the Event.
     */
    public Event() {}

    /**
     * Creates an Event with an auto-generated ID
     * @param project The project the event is contained in
     * @param eventName The name of the event
     * @param startDate When the event starts
     * @param endDate When the event ends
     */
    public Event(String eventName, Project project, Date startDate, Date endDate) {
        this.project = project;
        this.eventName = eventName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Creates an Event with a manually specified ID
     * @param project The project the event is contained in
     * @param eventName The name of the event
     * @param startDate When the event starts
     * @param endDate When the event ends
     */
    public Event(int eventId, Project project, String eventName, Date startDate, Date endDate) {
        this.eventId = eventId;
        this.project = project;
        this.eventName = eventName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public int getEventId() {
        return this.eventId;
    }

    public String getEventName() {
        return this.eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDateOnly() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    public String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        return formatter.format(date);
    }


    /**
     * Overrides for comparing event objects
     * @param o Object being compared
     * @return True or False
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return eventId == event.eventId 
        && Objects.equals(eventName, event.eventName)
        && Objects.equals(startDate, event.startDate)
        && Objects.equals(endDate, event.endDate);
    }

    /**
     * Builder Class to build the Event
     */
    public static class Builder{
        private int eventId;
        private Project project;
        private String eventName;
        private Date startDate;
        private Date endDate;

        /**
         * Updates the event ID
         * @param eventId Numerical ID of the project
         * @return Event builder
         */
        public Builder eventId(int eventId) {
            this.eventId = eventId;
            return this;
        }

        /**
         * Builds the builder with the current project
         * @param project Project containing the event
         * @return The current event builder
         */
        public Builder project(Project project) {
            this.project = project;
            return this;
        }
        /**
         * Updates the event's name
         * @param eventName Name of the project
         * @return Event builder
         */
        public Builder eventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        /**
         * Update the event's start date
         * @param startDate When the event starts
         * @return Event builder
         */
        public Builder startDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        /**
         * Update the event's end date
         * @param endDate When the event ends
         * @return Event builder
         */
        public Builder endDate(Date endDate) {
            this.endDate = endDate;
            return this;
        }



        /**
         * Builds the Event.
         * @return The built event
         */
        public Event build() {
            return new Event(eventId, project, eventName, startDate, endDate);
        }
    }
}
