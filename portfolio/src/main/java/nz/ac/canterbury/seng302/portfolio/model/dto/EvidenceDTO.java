package nz.ac.canterbury.seng302.portfolio.model.dto;

import nz.ac.canterbury.seng302.portfolio.model.Project;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * The evidenceDTO entity stored in the database for the portfolio application
 */

public class EvidenceDTO {


    private int evidenceId;

    private Project project;

    private String title;

    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOccurred;

    private int ownerId;

    private String weblinks;


    public int getEvidenceId() {
        return this.evidenceId;
    }

    public Date getDateOccurred() {
        return this.dateOccurred;
    }

    public int getOwnerId() {
        return this.ownerId;
    }

    public String getDescription() {
        return this.description;
    }

    public String getTitle() {
        return this.title;
    }

    public Project getProject() {return project;}

    public String getWeblinks() {
        return weblinks;
    }

    /**
     * Creates a new evidenceDTO object with the provided details
     * @param evidenceId id of the evidence object.
     * @param dateOccurred The date the evidence occurred.
     * @param title The title of the evidence (max 50).
     * @param description the description of the evidence.
     * @param ownerId the user id of the creator and owner of the evidence.
     */
    public EvidenceDTO(int evidenceId, Project project, Date dateOccurred, String title, String description, int ownerId,
                       String weblinks)  {
        this.evidenceId = evidenceId;
        this.project = project;
        this.dateOccurred = dateOccurred;
        this.title = title;
        this.description = description;
        this.ownerId = ownerId;
        this.weblinks = weblinks;
    }
}
