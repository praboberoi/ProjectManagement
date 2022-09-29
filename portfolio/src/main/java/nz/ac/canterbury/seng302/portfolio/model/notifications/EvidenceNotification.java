package nz.ac.canterbury.seng302.portfolio.model.notifications;

import java.util.Objects;

/**
 * Notification class for Evidence. Encapsulates the required data for exchanging data between front and back end
 */
public class EvidenceNotification {

    private int evidenceId;

    private int userId;

    private String username;
    
    private boolean active;

    private String sessionId;

    public EvidenceNotification(int evidenceId, int userId, String username, boolean active, String sessionId) {
        this.evidenceId = evidenceId;
        this.userId = userId;
        this.username = username;
        this.active = active;
        this.sessionId = sessionId;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvidenceNotification)) return false;
        EvidenceNotification project = (EvidenceNotification) o;
        return getEvidenceId() == project.getEvidenceId() && Objects.equals(getUsername(), project.getUsername()) && Objects.equals(getSessionId(), project.getSessionId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEvidenceId(), getUsername(), getSessionId());
    }

    @Override
    public String toString() {
        return "EvidenceNotification{" +
                "evidenceId=" + evidenceId +
                ", username='" + username + '\'' +
                ", active=" + active +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
