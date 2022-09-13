package nz.ac.canterbury.seng302.portfolio.model.dto;

import java.util.Date;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Project;

/**
 * The repoDTO entity stored in the database for the portfolio application
 */

public class DeadlineDTO {

    private int deadlineId;

    private Project project;

    private String name;

    private Date date;

    public int getDeadlineId() {return deadlineId;}

    public Project getProject() {return project;}

    public String getName() {return name;}

    public Date getDate() {return date;}

    public DeadlineDTO() {}

    /**
     * Creates a new repoDTO object with the provided details
     * @param repoId The ID of the repo object
     * @param groupId GroupId of the repo object
     * @param repoName Name of the repo object
     * @param gitlabProjectId The ID of the gitlab project
     * @param accessToken The access token
     * @param hostAddress The host address
     */
    public DeadlineDTO(int deadlineId, String name, Project project, Date date){
        this.deadlineId = deadlineId;
        this.name = name;
        this.project = project;
        this.date = date;
    }

    public DeadlineDTO(Deadline deadline){
        deadlineId = deadline.getDeadlineId();
        name = deadline.getName();
        project = deadline.getProject();
        date = deadline.getDate();
    }
}
