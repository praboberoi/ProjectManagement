package nz.ac.canterbury.seng302.identityprovider.model;

import java.util.ArrayList;

public class User {
    public int userId;
    public String username;
    public String firstName;
    public String lastName;
    public String nickname;
    public String bio;
    public ArrayList<String> pronouns;
    public String email;
    public String password;
    public String salt;
    public ArrayList<String> roles;

    public User(int userId, String username, String firstName, String lastName, String nickname, String bio, ArrayList<String> pronouns, String email, String password, String salt, ArrayList<String> roles) {
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
