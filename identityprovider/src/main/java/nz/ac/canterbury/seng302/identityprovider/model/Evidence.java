package nz.ac.canterbury.seng302.identityprovider.model;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.Date;

/**
 * The evidence entity stored in the database for the identity provider application
 */
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "users_evidence", joinColumns = @JoinColumn(name = "evidence_id", referencedColumnName = "EvidenceId"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "UserId"))
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
     * @param dateOccurred The date the evidence occurred.
     * @param title The title of the evidence (max 50).
     * @param description the description of the evidence.
     * @param ownerId the user id of the creator and owner of the evidence.
     */
    public Evidence(Date dateOccurred, String title, String description, Integer ownerId)  {
        this.dateOccurred = dateOccurred;
        this.title = title;
        this.description = description;
        this.ownerId = ownerId;
    }
}
