package nz.ac.canterbury.seng302.identityprovider.model;

import java.util.List;

import javax.persistence.*;

/**
 * The group entity stored in the database for the identity provider application
 * Note: Groups as group is a SQL service word
 */
@Entity
public class Groups {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int groupId;

    @Column(unique = true, nullable = false, length = 15)
    private String shortName;

    @Column(unique = true, nullable = false, length = 25)
    private String longName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_groups", joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "GroupId"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "UserId"))
    private List<User> users;

    public int getGroupId() {
        return this.groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return this.longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public List<User> getUsers() {
        return this.users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    /**
     * Blank constructor required by JPA
     */
    public Groups() {
    }

    /**
     * Creates a new group object with the provided details
     * @param shortName The short name of the group (max 15)
     * @param longName The long name of the group (max 25)
     */
    public Groups(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
    }
}
