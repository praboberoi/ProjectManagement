package nz.ac.canterbury.seng302.shared.projectDAL;

import nz.ac.canterbury.seng302.shared.enums.Roles;
import nz.ac.canterbury.seng302.shared.projectDAL.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class Datastore {

    // Database URL
    static final String DB_URL = "jdbc:h2:~/Team15Database";

    //  Database credentials
    static final String USER = "sa";
    static final String PASS = "";

    public Datastore() {
        init();
    }

    public void init() {
        H2UsersTable.createTable(this);
        H2RoleTable.createTable(this);
        H2UsersRolesTable.createTable(this);
        //H2DefaultUserTrigger.createTrigger(this);
    }

    public Connection getNewConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

}
