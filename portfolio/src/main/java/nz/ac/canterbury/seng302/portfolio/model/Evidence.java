package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.util.Date;

/**
 * The evidence entity stored in the database for the portfolio application
 */
@Entity
public class Evidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int evidenceId;

    @Column(nullable = false, length=50)
    private String title;

    @Column(nullable = false, length = 250)
    private String description;

    @Column(nullable = false)
    private Date dateOccurred;

    @Column(nullable = false)
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

    public void setDateOccurred(Date dateOccurred) {
        this.dateOccurred = dateOccurred;
    }

    public void setEvidenceId(int evidenceId) {
        this.evidenceId = evidenceId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Blank constructor required by JPA
     */
    public Evidence() {}

    /**
     * Creates a new evidence object with the provided details
     * @param evidenceId id of the evidence object.
     * @param dateOccurred The date the evidence occurred.
     * @param title The title of the evidence (max 50).
     * @param description the description of the evidence.
     * @param ownerId the user id of the creator and owner of the evidence.
     */
    public Evidence(Integer evidenceId, Date dateOccurred, String title, String description, Integer ownerId)  {
        this.evidenceId = evidenceId;
        this.dateOccurred = dateOccurred;
        this.title = title;
        this.description = description;
        this.ownerId = ownerId;
    }

    /**
     * Builder class to construct an evidence object.
     */
    public static class Builder {
        private Integer evidenceId;
        private Date dateOccurred;
        private String title;
        private String description = "";
        private Evidence evidence;
        private Integer ownerId;

        /**
         * Builds the current Builder with the given evidence id
         * @param evidenceId of type int.
         * @return The current Builder.
         */
        public Evidence.Builder evidenceId(int evidenceId) {
            this.evidenceId = evidenceId;
            return this;
        }

        /**
         * Builds the current Builder with the given date occurred label.
         * @param dateOccurred of type String
         * @return the current Builder
         */
        public Evidence.Builder dateOccurred(Date dateOccurred) {
            this.dateOccurred = dateOccurred;
            return this;
        }

        /**
         * Builds the current Builder with the given evidence title.
         * @param title of type String
         * @return the current Builder
         */
        public Evidence.Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Builds the current Builder with the given description
         * @param description of type String.
         * @return the current Builder.
         */
        public Evidence.Builder description(String description) {
            this.description = description;
            return this;
        }


        /**
         * Builds the current Builder with the given ownerId.
         * @param ownerId user id of the owner of the evidence.
         * @return the current Builder
         */
        public Evidence.Builder ownerId(Integer ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        /**
         * Returns a new Evidence with all the parameters of the current Builder
         * @return Evidence object
         */
        public Evidence build() {
            return new Evidence(evidenceId, dateOccurred, title, description, ownerId);
        }
    }
}
