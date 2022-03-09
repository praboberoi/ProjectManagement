package nz.ac.canterbury.seng302.shared.projectDAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class H2RoleTable {
    // JDBC driver name and database URL, JDBC means Java database connectivity
    static final String JDBC_DRIVER = "org.h2.Driver";

    public static void createTable(Datastore db)
    {
        try (Connection conn = db.getNewConnection(); Statement stmt = conn.createStatement()){
            // Registering JDBC driver
            Class.forName(JDBC_DRIVER);
            // Create User Database
            String command = "CREATE TABLE IF NOT EXISTS Roles " +
                    "(roleId integer primary key auto_increment, " +
                    " roleName VARCHAR(20) not NULL UNIQUE)";
            stmt.execute(command);
            command = "MERGE INTO Roles (roleId, roleName) " +
                    "VALUES (1, 'Student'), " +
                    "(2, 'Teacher'), " +
                    "(3, 'Course Administrator')";
            stmt.execute(command);
        } catch(SQLException sqlException) {
            System.out.println(LocalDateTime.now() + sqlException.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(LocalDateTime.now() + "Class.forName failed.");
        }
    }



}
