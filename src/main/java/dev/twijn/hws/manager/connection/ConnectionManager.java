package dev.twijn.hws.manager.connection;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionManager {
    public Connection createConnection() throws SQLException;
}
