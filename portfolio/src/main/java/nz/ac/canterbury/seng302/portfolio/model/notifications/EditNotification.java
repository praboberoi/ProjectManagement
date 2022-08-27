package nz.ac.canterbury.seng302.portfolio.model.notifications;

import java.util.Objects;

public class EditNotification {
    private int eventId;
    private int projectId;
    private String username;
    private boolean active;

    public EditNotification(int projectId, int eventId, String username, boolean active) {
        this.eventId = eventId;
        this.projectId = projectId;
        this.username = username;
        this.active = active;
    }

    public int getEventId() {
        return this.eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getProjectId() {
        return this.projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EditNotification notification)) return false;
        return notification.getUsername().equals(username) && notification.getEventId() == eventId && notification.getProjectId() == projectId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, eventId, projectId);
    }
}
