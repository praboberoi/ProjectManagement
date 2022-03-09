package nz.ac.canterbury.seng302.portfolio.entity;



import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Sprint implements Serializable {
    /**
     * ID for the sprint
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private long sprintId;

    /**
     * Project ID that sprint is contained in
     * Foreign key
     */
    @ManyToOne
    @JoinColumn(
            name = "projectId",
            referencedColumnName = "projectId"
    )
    private Project project;

    /**
     * Name of Sprint
     */
    @Column(nullable = false)
    private String sprintName;

    /**
     * Description of sprint
     */
    @Column
    private String description;

    /**
     * Start date of sprint
     */
    @Column(nullable = false)
    private String startDate;
    /**
     * End date of sprint
     */
    @Column(nullable = false)
    private String endDate;

    public Sprint() {
    }

    public Sprint(Project project, String sprintName, String description, String startDate, String endDate) {
        this.project = project;
        this.sprintName = sprintName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getId() {
        return sprintId;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sprint)) return false;
        Sprint sprint = (Sprint) o;
        return sprintId == sprint.sprintId && Objects.equals(project, sprint.project) && Objects.equals(sprintName, sprint.sprintName) && description.equals(sprint.description) && Objects.equals(startDate, sprint.startDate) && Objects.equals(endDate, sprint.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sprintId, project, sprintName, description, startDate, endDate);
    }

    /**
     * Builder class to build the sprint
     */
    public static class Builder {
        private String sprintName;
        private String description = "";
        private String startDate;
        private String endDate;
        private Project project;


        public Builder sprintName(String sprintName) {
            this.sprintName = sprintName;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder startDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder project(Project project) {
            this.project = project;
            return this;
        }
        public Sprint build() {
            return new Sprint(project, sprintName, description, startDate, endDate);
        }
    }
}
