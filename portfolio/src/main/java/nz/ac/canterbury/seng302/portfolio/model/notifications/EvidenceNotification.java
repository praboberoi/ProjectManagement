package nz.ac.canterbury.seng302.portfolio.model.notifications;

import java.util.Objects;

/**
 * Notification class for Evidence. Encapsulates the required data for exchanging data between front and back end
 */
public class EvidenceNotification extends Notification{

    private int evidenceId;

    private int userId;

    public EvidenceNotification(int evidenceId, int userId, String username, boolean active, String sessionId) {
        super(username, active, sessionId);
        this.evidenceId = evidenceId;
        this.userId = userId;
    }

    public int getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(int evidenceId) {
        this.evidenceId = evidenceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvidenceNotification)) return false;
        if (! super.equals(o)) return false;
        EvidenceNotification project = (EvidenceNotification) o;
        return (getEvidenceId() == project.getEvidenceId());
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 89 * hash + Objects.hashCode(evidenceId);
        return hash;
    }
}
