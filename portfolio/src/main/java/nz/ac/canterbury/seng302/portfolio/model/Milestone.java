package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;
import nz.ac.canterbury.seng302.portfolio.utils.SprintColor;
import nz.ac.canterbury.seng302.portfolio.utils.ValidationUtilities;

import org.springframework.format.annotation.DateTimeFormat;

import nz.ac.canterbury.seng302.portfolio.model.dto.MilestoneDTO;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * The data class for Milestones. Contains the id, name, and date of the milestone for storage in the db.
 */
@Entity
public class Milestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int milestoneId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "projectId", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private SprintColor color;

    /**
     * No args Constructor for the Milestone.
     */
    public Milestone() {}

    /**
     * Constructor for milestone with arguments
     * @param name The name of the milestone
     * @param project Project associated with the milestone
     * @param date Date of the milestone
     */
    public Milestone(int milestoneId, String name, Project project, Date date) {
        this.milestoneId = milestoneId;
        this.name = name;
        this.project = project;
        this.date = date;
    }

    public Milestone(MilestoneDTO milestoneDTO) throws IncorrectDetailsException {
        setMilestoneId(milestoneDTO.getMilestoneId());
        setName(milestoneDTO.getName());
        setProject(milestoneDTO.getProject());
        setDate(milestoneDTO.getDate());
    }

    public int getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(int milestoneId) {
        this.milestoneId = milestoneId;
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
     * Removes the extra whitespace from the given name and updates then milestone name
     * @param name of type String
     */
    public void setName(String name) throws IncorrectDetailsException {
        if (name == null) {
            throw new IncorrectDetailsException("Milestone values cannot be null");
        } else if (name.length() > 50) {
            throw new IncorrectDetailsException("Milestone name cannot exceed 50 characters");
        } else if (name.length() < 3) {
            throw new IncorrectDetailsException("Milestone name must be at least 3 characters");
        } else if (ValidationUtilities.hasMultipleSpaces(name)) {
            throw new IncorrectDetailsException("Milestone name must not contain multiple consecutive spaces.");
        }

        this.name = name.strip();
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) throws IncorrectDetailsException {
        if (project == null) {
            throw new IncorrectDetailsException("Milestone must be associated with a project");
        } else if (date.after(project.getEndDate())) {
            throw new IncorrectDetailsException("Milestone date cannot be after project end date");
        } else if (date.before(project.getStartDate())) {
            throw new IncorrectDetailsException("Milestone date cannot be before project start date");
        }
        this.date = date;
    }

    public SprintColor getColor() {
        return this.color;
    }

    public void setColor(SprintColor color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Milestone{" +
                "milestoneId=" + milestoneId +
                ", projectId=" + project.getProjectId() +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", color= " + color +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Milestone milestone)) return false;
        return milestoneId == milestone.milestoneId && project.equals(milestone.project) && name.equals(milestone.name) && date.equals(milestone.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(milestoneId, project, name, date);
    }

    /**
     * Builder class to build a milestone
     */
    public static class Builder {
        private int milestoneId;
        private String name;
        private Project project;
        private Date date;

        /**
         * Builds the builder with the current milestoneId
         * @param milestoneId ID for the milestone
         * @return The current builder
         */
        public Builder milestoneId(int milestoneId) {
            this.milestoneId = milestoneId;
            return this;
        }

        /**
         * Builds the builder with the current milestone's name
         * @param name Name for the milestone
         * @return The current builder
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Builds the builder with the current project
         * @param project Project for the milestone
         * @return The current builder
         */
        public Builder project(Project project) {
            this.project = project;
            return this;
        }

        /**
         * Builds the builder with the current date
         * @param date Date of the milestone
         * @return The current builder
         */
        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        /**
         * Returns a new Milestone object with the all the parameters of the current builder
         * @return Object of type Milestone
         */
        public Milestone build() {
            return new Milestone(milestoneId, name, project, date);
        }
    }
}
