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

    private String profileImagePath;

    private List<UserRole> roles = new ArrayList<>();

    private Date dateCreated;

    public User(int userId, String username, String firstName, String lastName, String nickname, String bio, String pronouns, String email, String profileImagePath, List<UserRole> roles, Date dateCreated) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.email = email;
        this.bio = bio;
        this.pronouns = pronouns;
        this.profileImagePath = profileImagePath;
        this.roles = roles;
        this.dateCreated = dateCreated;
    }

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

    public String getPronouns() {
        return pronouns;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImagePath() { return profileImagePath; }

    public List<UserRole> getRoles() {return roles;}

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

    public void setProfileImagePath(String profileImagePath) {this.profileImagePath = profileImagePath; }

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
        this.profileImagePath = response.getProfileImagePath();
        this.roles = response.getRolesList();
        this.dateCreated = new Date(response.getCreated().getSeconds());
        this.userId = response.getId();
    }

    /**
     * Builder Class to build the User
     */
    public static class Builder {
        private int userId;
        private String username = "";
        private String firstName = "";
        private String lastName = "";
        private String nickname;
        private String bio;
        private String pronouns;
        private String email = "";
        private String profileImagePath = "";
        private List<UserRole> roles = new ArrayList<>();
        private Date dateCreated = new Date();

        /**
         * Updates the user with the given id
         *
         * @param userId id of the user
         * @return self
         */
        public Builder userId(int userId) {
            this.userId = userId;
            return this;
        }

        /**
         * Updates the user with the given username
         *
         * @param username username of the user
         * @return self
         */
        public Builder username(String username) {
            this.username = username;
            return this;
        }

        /**
         * Updates the user with the given first name
         *
         * @param firstName first name of the user
         * @return self
         */
        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        /**
         * Updates the user with the given last name
         *
         * @param lastName last name of the user
         * @return self
         */
        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        /**
         * Updates the user with the given nickname
         *
         * @param nickname nickname of the user
         * @return self
         */
        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        /**
         * Updates the user with the given bio
         *
         * @param bio bio of the user
         * @return self
         */
        public Builder bio(String bio) {
            this.bio = bio;
            return this;
        }

        /**
         * Updates the user with the given pronouns
         *
         * @param pronouns personal pronouns of the user
         * @return self
         */
        public Builder pronouns(String pronouns) {
            this.pronouns = pronouns;
            return this;
        }

        /**
         * Updates the user with the given email address
         *
         * @param email email address of the user
         * @return self
         */
        public Builder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * Updates the user with the path to given image
         *
         * @param profileImagePath path of the profile photo of the user
         * @return self
         */
        public Builder profileImagePath(String profileImagePath) {
            this.profileImagePath = profileImagePath;
            return this;
        }

        /**
         * Updates the user with the given roles
         *
         * @param roles bio of the user
         * @return self
         */
        public Builder roles(List<UserRole> roles) {
            this.roles = roles;
            return this;
        }

        /**
         * Updates the user with the given creation date
         *
         * @param dateCreated creation date of the user
         * @return self
         */
        public Builder creationDate(Date dateCreated) {
            this.dateCreated = dateCreated;
            return this;
        }

        /**
         * builds the new User.
         *
         * @return an object of type User
         */
        public User build() {
            return new User(userId, username, firstName, lastName, nickname, bio, pronouns, email, profileImagePath, roles, dateCreated);
        }
    }

}
