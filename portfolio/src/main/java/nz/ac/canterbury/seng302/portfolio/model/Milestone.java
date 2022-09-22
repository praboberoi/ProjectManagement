package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.utils.SprintColor;
import org.springframework.format.annotation.DateTimeFormat;

import nz.ac.canterbury.seng302.portfolio.model.dto.MilestoneDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    @Column
    @ElementCollection
    private final List<SprintColor> colors = new ArrayList<>();

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

    public Milestone(MilestoneDTO milestoneDTO) {
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
    public void setName(String name) {
        this.name = name;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
        return "Milestone{" +
                "milestoneId=" + milestoneId +
                ", projectId=" + project.getProjectId() +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", colors= " + colors +
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
