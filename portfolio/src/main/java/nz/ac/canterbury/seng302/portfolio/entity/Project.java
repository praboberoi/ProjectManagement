package nz.ac.canterbury.seng302.portfolio.entity;

import javax.persistence.*;

import java.util.Objects;

/**
 * Creates a Project class required that maps to a table in the database
 */
@Entity
public class Project{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private long projectId;

    /**
     * Name of the project
     */

    @Column(nullable = false)
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
    private String startDate;
    /**
     * End Date of the project.
     */
    @Column(nullable = false)
    private String endDate;

    /**
     * No args Constructor of the Project.
     */
    public Project() {}

    /**
     * Constructor of the object
     * @param projectName name of the project.
     * @param description information related to the project.
     * @param startDate start date of the project.
     * @param endDate end date of the project.
     */
    public Project(String projectName, String description, String startDate, String endDate) {
        this.projectName = projectName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getProjectId() {
        return projectId;
    }
    /**
     * Obtains the name of the project
     * @return projectName
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
     * @return startDate of type String
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Obtains the end date of the project
     * @return endDate of type string
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Sets the name of the Project
     * @param projectName of type String
     */
    public void setProjectName(String projectName) {
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
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Sets the end date of the project
     * @param endDate of type String
     */
    public void setEndDate(String endDate) {
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
        private String projectName;
        private String description = "";
        private String startDate;
        private String endDate;

        /**
         * Updates the project with the given project name
         * @param projectName name of the project of type String
         * @return
         */
        public Builder projectName(String projectName) {
            this.projectName = projectName;
            return this;
        }

        /**
         * Updates the description with the given project description
         * @param description
         * @ Builder object
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
        public Builder startDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        /**
         * Update the end date with the given end date
         * @param endDate of type Sting
         * @return Builder object
         */
        public Builder endDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        /**
         * builds the new Project.
         * @return an object of type Project
         */
        public Project build() {
            return new Project(projectName, description, startDate, endDate);
        }

}
}
