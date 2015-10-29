package net.wakamesoba98.saltmanager.database;

public class SodiumData {
    private String date;
    private String food;
    private int sodium;
    private double salt;

    public SodiumData(String date, String food, String sodium) {
        int sodiumValue = Integer.parseInt(sodium);
        this.date = date;
        this.food = food;
        this.sodium = sodiumValue;
        this.salt = toSalt(sodiumValue);
    }

    private double toSalt(int sodium) {
        return sodium * 2.54 / 1000;
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
