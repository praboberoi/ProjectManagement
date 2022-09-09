package nz.ac.canterbury.seng302.portfolio.model.notifications;

import java.util.Objects;

/**
 * Edit notifications for deadlines. Keeps track of who is editing which deadline and the project it is associated with.
 */
public class DeadlineNotification {
    private int deadlineId;
    private int projectId;
    private String username;
    private boolean active;
    private String sessionId;

    public DeadlineNotification(int deadlineId, int projectId, String username, boolean active, String sessionId) {
        this.deadlineId = deadlineId;
        this.projectId = projectId;
        this.username = username;
        this.active = active;
        this.sessionId = sessionId;
    }

    public int getDeadlineId() {
        return deadlineId;
    }

    public void setDeadlineId(int deadlineId) {
        this.deadlineId = deadlineId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
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
        if (!(o instanceof DeadlineNotification)) return false;
        DeadlineNotification that = (DeadlineNotification) o;
        return getDeadlineId() == that.getDeadlineId() && getProjectId() == that.getProjectId() && Objects.equals(getUsername(), that.getUsername()) && Objects.equals(getSessionId(), that.getSessionId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDeadlineId(), getProjectId(), getUsername(), getSessionId());
    }
}
