package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;

import java.util.List;

public class Groups {
    private  String shortName;
    private String longName;

    private int groupId;

    private List<User> members;

    public Groups(String shortName, String longName, int groupId, List<User> members) {
        this.longName = longName;
        this.shortName = shortName;
        this.groupId = groupId;
        this.members = members;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public Groups () {}
    
    public Groups(GroupDetailsResponse response) {
        this.groupId = response.getGroupId();
        this.shortName = response.getShortName();
        this.longName = response.getLongName();
        this.members = response.getMembersList().stream().map(User::new).toList();
    }
}
