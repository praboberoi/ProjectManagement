package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.utils.SprintColor;
import org.springframework.format.annotation.DateTimeFormat;

import nz.ac.canterbury.seng302.portfolio.model.dto.DeadlineDTO;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * The data class for Deadlines. Contains the id, name, and date of the deadline for storage in the db.
 */
@Entity
public class Deadline {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private int deadlineId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "projectId", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date date;

    @Column
    @ElementCollection
    private final List<SprintColor> colors = new ArrayList<>();

    /**
     * No args Constructor for the Deadline.
     */
    public Deadline() {}

    /**
     * Constructor for deadline with arguments
     * @param name The name of the deadline
     * @param project Project associated with the deadline
     * @param date Date of the deadline
     */
    public Deadline(int deadlineId, String name, Project project, Date date) {
        this.deadlineId = deadlineId;
        this.name = name;
        this.project = project;
        this.date = date;
    }

    public Deadline(DeadlineDTO deadlineDTO) {
        setDeadlineId(deadlineDTO.getDeadlineId());
        setName(deadlineDTO.getName());
        setProject(deadlineDTO.getProject());
        setDate(deadlineDTO.getDate());
    }

    public int getDeadlineId() {
        return deadlineId;
    }

    public void setDeadlineId(int deadlineId) {
        this.deadlineId = deadlineId;
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

    /**
     * Removes the extra whitespace from the given name and updates then deadline name
     * @param name of type String
     */
    public void setName(String name) {
        this.name = String.join(" ", List.of(name.strip().split("\s+")));
    }


    public Date getDate() {
        return date;
    }

    public String getDateOnly() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        return formatter.format(date);
    }

    public List<SprintColor> getColors() {
        return this.colors;
    }

    public void addColor(SprintColor color, int index) {
        this.colors.add(index, color);
    }

    public void clearColorList() {
        colors.clear();
    }

    @Override
    public String toString() {
        return "Deadline{" +
                "deadlineId=" + deadlineId +
                ", projectId=" + project.getProjectId() +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", colors= " + colors +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Deadline deadline)) return false;
        return deadlineId == deadline.deadlineId && project.equals(deadline.project) && name.equals(deadline.name) && date.equals(deadline.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deadlineId, project, name, date);
    }

    /**
     * Builder class to build a deadline
     */
    public static class Builder {
        private int deadlineId;
        private String name;
        private Project project;
        private Date date;

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
         * Returns a new Deadline object with the all the parameters of the current builder
         * @return Object of type Deadline
         */
        public Deadline build() {
            return new Deadline(deadlineId, name, project, date);
        }
    }
}
