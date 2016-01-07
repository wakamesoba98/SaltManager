package net.wakamesoba98.saltmanager.preferences.key;

public interface Prefs {
    String getKey();
    EnumValueType getType();
    Object getDefaultValue();
}
