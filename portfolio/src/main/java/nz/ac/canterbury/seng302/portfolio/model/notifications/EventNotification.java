package nz.ac.canterbury.seng302.portfolio.model.notifications;

import java.util.Objects;

/**
 * Edit notifications for events. Keeps track of who is editing which event and the project it is associated with
 */
public class EventNotification extends ProjectNotification{
    private int eventId;

    public EventNotification(int projectId, int eventId, String username, boolean active, String sessionId) {
        super(projectId, username, active, sessionId);
        this.eventId = eventId;
    }

    public int getEventId() {
        return this.eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventNotification notification)) return false;
        if (! super.equals(o)) return false;
        return notification.getEventId() == eventId;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 89 * hash + Objects.hashCode(eventId);
        return hash;
    }
}
