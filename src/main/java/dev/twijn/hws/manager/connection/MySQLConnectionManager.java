package dev.twijn.hws.manager.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnectionManager implements ConnectionManager {

    private String host;
    private String database;
    private String username;
    private String password;

    public MySQLConnectionManager(String host, String database, String username, String password) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public Connection createConnection() throws SQLException {
        return  DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?user=" + username + "&password=" + password + "&useSSL=true&verifyServerCertificate=false");
    }

}
