package org.ado.atf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class description here.
 *
 * @author andoni
 * @since 30.06.2014
 */
public class SqlLiteDatabase {

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Sqlite JDBC driver not found");
        }
        return DriverManager.getConnection("jdbc:sqlite:sample.db");
    }
}
