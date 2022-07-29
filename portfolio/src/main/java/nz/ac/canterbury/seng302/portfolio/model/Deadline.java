package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.sql.Date;

/**
 * The data class for Deadlines. Contains the id, name, and date of the deadline for storage in the db.
 */
@Entity
public class Deadline {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int deadlineID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "projectId", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private String time;

    /**
     * No args Constructor for the Deadline.
     */
    public Deadline() {}

    /**
     * Constructor for deadline with arguments
     * @param name The name of the deadline
     * @param project Project associated with the deadline
     * @param date Date of the deadline
     * @param time Time of the deadline
     */
    public Deadline(int deadlineId, String name, Project project, Date date, String time) {
        this.deadlineID = deadlineId;
        this.name = name;
        this.project = project;
        this.date = date;
        this.time = time;
    }

    public int getDeadlineID() {
        return deadlineID;
    }

    public void setDeadlineID(int deadlineID) {
        this.deadlineID = deadlineID;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Deadline deadline)) return false;

        return deadlineID == deadline.deadlineID
                && name.equals(deadline.name)
                && date.equals(deadline.date)
                && time.equals(deadline.time);
    }

    /**
     * Builder class to build a deadline
     */
    public static class Builder {
        private int deadlineId;
        private String name;
        private Project project;
        private Date date;
        private String time;

        /**
         * Builds the builder with the current deadlineId
         * @param deadlineId ID for the deadline
         * @return The current builder
         */
        public Builder deadlineId(int deadlineId) {
            this.deadlineId = deadlineId;
            return this;
        }

        /**
         * Builds the builder with the current deadline's name
         * @param name Name for the deadline
         * @return The current builder
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Builds the builder with the current project
         * @param project Project for the deadline
         * @return The current builder
         */
        public Builder project(Project project) {
            this.project = project;
            return this;
        }

        /**
         * Builds the builder with the current date
         * @param date Date of the deadline
         * @return The current builder
         */
        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        /**
         * Builds the builder with the current time
         * @param time Time of the deadline
         * @return The current builder
         */
        public Builder time(String time) {
            this.time = time;
            return this;
        }

        /**
         * Returns a new Deadline object with the all the parameters of the current builder
         * @return Object of type Deadline
         */
        public Deadline build() {
            return new Deadline(deadlineId, name, project, date, time);
        }
    }
}
