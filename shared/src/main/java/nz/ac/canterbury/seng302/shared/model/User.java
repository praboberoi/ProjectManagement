package nz.ac.canterbury.seng302.shared.model;

import nz.ac.canterbury.seng302.shared.enums.Roles;

import java.util.ArrayList;

public class User {
    public int userId;
    public String username;
    public String firstName;
    public String lastName;
    public String nickname;
    public String bio;
    public String pronouns;
    public String email;
    public String password;
    public String salt;
    public ArrayList<Roles> roles;

    public User(int userId, String username, String firstName, String lastName, String nickname, String bio, String pronouns, String email, String password, String salt, ArrayList<Roles> roles) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.bio = bio;
        this.pronouns = pronouns;
        this.password = password;
        this.salt = salt;
        this.roles = roles;
    }
}
