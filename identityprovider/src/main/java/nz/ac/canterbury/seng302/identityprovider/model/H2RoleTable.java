package nz.ac.canterbury.seng302.identityprovider.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2RoleTable {
    // JDBC driver name and database URL, JDBC means Java database connectivity
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/Team15Database";

    //  Database credentials
    static final String USER = "sa";
    static final String PASS = "";

    public H2RoleTable()
    {
        try (Connection conn = DriverManager.getConnection(DB_URL,USER,PASS); Statement stmt = conn.createStatement()){
            // Registering JDBC driver
            Class.forName(JDBC_DRIVER);
            // Create User Database
            String sql = "DROP TABLE IF EXISTS ROLES; " +
                    "CREATE TABLE ROLES " +
                    "(id int not NULL, " +
                    " name VARCHAR(20) not NULL, " +
                    " PRIMARY KEY (id))";
            stmt.executeUpdate(sql);
            sql = "INSERT INTO ROLES " +
                    "VALUES (1, 'Student'), " +
                    "(2, 'Teacher'), " +
                    "(3, 'Course Administrator')";
            stmt.executeUpdate(sql);
            // STEP 4: Clean-up environment
            stmt.close();
            conn.close();
        } catch(SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch(Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        }
    }



}
