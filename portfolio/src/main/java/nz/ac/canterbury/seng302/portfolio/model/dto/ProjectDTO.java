package nz.ac.canterbury.seng302.portfolio.model.dto;

import java.sql.Date;

/**
 * Creates a Project class required for creating a table in the database
 */
public class ProjectDTO {

    /**
     * Id of the project
     */
    private int projectId;

    /**
     * Name of the project
     */
    private String projectName;

    /**
     * Description of the project.
     */
    private String description;

    /**
     * Start Date of the project.
     */
    private Date startDate;

    /**
     * End Date of the project.
     */
    private Date endDate;


    public ProjectDTO(int projectId, String projectName, String description, Date startDate, Date endDate) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
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
}