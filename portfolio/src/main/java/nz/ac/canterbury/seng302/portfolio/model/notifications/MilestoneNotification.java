package nz.ac.canterbury.seng302.portfolio.model.notifications;

import java.util.Objects;

/**
 * Edit notifications for milestones. Keeps track of who is editing which milestone and the project it is associated with.
 */
public class MilestoneNotification extends ProjectNotification {
    private int milestoneId;

    public MilestoneNotification(int milestoneId, int projectId, String username, boolean active, String sessionId) {
        super(projectId, username, active, sessionId);
        this.milestoneId = milestoneId;
    }

    public int getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(int milestoneId) {
        this.milestoneId = milestoneId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MilestoneNotification)) return false;
        if (! super.equals(o)) return false;
        MilestoneNotification milestone = (MilestoneNotification) o;
        return getMilestoneId() == milestone.getMilestoneId() && getProjectId() == milestone.getProjectId() && Objects.equals(getUsername(), milestone.getUsername()) && Objects.equals(getSessionId(), milestone.getSessionId());
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 89 * hash + Objects.hashCode(milestoneId);
        return hash;
    }
}
