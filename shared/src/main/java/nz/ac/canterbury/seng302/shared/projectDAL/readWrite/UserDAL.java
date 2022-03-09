package nz.ac.canterbury.seng302.shared.projectDAL.readWrite;

import nz.ac.canterbury.seng302.shared.enums.Roles;
import nz.ac.canterbury.seng302.shared.projectDAL.Datastore;
import nz.ac.canterbury.seng302.shared.projectDAL.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class UserDAL {
    public static User getUser(Datastore db, int userId) {
        User user = null;
        try (Connection conn = db.getNewConnection();
             Statement stmt = conn.createStatement()) {
            var reader = stmt.executeQuery("SELECT UserId, Username, FirstName, LastName, Nickname, Bio, Pronouns, Email, Password, Salt FROM USERS WHERE UserId = " + userId);
            while (reader.next()) {
                user = new User(
                        reader.getInt("userId"),
                        reader.getString("Username"),
                        reader.getString("FirstName"),
                        reader.getString("LastName"),
                        reader.getString("Nickname"),
                        reader.getString("bio"),
                        reader.getString("pronouns"),
                        reader.getString("email"),
                        reader.getString("password"),
                        reader.getString("salt"),
                        null
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static User getUserByUsername(Datastore db, String username) {
        User user = null;
        try (Connection conn = db.getNewConnection();
             Statement stmt = conn.createStatement()) {
            var reader = stmt.executeQuery("SELECT UserId, Username, FirstName, LastName, Nickname, Bio, Pronouns, Email, Password, Salt FROM USERS WHERE Username = '" + username + "'");
            while (reader.next()) {
                user = new User(
                        reader.getInt("UserId"),
                        reader.getString("Username"),
                        reader.getString("FirstName"),
                        reader.getString("LastName"),
                        reader.getString("Nickname"),
                        reader.getString("bio"),
                        reader.getString("pronouns"),
                        reader.getString("email"),
                        reader.getString("password"),
                        reader.getString("salt"),
                        new ArrayList<Roles>()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static boolean addUser(Datastore db, String username, String firstName, String lastName, String nickname, String bio, String pronouns, String email, String password, String salt, ArrayList<Roles> roles) throws SQLException {
        try (Connection conn = db.getNewConnection();
                Statement stmt = conn.createStatement()
                ) {
            stmt.execute("INSERT INTO Users (username, firstName, lastName, nickname, bio, pronouns, email, password, salt)" +
                    " VALUES ('" + username + "', '" + firstName + "', '" + lastName + "', '" + nickname + "', '" + bio + "', '" + pronouns + "', '" + email + "', '" + password + "', '" + salt + "')");
            return true;
        } catch (SQLException e) {
            if (!e.getMessage().contains("Unique index or primary key violation")) {
                throw e;
            }
        }
        return false;
    }
}