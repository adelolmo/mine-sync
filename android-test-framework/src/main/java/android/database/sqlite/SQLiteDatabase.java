package android.database.sqlite;

import android.database.Cursor;
import org.ado.atf.SqlLiteDatabase;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Class description here.
 *
 * @author andoni
 * @since 30.06.2014
 */
public class SQLiteDatabase  {

    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy) {

        SqlLiteCursor sqlLiteCursor = new SqlLiteCursor();
        try {
            Connection connection = SqlLiteDatabase.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return sqlLiteCursor;
    }

}
