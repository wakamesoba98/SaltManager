package net.wakamesoba98.saltmanager.database;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class SodiumDatabase implements DatabaseController {
    // データベース情報
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "sodium.db";
    private static final String DB_TABLE = "sodium";
    private static final String[][] COLUMNS = {
            {"date", "TEXT"},
            {"food", "TEXT"},
            {"sodium", "INTEGER"}
    };

    private DatabaseManager database;

    public SodiumDatabase(Context context) {
        database = new DatabaseManager(context);
        database.setColumns(COLUMNS);
        database.setDatabaseName(DB_NAME);
        database.setTableName(DB_TABLE);
        database.setVersion(DB_VERSION);
    }

    public String getName() {
        return DB_NAME;
    }

    public void openDatabase() {
        database.open();
    }

    public void closeDatabase() {
        database.close();
    }

    public int getCount() {
        return database.getCount();
    }

    public void insert(String[] data) {
        database.insert(data);
    }

    public void update(String[] data) {
        database.update(data);
    }

    public List<String[]> getData(String key, String value) {
        return database.read(key, value);
    }

    public List<String[]> getAllData() {
        return database.read(null, null);
    }

    public void deleteData(String key, String value) {
        database.delete(key, value);
    }

    public void deleteAllData() {
        database.deleteAll();
    }

    private List<SodiumData> createDataList(List<String[]> strList) {
        List<SodiumData> sodiumList = new ArrayList<>();
        for (String[] array : strList) {
            SodiumData data = new SodiumData(array[1], array[2], array[3]);
            sodiumList.add(data);
        }
        return sodiumList;
    }

    public List<SodiumData> getDataFromDate(String value) {
        return createDataList(getData("date", value));
    }

    public List<SodiumData> getDataFromPeriod(String start, String end) {
        return createDataList(database.readBetween("date", start, end));
    }
}
