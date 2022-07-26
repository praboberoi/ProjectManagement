package nz.ac.canterbury.seng302.identityprovider.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.Id;
import javax.persistence.JoinTable;

public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int GroupId;

    @Column(unique = true, nullable = false, length = 25)
    String ShortName;

    @Column(unique = true, nullable = false, length = 25)
    String LongName;

    @ManyToMany
    @JoinTable(
    name = "USERS_GROUPS",
    joinColumns = @JoinColumn(name = "GROUP_ID", referencedColumnName = "ID"),
    inverseJoinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    )
    private List<User> users;

    /**
     * Blank constructor required by JPA
     */
    public Group() {
    }
}
