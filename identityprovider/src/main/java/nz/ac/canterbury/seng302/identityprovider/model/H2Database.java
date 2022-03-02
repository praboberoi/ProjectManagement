package nz.ac.canterbury.seng302.identityprovider.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2Database {
    // JDBC driver name and database URL, JDBC means Java database connectivity
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/test";

    //  Database credentials
    static final String USER = "sa";
    static final String PASS = "";

    public H2Database()
    {
        Connection conn = null;
        Statement stmt = null;

        try{
            // Registering JDBC driver
            Class.forName(JDBC_DRIVER);

            // Opening Connection
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // Create User Database
            String sql =  "CREATE TABLE USERS " +
                    "(username VARCHAR(6) not NULL, " +
                    " first VARCHAR(20) not NULL, " +
                    " last VARCHAR(20) not NULL, " +
                    " nickname VARCHAR(20), " +
                    " bio VARCHAR(255), " +
                    " pronouns VARCHAR(10), " +
                    " email VARCHAR(255) not NULL, " +
                    " PRIMARY KEY (username))";
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
        } finally {
            // finally block used to close resources
            try {
                if(stmt!=null) stmt.close();
            } catch(SQLException se2) {
            } // nothing we can do
            try {
                if(conn!=null) conn.close();
            } catch(SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try
    }

}
