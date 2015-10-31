package net.wakamesoba98.saltmanager.util;

public class SodiumConverter {
    public static int toSodium(double salt) {
        return (int) (salt / 2.54 * 1000);
    }

    public static double toSalt(int sodium) {
        return sodium * 2.54 / 1000;
    }
}
