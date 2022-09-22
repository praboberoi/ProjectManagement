package nz.ac.canterbury.seng302.portfolio.model.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import nz.ac.canterbury.seng302.portfolio.model.Project;

/**
 * The milestoneDTO entity stored in the database for the portfolio application
 */

public class MilestoneDTO {

    private int milestoneId;

    private Project project;

    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    public int getMilestoneId() {return milestoneId;}

    public Project getProject() {return project;}

    public String getName() {return name;}

    public Date getDate() {return date;}

    /**
     * Creates a new milestoneDTO object with the provided details
      * @param milestoneId The ID of the milestone object
      * @param name Name of the milestone
      * @param project Project milestone is contained in
      * @param date Date the project occurs
      */
    public MilestoneDTO(int milestoneId, String name, Project project, Date date){
        this.milestoneId = milestoneId;
        this.name = name;
        this.project = project;
        this.date = date;
    }
}
