package nz.ac.canterbury.seng302.identityprovider.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2UsersRolesTable {
    // JDBC driver name and database URL, JDBC means Java database connectivity
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/Team15Database";

    //  Database credentials
    static final String USER = "sa";
    static final String PASS = "";

    public H2UsersRolesTable()
    {
        try (Connection conn = DriverManager.getConnection(DB_URL,USER,PASS); Statement stmt = conn.createStatement()) {
            // Registering JDBC driver
            Class.forName(JDBC_DRIVER);

            // Create UserToRole Database
            String sql = "DROP TABLE IF EXISTS UsersRoles; " +
                    "CREATE TABLE UsersRoles" +
                    "(userId varchar(20) not NULL, " +
                    "roleId int DEFAULT 1, " +
                    "FOREIGN KEY (userId) REFERENCES Users(userId), " +
                    "FOREIGN KEY (roleId) REFERENCES Roles(roleId), " +
                    "PRIMARY KEY (userId, roleId))";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch(SQLException se) {
            se.printStackTrace();
        } catch(Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        }
    }


}


