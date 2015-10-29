package net.wakamesoba98.saltmanager.database;

import java.util.List;

public interface DatabaseController {
    String getName();

    void openDatabase();

    void closeDatabase();

    int getCount();

    void insert(String[] data);

    void update(String[] data);

    List<String[]> getData(String key, String value);

    List<String[]> getAllData();

    void deleteData(String key, String value);

    void deleteAllData();
}
