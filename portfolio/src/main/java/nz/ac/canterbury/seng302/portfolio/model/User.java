package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User class with required information for use in the portfolio application
 */
public class User implements Serializable {
    private int userId;

    private String username;

    private String firstName;

    private String lastName;

    private String nickname;

    private String bio;

    private String pronouns;

    private String email;

    private List<UserRole> roles = new ArrayList<>();

    private Date dateCreated;

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

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User() {

    }

    public User(UserResponse response) {
        this.username = response.getUsername();
        this.firstName = response.getFirstName();
        this.lastName = response.getLastName();
        this.nickname = response.getNickname();
        this.email = response.getEmail();
        this.bio = response.getBio();
        this.pronouns = response.getPersonalPronouns();
        this.roles = response.getRolesList();
        this.dateCreated = new Date(response.getCreated().getSeconds());
    }
}
