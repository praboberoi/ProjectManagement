package nz.ac.canterbury.seng302.portfolio.model.dto;

import nz.ac.canterbury.seng302.portfolio.model.Project;

import java.util.Date;
import java.util.Objects;

/**
 * The evidenceDTO entity stored in the database for the portfolio application
 */

public class EvidenceDTO {


    private int evidenceId;

    private Project project;

    private String title;

    private String description;

    private Date dateOccurred;

    private Integer ownerId;


    public int getEvidenceId() {
        return this.evidenceId;
    }

    public Date getDateOccurred() {
        return this.dateOccurred;
    }

    public Integer getOwnerId() {
        return this.ownerId;
    }

    public String getDescription() {
        return this.description;
    }

    public String getTitle() {
        return this.title;
    }

    public Project getProject() {return project;}


    /**
     * Blank constructor required by JPA
     */
    public EvidenceDTO() {}

    /**
     * Creates a new evidence object with the provided details
     * @param evidenceId id of the evidence object.
     * @param dateOccurred The date the evidence occurred.
     * @param title The title of the evidence (max 50).
     * @param description the description of the evidence.
     * @param ownerId the user id of the creator and owner of the evidence.
     */
    public EvidenceDTO(Integer evidenceId, Project project, Date dateOccurred, String title, String description, Integer ownerId)  {
        this.evidenceId = evidenceId;
        this.project = project;
        this.dateOccurred = dateOccurred;
        this.title = title;
        this.description = description;
        this.ownerId = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvidenceDTO)) return false;
        EvidenceDTO evidenceDTO = (EvidenceDTO) o;
        return evidenceId == evidenceDTO.evidenceId && Objects.equals(project, evidenceDTO.project) && Objects.equals(title, evidenceDTO.title) && Objects.equals(dateOccurred, evidenceDTO.dateOccurred);
    }

    @Override
    public int hashCode() {
        return Objects.hash(evidenceId, project, title, description, dateOccurred);
    }


}
