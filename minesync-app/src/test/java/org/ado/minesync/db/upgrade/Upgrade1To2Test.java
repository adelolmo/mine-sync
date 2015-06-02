package org.ado.minesync.db.upgrade;

import android.database.sqlite.SQLiteDatabase;
import org.ado.atf.db.SqlLiteDatabase;
import org.ado.minesync.ClassTestCase;
import org.ado.minesync.commons.DateUtils;
import org.ado.minesync.db.SyncTypeEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.Date;

import static org.ado.minesync.db.GeneralTableColumns.KEY_ID;
import static org.ado.minesync.db.TableWorldColumns.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Andoni del Olmo
 * @since 28.05.15
 */
public class Upgrade1To2Test extends ClassTestCase<Upgrade1To2> {

    private SQLiteDatabase sqLiteDatabase;
    private SqlLiteDatabase tmpDatabase;

    public static final String CREATE_WORLD_TABLE_V1 = "create table " + WORLD_TABLE
            + " (" + KEY_ID + " integer primary key autoincrement, "
            + WORLD_NAME_COLUMN + " text UNIQUE not null, "
            + WORLD_MODIFICATION_DATE_COLUMN + " timestamp not null, "
            + WORLD_SIZE_COLUMN + " long);";

    @Before
    public void setUp() throws Exception {
        tmpDatabase = new SqlLiteDatabase();
        sqLiteDatabase = new SQLiteDatabase(tmpDatabase);
    }

    @After
    public void tearDown() throws Exception {
        sqLiteDatabase.close();
        tmpDatabase.close();
    }

    @Test
    public void testCreate() throws Exception {
        unitUnderTest.create(sqLiteDatabase);

        verifyHistoryTable();
    }

    @Test
    public void testUpgrade() throws Exception {
        executeQuery(CREATE_WORLD_TABLE_V1);
        final Date date = new Date();
        insertWorld(0, "world name", DateUtils.formatSqlLiteDate(date), 100);

        unitUnderTest.upgrade(sqLiteDatabase);

        verifyWorldTable();
        verifyHistoryTable();
        verifyWorld(1, "world name", DateUtils.formatSqlLiteDate(date), 100, SyncTypeEnum.AUTO);
    }

    private void executeQuery(String sql) throws SQLException {
        final Connection connection = tmpDatabase.getConnection();
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } finally {
            connection.close();
        }
    }

    private void insertWorld(int id, String name, String date, long size) throws SQLException {
        final Connection connection = tmpDatabase.getConnection();

        try {
            String query = "INSERT INTO world (_id, NAME_COLUMN, MODIFICATION_DATE_COLUMN, SIZE_COLUMN) VALUES (?,?,?,?)";
            final PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, id);
            statement.setString(2, name);
            statement.setString(3, date); // 2014-12-14 16:40:57
            statement.setLong(4, size);
            final int i = statement.executeUpdate();
        } finally {
            connection.close();
        }
    }

    private void verifyWorld(int id, String name, String date, long size, SyncTypeEnum syncType) throws SQLException {
        final String query = "SELECT _id, NAME_COLUMN, MODIFICATION_DATE_COLUMN, SIZE_COLUMN, SYNC_TYPE_COLUMN FROM world WHERE _id = ?";
        final ResultSet resultSet = getResultSet(query, id);
        if (resultSet.next()) {
            assertEquals("id", id, resultSet.getInt("_id"));
            assertEquals("name", name, resultSet.getString("NAME_COLUMN"));
            assertEquals("date", date, resultSet.getString("MODIFICATION_DATE_COLUMN"));
            assertEquals("size", size, resultSet.getLong("SIZE_COLUMN"));
            assertEquals("syncType", syncType.getSyncType(), resultSet.getInt("SYNC_TYPE_COLUMN"));
        } else {
            fail("world not found with id \"" + id + "\".");
        }
    }

    private void verifyWorldTable() throws SQLException {
        final String query = "SELECT _id, NAME_COLUMN, MODIFICATION_DATE_COLUMN, SIZE_COLUMN, SYNC_TYPE_COLUMN FROM world";
        final ResultSet resultSet = getResultSet(query);
        final boolean next = resultSet.next();
        System.out.println(next);
    }

    private void verifyHistoryTable() throws SQLException {
        final String query = "SELECT _id, WORLD_ID_COLUMN, DATE_COLUMN, ACTION_COLUMN, SIZE FROM history";
        final ResultSet resultSet = getResultSet(query);
        final boolean next = resultSet.next();
        System.out.println(next);
    }

    private ResultSet getResultSet(String query) throws SQLException {
        final Connection connection = tmpDatabase.getConnection();
        final PreparedStatement preparedStatement = connection.prepareStatement(query);
        return preparedStatement.executeQuery();
    }

    private ResultSet getResultSet(String query, int id) throws SQLException {
        final Connection connection = tmpDatabase.getConnection();
        final PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        return preparedStatement.executeQuery();
    }
}