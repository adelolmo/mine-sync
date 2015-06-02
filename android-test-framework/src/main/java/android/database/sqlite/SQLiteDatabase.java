package android.database.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import org.ado.atf.db.CursorImpl;
import org.ado.atf.db.SqlLiteDatabase;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

//import org.ado.atf.SqlLiteDatabase;

/**
 * Class description here.
 *
 * @author andoni
 * @since 30.06.2014
 */
public class SQLiteDatabase {

    private final SqlLiteDatabase tmpDatabase;

    public SQLiteDatabase(SqlLiteDatabase tmpDatabase) throws SQLException {
        this.tmpDatabase = tmpDatabase;
    }

    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy) throws SQLException {

//        new android.database.sqlite.SQLiteDirectCursorDriver();
//        SqlLiteCursor sqlLiteCursor = new android.database.sqlite.SQLiteCursor();

        //     public SQLiteCursor(SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {

//        final SQLiteCursorDriver driver = new SQLiteDirectCursorDriver();
//        final SQLiteCursor cursor = new SQLiteCursor(driver, "", new SQLiteQuery());

        return new CursorImpl(tmpDatabase, columns, table, orderBy);
    }

    public void execSQL(String sql) throws SQLException {
        Statement stmt = null;
        try {
            final Connection connection = tmpDatabase.getConnection();
            stmt = connection.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public long insert(String table, String nullColumnHack, ContentValues values) {
        System.out.println("insert into " + table);
        StringBuilder columnsString = new StringBuilder();//StringUtils.join(values.keySet(), ", ");
        StringBuilder valuesString = new StringBuilder();//StringUtils.join(values.(), ", ");

        for (String key : values.keySet()) {
            columnsString.append(key).append(", ");
            final Object obj = values.get(key);
            if (obj instanceof Number) {
                valuesString.append(obj).append(", ");
            } else {
                valuesString.append("'").append(obj).append("'").append(", ");
            }
        }
        columnsString.setLength(columnsString.length() - 2);
        valuesString.setLength(valuesString.length() - 2);

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", table, columnsString, valuesString);
        try {
            execSQL(sql);
        } catch (SQLException e) {
            return -1;
        }
        return 0;
    }

    public void close() throws SQLException {
        tmpDatabase.getConnection().close();
    }

    public final String getPath() {
        return "mock";
    }

    public int getVersion() {
        return 69;
    }
    public interface CursorFactory {
        /**
         * See {@link SQLiteCursor#SQLiteCursor(SQLiteCursorDriver, String, SQLiteQuery)}.
         */
        public Cursor newCursor(SQLiteDatabase db,
                                SQLiteCursorDriver masterQuery, String editTable,
                                SQLiteQuery query);
    }
}
