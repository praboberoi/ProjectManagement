package nz.ac.canterbury.seng302.portfolio.model.notifications;

import java.util.Objects;

/**
 * Edit notifications for projects. Keeps track of who is editing which project.
 */
public class ProjectNotification extends Notification{
    private int projectId;

    public ProjectNotification(int projectId, String username, boolean active, String sessionId) {
        super(username, active, sessionId);
        this.projectId = projectId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectNotification)) return false;
        if (! super.equals(o)) return false;
        ProjectNotification project = (ProjectNotification) o;
        return getProjectId() == project.getProjectId();
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 89 * hash + Objects.hashCode(getProjectId());
        return hash;
    }
}
