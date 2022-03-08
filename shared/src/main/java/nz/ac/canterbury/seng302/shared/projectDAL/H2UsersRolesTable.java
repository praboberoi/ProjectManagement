package nz.ac.canterbury.seng302.shared.projectDAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class H2UsersRolesTable {
    // JDBC driver name and database URL, JDBC means Java database connectivity
    static final String JDBC_DRIVER = "org.h2.Driver";


    public static void createTable(Datastore db)
    {
        try (Connection conn = db.getNewConnection(); Statement stmt = conn.createStatement()) {
            // Registering JDBC driver
            Class.forName(JDBC_DRIVER);

            // Create UserToRole Database
            String sql = "CREATE TABLE IF NOT EXISTS UsersRoles" +
                    "(userId varchar(20) not NULL, " +
                    "roleId int DEFAULT 1, " +
                    "FOREIGN KEY (userId) REFERENCES Users(userId), " +
                    "FOREIGN KEY (roleId) REFERENCES Roles(roleId), " +
                    "PRIMARY KEY (userId, roleId))";
            stmt.executeUpdate(sql);
        } catch(SQLException sqlException) {
            System.out.println(LocalDateTime.now() + sqlException.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(LocalDateTime.now() + "Class.forName failed.");
        }
    }


}


