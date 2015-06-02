package org.ado.atf.db;

import org.ado.atf.Config;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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

    private static final File DATABASE_FILE = new File(Config.ANDROID_TEST_FRAMEWORK_DIR, "test.db");

    public void close() throws IOException {
        FileUtils.forceDelete(DATABASE_FILE);
    }

    public Connection getConnection() throws SQLException {
        return getConnection(DATABASE_FILE);
    }

    private Connection getConnection(File databaseFile) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            if (!databaseFile.exists()) {
                FileUtils.touch(databaseFile);
            }
            return DriverManager.getConnection(String.format("jdbc:sqlite:%s", databaseFile.getAbsolutePath()));
        } catch (Exception e) {
            throw new SQLException(String.format("Unable to establish connection to database \"%s\".", databaseFile), e);
        }
    }
}
