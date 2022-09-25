package nz.ac.canterbury.seng302.portfolio.model.notifications;

import com.fasterxml.jackson.databind.ObjectMapper;


public class EvidenceNotification {

    private int evidenceId;

    private String action;

    private int firstEvidenceId;

    private String userUpdating;
    
    private int userId;


    public EvidenceNotification(int evidenceId, String action, int firstEvidenceId, String userUpdating, int userId) {
        this.evidenceId = evidenceId;
        this.action = action;
        this.firstEvidenceId = firstEvidenceId;
        this.userUpdating = userUpdating;
        this.userId = userId;
    }

    public int getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(int evidenceId) {
        this.evidenceId = evidenceId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getFirstEvidenceId() {
        return firstEvidenceId;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserUpdating() {
        return userUpdating;
    }

    public void setFirstEvidenceId(int firstEvidenceId) {
        this.firstEvidenceId = firstEvidenceId;
    }


    public void setUserUpdating(String userUpdating) {
        this.userUpdating = userUpdating;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "EvidenceNotification{" +
                "evidenceId=" + evidenceId +
                ", action='" + action + '\'' +
                ", firstEvidenceId=" + firstEvidenceId +
                ", userUpdating='" + userUpdating + '\'' +
                ", userId=" + userId +
                '}';
    }
}
