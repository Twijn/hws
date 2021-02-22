package dev.twijn.hws.manager.connection;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnectionManager implements ConnectionManager {

    private File databaseFile;

    public SQLiteConnectionManager(File dataFolder) {
        this.databaseFile = new File(dataFolder, "database.db");
    }

    public Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());
    }
}
