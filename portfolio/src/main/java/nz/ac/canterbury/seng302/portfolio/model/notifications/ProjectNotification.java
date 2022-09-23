package nz.ac.canterbury.seng302.portfolio.model.notifications;

import java.util.Objects;

/**
 * Edit notifications for projects. Keeps track of who is editing which project.
 */
public class ProjectNotification {
    private int projectId;
    private String username;
    private boolean active;
    private String sessionId;

    public ProjectNotification(int projectId, String username, boolean active, String sessionId) {
        this.projectId = projectId;
        this.username = username;
        this.active = active;
        this.sessionId = sessionId;
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
        if (!(o instanceof ProjectNotification)) return false;
        ProjectNotification project = (ProjectNotification) o;
        return getProjectId() == project.getProjectId() && Objects.equals(getUsername(), project.getUsername()) && Objects.equals(getSessionId(), project.getSessionId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProjectId(), getUsername(), getSessionId());
    }
}
