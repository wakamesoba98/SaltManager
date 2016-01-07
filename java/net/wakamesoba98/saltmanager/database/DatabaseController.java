package net.wakamesoba98.saltmanager.database;

import java.util.List;

public interface DatabaseController {
    String getName();

    void openDatabase();

    void closeDatabase();

    int getCount();

    void insert(String[] data);

    void update(int id, String[] data);

    List<String[]> getData(String key, String value);

    List<String[]> getAllData();

    void delete(int id);

    void deleteAllData();
}
