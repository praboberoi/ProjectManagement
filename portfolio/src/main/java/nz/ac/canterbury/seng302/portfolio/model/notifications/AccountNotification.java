package nz.ac.canterbury.seng302.portfolio.model.notifications;

import java.util.Objects;

/**
 * Edit notifications for events. Keeps track of who is editing which event and the project it is associated with
 */
public class AccountNotification {
    private int userId;

    public AccountNotification(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountNotification notification)) return false;
        return notification.getUserId() == userId;

    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
