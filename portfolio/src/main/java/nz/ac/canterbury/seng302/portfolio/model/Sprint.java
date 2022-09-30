package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.model.dto.SprintDTO;
import nz.ac.canterbury.seng302.portfolio.utils.SprintColor;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

/**
 * Creates a Sprint class required for creating a table in the database
 */
@Entity
public class Sprint {

    /**
     * ID for the sprint
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sprintId;

    /**
     * Project ID that sprint is contained in
     * Foreign key
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "projectId", nullable = false)
    private Project project;

    /**
     * Label of Sprint
     */
    @Column(nullable = false)
    private String sprintLabel;


    /**
     * Name of Sprint
     */
    @Column(nullable = false)
    private String sprintName;

    /**
     * Description of sprint
     */
    @Lob
    @Column
    private String description;

    /**
     * Start date of sprint
     */
    @Column(nullable = false)
    private Date startDate;
    /**
     * End date of sprint
     */
    @Column(nullable = false)
    private Date endDate;

    /**
     * Colour of the sprint
     */
    @Column()
    @Enumerated(value = EnumType.STRING)
    private SprintColor color = SprintColor.WHITE;

    /**
     * Constructor for the Sprint class without arguments
     */
    public Sprint() {
    }

    /**
     * Constructor for the sprint class with arguments
     * @param sprintId id of the sprint.
     * @param project Project associated with the sprint.
     * @param sprintLabel Label of the sprint.
     * @param sprintName Name of the sprint.
     * @param description Description of the sprint.
     * @param startDate Start date of the sprint.
     * @param endDate End date of the sprint.
     */
    public Sprint(int sprintId, Project project, String sprintLabel, String sprintName, String description, Date startDate, Date endDate, SprintColor color) {
        this.sprintId = sprintId;
        this.project = project;
        this.sprintLabel= sprintLabel;
        this.sprintName = sprintName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.color = color;
    }

    /**
     * Constructor for sprint with argument
     * @param sprintDTO of type SprintDTO
     */
    public Sprint(SprintDTO sprintDTO) {
        this.setSprintId(sprintDTO.getSprintId());
        this.setProject(sprintDTO.getProject());
        this.setSprintName(sprintDTO.getSprintName());
        this.setDescription(sprintDTO.getDescription());
        this.setSprintLabel(sprintDTO.getSprintLabel());
        this.setStartDate(sprintDTO.getStartDate());
        this.setEndDate(sprintDTO.getEndDate());
        this.setColor(sprintDTO.getColor());

    }

    /**
     * To obtain the id of the sprint
     * @return Id of the sprint
     */
    public int getSprintId() {
        return sprintId;
    }

    /**
     * To update the id of the current sprint with the given id
     * @param id of type int
     */
    public void setSprintId(int id) {
        this.sprintId = id;
    }

    /**
     * To obtain the project connected with the sprint
     * @return Project connected with the sprint.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Sets the project to the given project
     * @param project of type Project
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * To obtain the label of the Sprint
     * @return The name of the sprint.
     */
    public String getSprintLabel() {
        return sprintLabel;
    }

    /**
     * Sets the sprint label to the given label
     * @param sprintLabel of type String
     */
    public void setSprintLabel(String sprintLabel) {
        this.sprintLabel = sprintLabel;
    }

    /**
     * To obtain the name of the Sprint
     * @return The name of the sprint.
     */
    public String getSprintName() {
        return sprintName;
    }

    /**
     * Sets the sprint name to the given name
     * @param sprintName of type String
     */
    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    /**
     * To obtain the sprint description
     * @return The description of the sprint.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the sprint.
     * @param description of type String.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * To obtain the start date of the Sprint
     * @return the Start date of the sprint.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date of the sprint to the given date
     * @param startDate of type Date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Returns the end date of the sprint
     * @return End Date of the sprint.
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date of the sprint to the given date
     * @param endDate of type Date.
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Getter for the color attribute
     * @return color of type String
     */
    public SprintColor getColor() {
        return color;
    }

    /**
     * Setter for the color attribute
     * @param color of type String
     */
    public void setColor(SprintColor color) {
        this.color = color;
    }

    /**
     * To check the given sprint is equal to the current sprint.
     * @param o Of type object
     * @return A boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sprint)) return false;
        Sprint sprint = (Sprint) o;
        return sprintId == sprint.sprintId && Objects.equals(project, sprint.project) && Objects.equals(sprintLabel, sprint.sprintLabel) && Objects.equals(sprintName, sprint.sprintName) && Objects.equals(description, sprint.description) && Objects.equals(startDate, sprint.startDate) && Objects.equals(endDate, sprint.endDate) && Objects.equals(color, sprint.color);
    }

    @Override
    public String toString() {
        return "Sprint{" +
                "sprintId=" + sprintId +
                ", project=" + project +
                ", sprintLabel='" + sprintLabel + '\'' +
                ", sprintName='" + sprintName + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", color=" + color +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(sprintId, project, sprintLabel, sprintName, description, startDate, endDate, color);
    }

    /**
     * Builder class to build the sprint
     */
    public static class Builder {
        private int sprintId;
        private String sprintLabel;
        private String sprintName;
        private String description = "";
        private Date startDate;
        private Date endDate;
        private Project project;
        private SprintColor color;

        /**
         * Builds the current Builder with the given sprint id
         * @param sprintId of type int.
         * @return The current Builder.
         */
        public Builder sprintId(int sprintId) {
            this.sprintId = sprintId;
            return this;
        }

        /**
         * Builds the current Builder with the given sprint label.
         * @param sprintLabel of type String
         * @return the current Builder
         */
        public Builder sprintLabel(String sprintLabel) {
            this.sprintLabel = sprintLabel;
            return this;
        }

        /**
         * Builds the current Builder with the given sprint name.
         * @param sprintName of type String
         * @return the current Builder
         */
        public Builder sprintName(String sprintName) {
            this.sprintName = sprintName;
            return this;
        }

        /**
         * Builds the current Builder with the given description
         * @param description of type String.
         * @return the current Builder.
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Builds the current Builder with the given start date
         * @param startDate of type Date
         * @return the current Builder
         */
        public Builder startDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        /**
         * Builds the current Builder with the given end date
         * @param endDate of type Date
         * @return the current Builder
         */
        public Builder endDate(Date endDate) {
            this.endDate = endDate;
            return this;
        }

        /**
         * Builds the current Builder with the given Project
         * @param project of type Project
         * @return the current Builder
         */
        public Builder project(Project project) {
            this.project = project;
            return this;
        }

        public Builder color(SprintColor color) {
            this.color = color;
            return this;
        }

        /**
         * Returns a new Sprint with all the parameters of the current Builder
         * @return of type Sprint
         */
        public Sprint build() {
            return new Sprint(sprintId, project, sprintLabel, sprintName, description, startDate, endDate, color);
        }
    }
}
