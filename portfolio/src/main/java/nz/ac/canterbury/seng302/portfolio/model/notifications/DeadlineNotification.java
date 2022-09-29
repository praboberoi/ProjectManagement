package nz.ac.canterbury.seng302.portfolio.model.notifications;

import java.util.Objects;

/**
 * Edit notifications for deadlines. Keeps track of who is editing which deadline and the project it is associated with.
 */
public class DeadlineNotification extends ProjectNotification{
    private int deadlineId;

    public DeadlineNotification(int deadlineId, int projectId, String username, boolean active, String sessionId) {
        super(projectId, username, active, sessionId);
        this.deadlineId = deadlineId;
    }

    public int getDeadlineId() {
        return deadlineId;
    }

    public void setDeadlineId(int deadlineId) {
        this.deadlineId = deadlineId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeadlineNotification)) return false;
        if (! super.equals(o)) return false;
        DeadlineNotification that = (DeadlineNotification) o;
        return getDeadlineId() == that.getDeadlineId();
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 89 * hash + Objects.hashCode(deadlineId);
        return hash;
    }
}
