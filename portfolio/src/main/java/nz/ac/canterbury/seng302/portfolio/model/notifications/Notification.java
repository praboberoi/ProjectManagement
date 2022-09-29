package nz.ac.canterbury.seng302.portfolio.model.notifications;

import java.util.Objects;

/**
 * Edit notifications base class. Keeps track of who is editing which project.
 */
public class Notification {
    private String username;
    private boolean active;
    private String sessionId;

    public Notification(String username, boolean active, String sessionId) {
        this.username = username;
        this.active = active;
        this.sessionId = sessionId;
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
        if (!(o instanceof Notification)) return false;
        Notification project = (Notification) o;
        return Objects.equals(getUsername(), project.getUsername()) && Objects.equals(getSessionId(), project.getSessionId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getSessionId());
    }
}
