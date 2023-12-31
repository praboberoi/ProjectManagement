package nz.ac.canterbury.seng302.identityprovider.model;

import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * The user entity used to access the database in the identity provider application
 */
@Entity
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String nickname;

    @Column
    private String bio;

    @Column
    private String pronouns;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String salt;

    @Column
    private String profileImagePath;

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = "order_column")
    private List<UserRole> roles = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    public Date dateCreated;

    @ManyToMany(mappedBy = "users")
    List<Groups> groups;

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getBio() {
        return bio;
    }

    public String getPersonalPronouns() {
        return pronouns;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public String getProfileImagePath() { return profileImagePath; }

    public List<UserRole> getRoles() {
        return roles;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setPersonalPronouns( String pronouns) {
        this.pronouns = pronouns;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setProfileImagePath(String profileImagePath) { this.profileImagePath = profileImagePath; }

    public void setRoles(List<UserRole> roles) {
        Collections.sort(roles);
        this.roles = roles;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * Creates a new ArrayList type of roles (because cannot add a UserRole to an Abstract list)
     * and the new list including the new role replaces the old list.
     * @param role : role to be added to the users list of roles.
     */
    public void addRole(UserRole role) {
        List<UserRole> updatedRoles = new ArrayList<>(roles);
        updatedRoles.add(role);
        setRoles(updatedRoles);
    }

    /**
     * Creates a new ArrayList type of roles (because cannot remove a UserRole from an Abstract list)
     * and the new list without the specified role replaces the old list.
     * @param role : role to be removed to the users list of roles.
     */
    public void removeRole(UserRole role) {
        List<UserRole> updatedRoles = new ArrayList<>(roles);
        updatedRoles.remove(role);
        setRoles(updatedRoles);
    }

    /**
     * Blank constructor required by JPA
     */
    public User() {
    }

    /**
     * Creates a new user from a registration request
     * @param request Proto class which contains required idp user info
     * @param password The new users encrypted password
     * @param salt The salt used during the encryption of the password
     */
    public User(UserRegisterRequest request, String password, String salt) {
        this.username = request.getUsername();
        this.firstName = request.getFirstName();
        this.lastName = request.getLastName();
        this.nickname = request.getNickname();
        this.email = request.getEmail();
        this.bio = request.getBio();
        this.pronouns = request.getPersonalPronouns();
        this.password = password;
        this.salt = salt;
        this.roles.add(UserRole.STUDENT);

        this.dateCreated = new Timestamp(new Date().getTime());
    }
}
