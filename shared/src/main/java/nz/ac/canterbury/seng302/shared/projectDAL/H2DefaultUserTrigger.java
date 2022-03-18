package nz.ac.canterbury.seng302.shared.projectDAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class H2DefaultUserTrigger {

    // JDBC driver name and database URL, JDBC means Java database connectivity
    static final String JDBC_DRIVER = "org.h2.Driver";

    public static void createTrigger(Datastore db)
    {
        try (Connection conn = db.getNewConnection(); Statement stmt = conn.createStatement()){
            // Registering JDBC driver
            Class.forName(JDBC_DRIVER);

            // Create User Database
            String sql = "CREATE TRIGGER IF NOT EXISTS DefaultUserRole " +
                    "AFTER INSERT " +
                    "on Users  " +
                    "AS " +
                    "BEGIN " +
                    " INSERT INTO UsersRoles" +
                    "VALUES ( SELECT i.userId from inserted i ) "
                    +"END;";
            stmt.executeUpdate(sql);
        } catch(SQLException sqlException) {
            System.out.println(LocalDateTime.now() + sqlException.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(LocalDateTime.now() + "Class.forName failed.");
        }
    }
}
