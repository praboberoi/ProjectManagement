package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.utils.SprintColor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.*;
import java.text.SimpleDateFormat;

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
     * Start date and time for the event
     */
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date startDate;

    @Column
    @ElementCollection
    private final List<SprintColor> colors = new ArrayList<>();


    /**
     * End date and time for the event
     */
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date endDate;


    /**
     * No args Constructor of the Event.
     */
    public Event() {}


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

    public void setEventId(int eventId) { this.eventId = eventId; }

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

    public String getStartDateOnly() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(startDate);
    }

    public String getEndDateOnly() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(endDate);
    }

    public String getStartTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        return formatter.format(startDate);
    }

    public List<SprintColor> getColors() {
        return this.colors;
    }

    public void addColor(SprintColor color, int index) {
        this.colors.add(index, color);
    }

    public void clearColourList() {
        colors.clear();
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
        return eventId == event.eventId && Objects.equals(project, event.project) && Objects.equals(eventName, event.eventName) && Objects.equals(startDate, event.startDate) && Objects.equals(endDate, event.endDate);
    }


    @Override
    public int hashCode() {
        return Objects.hash(eventId, project, eventName, startDate, endDate);
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", project=" + project +
                ", eventName='" + eventName + '\'' +
                ", startDate=" + startDate +
                ", colors=" + colors +
                ", endDate=" + endDate +
                '}';
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
