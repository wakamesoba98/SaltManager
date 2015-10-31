package net.wakamesoba98.saltmanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager extends ContextWrapper {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String COL_ID = "_id";
    protected SQLiteDatabase mSQLiteDatabase = null;
    protected String mTableName;
    protected String[][] mColumns;
    private String mDatabaseName;
    private int mVersion;
    private Context mContext;

    public DatabaseManager(Context context) {
        super(context);

        mContext = context;

        if (mSQLiteDatabase != null){
            mSQLiteDatabase.close();
        }
    }

    private String whereClause(String key) {
        if (key == null) {
            return null;
        } else {
            return key + "=?";
        }
    }

    private String[] whereArgs(String value) {
        if (value == null) {
            return null;
        } else {
            return new String[]{value};
        }
    }

    public void setDatabaseName(String name) {
        mDatabaseName = name;
    }

    public void setTableName(String table) {
        mTableName = table;
    }

    public void setColumns(String[][] columns) {
        mColumns = columns;
    }

    public void setVersion(int version) {
        mVersion = version;
    }

    public void open() {
        mSQLiteDatabase = new DBHelper(mContext).getWritableDatabase();
        createTableIfNotExists();
    }

    public void close() {
        mSQLiteDatabase.close();
        mSQLiteDatabase = null;
    }

    public List<String[]> readBetween(String key, String start, String end) {
        return read(mSQLiteDatabase.query(mTableName, null, key + " BETWEEN ? AND ?",
                                          new String[]{start, end}, null, null, null));
    }

    public List<String[]> read(String key, String value) {
        return read(mSQLiteDatabase.query(mTableName, null, whereClause(key),
                                          whereArgs(value), null, null, null));
    }

    private List<String[]> read(Cursor cursor) {
        boolean isEof = cursor.moveToFirst();
        List<String[]> dataList = new ArrayList<>();

        while (isEof) {
            List<String> column = new ArrayList<>();
            for (int i=0; i<cursor.getColumnCount(); i++) {
                column.add(cursor.getString(i));
            }
            dataList.add(column.toArray(new String[column.size()]));

            isEof = cursor.moveToNext();
        }
        cursor.close();

        return dataList;
    }

    public void insert(String[] data) {
        ContentValues values = new ContentValues();

        for (int i=0; i<mColumns.length; i++) {
            values.put(mColumns[i][0], data[i]);
        }

        mSQLiteDatabase.insert(mTableName, null, values);
    }

    public void update(String[] data) {
        ContentValues values = new ContentValues();

        for (int i=0; i<mColumns.length; i++) {
            values.put(mColumns[i][0], data[i]);
        }

        mSQLiteDatabase.update(mTableName, values, whereClause(mColumns[0][0]), whereArgs(data[0]));
    }

    public void deleteAll() {
        mSQLiteDatabase.delete(mTableName, null, null);
    }

    public void delete(String key, String value) {
        mSQLiteDatabase.delete(mTableName, whereClause(key), whereArgs(value));
    }

    public int getCount() {
        Cursor cursor;
        cursor = mSQLiteDatabase.query(mTableName, null, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    protected void createTableIfNotExists() {
        StringBuilder exec = new StringBuilder();

        exec.append("CREATE TABLE IF NOT EXISTS ");
        exec.append(mTableName);
        exec.append(" (");
        exec.append(COL_ID);
        // AUTOINCREMENT を付けると _id の値の再割当てが行われなくなる
        exec.append(" INTEGER PRIMARY KEY AUTOINCREMENT");
        for (String[] column : mColumns) {
            exec.append(",");
            exec.append(column[0]);
            exec.append(" ");
            exec.append(column[1]);
            exec.append(" NOT NULL");
        }
        exec.append(");");

        mSQLiteDatabase.execSQL(exec.toString());
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, mDatabaseName, null, mVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + mDatabaseName);
            onCreate(db);
        }
    }
}
