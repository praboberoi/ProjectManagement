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

    private int ownerId;


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


    /**
     * Blank constructor required by JPA
     */
    public EvidenceDTO() {}

    /**
     * Creates a new evidenceDTO object with the provided details
     * @param evidenceId id of the evidence object.
     * @param dateOccurred The date the evidence occurred.
     * @param title The title of the evidence (max 50).
     * @param description the description of the evidence.
     * @param ownerId the user id of the creator and owner of the evidence.
     */
    public EvidenceDTO(int evidenceId, Project project, Date dateOccurred, String title, String description, int ownerId)  {
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


    /**
     * Builder class to construct an evidenceDTO object.
     */
    public static class Builder {
        private int evidenceId;
        private Date dateOccurred;
        private String title;
        private String description = "";
        private int ownerId;
        private Project project;

        /**
         * Builds the current Builder with the given evidence id
         * @param evidenceId of type int.
         * @return The current Builder.
         */
        public EvidenceDTO.Builder evidenceId(int evidenceId) {
            this.evidenceId = evidenceId;
            return this;
        }

        /**
         * Builds the current Builder with the given date occurred label.
         * @param dateOccurred of type String
         * @return the current Builder
         */
        public EvidenceDTO.Builder dateOccurred(Date dateOccurred) {
            this.dateOccurred = dateOccurred;
            return this;
        }

        /**
         * Builds the current Builder with the given evidence title.
         * @param title of type String
         * @return the current Builder
         */
        public EvidenceDTO.Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Builds the current Builder with the given project.
         * @param project of type Project
         * @return the current Builder
         */
        public EvidenceDTO.Builder project(Project project) {
            this.project = project;
            return this;
        }

        /**
         * Builds the current Builder with the given description
         * @param description of type String.
         * @return the current Builder.
         */
        public EvidenceDTO.Builder description(String description) {
            this.description = description;
            return this;
        }


        /**
         * Builds the current Builder with the given ownerId.
         * @param ownerId user id of the owner of the evidence.
         * @return the current Builder
         */
        public EvidenceDTO.Builder ownerId(int ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        /**
         * Returns a new Evidence with all the parameters of the current Builder
         * @return Evidence object
         */
        public EvidenceDTO build() {
            return new EvidenceDTO(evidenceId, project, dateOccurred, title, description, ownerId);
        }
    }

}
