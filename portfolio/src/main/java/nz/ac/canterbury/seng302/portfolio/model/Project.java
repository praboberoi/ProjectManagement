package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;

import nz.ac.canterbury.seng302.portfolio.model.dto.ProjectDTO;
import nz.ac.canterbury.seng302.portfolio.utils.IncorrectDetailsException;

import java.sql.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Creates a Project class required for creating a table in the database
 */
@Entity
public class Project {

    /**
     * Id of the project
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int projectId;

    /**
     * Name of the project
     */
    @Column(nullable = false, unique = true)
    private String projectName;

    /**
     * Description of the project.
     */
    @Column
    private String description;

    /**
     * Start Date of the project.
     */
    @Column(nullable = false)
    private Date startDate;
    /**
     * End Date of the project.
     */
    @Column(nullable = false)
    private Date endDate;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Sprint> sprints;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private Set<Event> events;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private Set<Deadline> deadlines;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private Set<Evidence> evidences;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private Set<Milestone> milestones;

    /**
     * No args Constructor of the Project.
     */
    public Project() {}

    /**
     * Constructor of the object
     * @param projectId id of the project.
     * @param projectName name of the project.
     * @param description information related to the project.
     * @param startDate start date of the project.
     * @param endDate end date of the project.
     */
    public Project(int projectId, String projectName, String description, Date startDate, Date endDate) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Project(ProjectDTO projectDTO) throws IncorrectDetailsException{
        setProjectId(projectDTO.getProjectId());
        setProjectName(projectDTO.getProjectName());
        setDescription(projectDTO.getDescription());
        setStartDate(projectDTO.getStartDate());
        setEndDate(projectDTO.getEndDate());
    }

    /**
     * Obtains the id of the project
     * @return project Id of type int
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * Obtains the name of the project
     * @return project name of type String
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Obtains the description of the project
     * @return description of type String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Obtains the start date of the project
     * @return startDate of type Date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Obtains the end date of the project
     * @return endDate of type Date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the project ID of the project
     * @param projectId of type int
     */
    public void setProjectId(int projectId){
        this.projectId = projectId;
    }

    /**
     * Sets the name of the Project
     * @param projectId of type String
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * Sets the name of the Project
     * @param projectName of type String
     */
    public void setProjectName(String projectName) throws IncorrectDetailsException {
        if (projectName.length() < 1 || projectName.length() > 50) {
            throw new IncorrectDetailsException("Project Name must not be empty or greater than 50 characters.");
        }
        this.projectName = projectName;
    }

    /**
     * Sets the description of the Project
     * @param description of type String
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the start date of the project
     * @param startDate of type String
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Sets the end date of the project
     * @param endDate of type String
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return projectName.equals(project.projectName) && Objects.equals(description, project.description) && startDate.equals(project.startDate) && endDate.equals(project.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectName, description, startDate, endDate);
    }

    /**
     * Builder Class to build the Project
     */
    public static class Builder{
        private int projectId;
        private String projectName;
        private String description = "";
        private Date startDate;
        private Date endDate;

        /**
         * Updaters the project Id with the given project id
         * @param projectId Id of the project of type int
         * @return Builder
         */
        public Builder projectId(int projectId) {
            this.projectId = projectId;
            return this;
        }
        /**
         * Updates the project with the given project name
         * @param projectName name of the project of type String
         * @return Builder
         */
        public Builder projectName(String projectName) {
            this.projectName = projectName;
            return this;
        }

        /**
         * Updates the description with the given project description
         * @param description
         * @return Builder object
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Update the start date with the given start date
         * @param startDate of type
         * @return Builder object
         */
        public Builder startDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        /**
         * Update the end date with the given end date
         * @param endDate of type Sting
         * @return Builder object
         */
        public Builder endDate(Date endDate) {
            this.endDate = endDate;
            return this;
        }

        /**
         * builds the new Project.
         * @return an object of type Project
         */
        public Project build() {
            return new Project(projectId, projectName, description, startDate, endDate);
        }

    }
}
