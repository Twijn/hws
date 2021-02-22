package dev.twijn.hws.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtils {

    public void seedDatabase(Connection con) throws IOException, SQLException {
        InputStream in = getClass().getResourceAsStream("/sql/structure.sql");

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        StringBuilder query = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            query.append(line + " ");

            if (line.equals("")) {
                Statement statement = con.createStatement();
                statement.execute(query.toString().trim());
                statement.close();

                query = new StringBuilder();
            }
        }
        Statement statement = con.createStatement();
        statement.execute(query.toString().trim());
        statement.close();
        reader.close();
    }

}
