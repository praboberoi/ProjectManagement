package nz.ac.canterbury.seng302.portfolio.model.dto;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.utils.SprintColor;
import java.sql.Date;
import java.util.Objects;

/**
 * The SprintDTO object for transferring the date from the frontend to the database
 */
public class SprintDTO {
    private int sprintId;

    private Project project;

    private String sprintLabel;

    private String sprintName;


    private String description;


    private Date startDate;

    private Date endDate;


    private SprintColor color = SprintColor.WHITE;


    /**
     * Constructor for the sprintDTO class with arguments
     * @param sprintId id of the sprint.
     * @param project Project associated with the sprint.
     * @param sprintLabel Label of the sprint.
     * @param sprintName Name of the sprint.
     * @param description Description of the sprint.
     * @param startDate Start date of the sprint.
     * @param endDate End date of the sprint.
     */
    public SprintDTO(int sprintId, Project project, String sprintLabel, String sprintName, String description, Date startDate, Date endDate, SprintColor color) {
        this.sprintId = sprintId;
        this.project = project;
        this.sprintLabel= sprintLabel;
        this.sprintName = sprintName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.color = color;
    }


    public int getSprintId() {
        return sprintId;
    }

    public Project getProject() {
        return project;
    }


    public String getSprintLabel() {
        return sprintLabel;
    }


    public String getSprintName() {
        return sprintName;
    }


    public String getDescription() {
        return description;
    }


    public Date getStartDate() {
        return startDate;
    }


    public Date getEndDate() {
        return endDate;
    }


    public SprintColor getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "SprintDTO{" +
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SprintDTO)) return false;
        SprintDTO sprintDTO = (SprintDTO) o;
        return sprintId == sprintDTO.sprintId && project.equals(sprintDTO.project) && sprintLabel.equals(sprintDTO.sprintLabel) && sprintName.equals(sprintDTO.sprintName) && description.equals(sprintDTO.description) && startDate.equals(sprintDTO.startDate) && endDate.equals(sprintDTO.endDate) && color == sprintDTO.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sprintId, project, sprintLabel, sprintName, description, startDate, endDate, color);
    }
}
