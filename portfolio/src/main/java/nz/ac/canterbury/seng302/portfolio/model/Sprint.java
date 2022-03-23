package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Objects;

@Entity
public class Sprint {

    /**
     * ID for the sprint
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private int sprintId;

    /**
     * Project ID that sprint is contained in
     * Foreign key
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "projectId", nullable = false)
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
    private Date startDate;
    /**
     * End date of sprint
     */
    @Column(nullable = false)
    private Date endDate;

    public Sprint() {
    }

    public Sprint(Project project, String sprintName, String description, Date startDate, Date endDate) {
        this.project = project;
        this.sprintName = sprintName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getSprintId() {
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStartDateString() {
        return new SimpleDateFormat("dd/MMMM/yyyy").format(startDate);
    }

    public String getEndDateString() {
        return new SimpleDateFormat("dd/MMMM/yyyy").format(endDate);
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
        private Date startDate;
        private Date endDate;
        private Project project;


        public Builder sprintName(String sprintName) {
            this.sprintName = sprintName;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder startDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(Date endDate) {
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
