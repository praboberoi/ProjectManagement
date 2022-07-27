package nz.ac.canterbury.seng302.portfolio.model;

import java.sql.Date;

import javax.persistence.*;

/**
 * The data class for Events. Contains the id, name, and dates of the event for storage in the db. 
 */
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int eventId;

    @Column(nullable = false)
    private int projectId;

    @Column(nullable = false)
    private String eventName;

    @Column(nullable = false)
    private Date startDate;

    @Column(nullable = false)
    private Date endDate;

    @Column(nullable = false)
    private String startTime;

    @Column(nullable = false)
    private String endTime;

    /**
     * No args Constructor of the Event.
     */
    public Event() {}

    /**
     * Creates an Event with an auto-generated ID
     * @param eventName The name of the event
     * @param startDate When the event starts
     * @param endDate When the event ends
     */
    public Event(String eventName, Date startDate, Date endDate, String startTime, String endTime) {
        this.eventName = eventName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Creates an Event with a manually specified ID
     * @param eventId The event's ID
     * @param eventName The name of the event
     * @param startDate When the event starts
     * @param endDate When the event ends
     */
    public Event(int eventId, String eventName, Date startDate, Date endDate, String startTime, String endTime) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getProjectId() {
        return this.projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
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

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return eventId == event.eventId 
        && eventName.equals(event.eventName) 
        && startDate.equals(event.startDate) 
        && endDate.equals(event.endDate)
        && startTime.equals(event.startTime)
                && endTime.equals(event.endTime);
    }

    /**
     * Builder Class to build the Event
     */
    public static class Builder{
        private int eventId;
        private String eventName;
        private Date startDate;
        private Date endDate;
        private String startTime;
        private String endTime;

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

        public Builder startTime(String startTime){
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(String endTime){
            this.endTime = endTime;
            return this;
        }

        /**
         * Builds the Event.
         * @return The built event
         */
        public Event build() {
            return new Event(eventId, eventName, startDate, endDate, startTime, endTime);
        }
    }
}
