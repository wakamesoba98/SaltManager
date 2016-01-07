package net.wakamesoba98.saltmanager.database;

import net.wakamesoba98.saltmanager.util.SodiumConverter;

public class SodiumData {
    private int id;
    private String date;
    private String food;
    private int sodium;
    private double salt;

    public SodiumData(int id, String date, String food, int sodium) {
        this.id = id;
        this.date = date;
        this.food = food;
        this.sodium = sodium;
        this.salt = SodiumConverter.toSalt(sodium);
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getFood() {
        return food;
    }

    public int getSodium() {
        return sodium;
    }

    public double getSalt() {
        return salt;
    }
}
