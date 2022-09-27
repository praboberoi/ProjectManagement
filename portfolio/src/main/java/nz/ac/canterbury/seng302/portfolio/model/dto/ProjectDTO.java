package nz.ac.canterbury.seng302.portfolio.model.dto;

import java.sql.Date;

/**
 * Creates a Project class required for creating a table in the database
 */
public class ProjectDTO {
    private int projectId;
    private String projectName;
    private String description;
    private Date startDate;
    private Date endDate;

    public ProjectDTO(int projectId, String projectName, String description, Date startDate, Date endDate) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getProjectId() {return projectId;}

    public String getProjectName() {return projectName;}

    public String getDescription() {return description;}

    public Date getStartDate() {return startDate;}

    public Date getEndDate() {return endDate;}
}