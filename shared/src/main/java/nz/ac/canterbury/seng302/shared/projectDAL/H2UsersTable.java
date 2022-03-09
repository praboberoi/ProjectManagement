package nz.ac.canterbury.seng302.shared.projectDAL;

import nz.ac.canterbury.seng302.shared.projectDAL.Datastore;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class H2UsersTable {
    // JDBC driver name, JDBC means Java database connectivity
    static final String JDBC_DRIVER = "org.h2.Driver";

    public static void createTable(Datastore db)
    {
        try (Connection conn = db.getNewConnection(); Statement stmt = conn.createStatement()){
            // Registering JDBC driver
            Class.forName(JDBC_DRIVER);

            // Create User Database
            String sql = "CREATE TABLE IF NOT EXISTS Users " +
                    "(userId int primary key auto_increment, " +
                    " username VARCHAR(20) not NULL UNIQUE, " +
                    " firstName VARCHAR(20) not NULL, " +
                    " lastName VARCHAR(20) not NULL, " +
                    " nickname VARCHAR(20), " +
                    " bio VARCHAR(255), " +
                    " pronouns VARCHAR(10), " +
                    " email VARCHAR(255) not NULL, " +
                    " password VARCHAR(63) not NULL," +
                    " salt VARCHAR(40) not NULL)";
            stmt.execute(sql);
        } catch(SQLException sqlException) {
            System.out.println(LocalDateTime.now() + sqlException.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(LocalDateTime.now() + "Class.forName failed.");
        }
    }


}
