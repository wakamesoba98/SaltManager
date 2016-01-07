package net.wakamesoba98.saltmanager.preferences.key;

public enum EnumPrefs implements Prefs {

    // General
    SALT_THRESHOLD (EnumValueType.INTEGER, "pref_salt_threshold", 6),
    ;

    private final EnumValueType type;
    private final String key;
    private final Object defaultValue;

    EnumPrefs(EnumValueType type, String key, Object defaultValue) {
        this.type = type;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return this.key;
    }

    public EnumValueType getType() {
        return this.type;
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }
}
