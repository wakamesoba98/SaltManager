package net.wakamesoba98.saltmanager.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import net.wakamesoba98.saltmanager.preferences.key.EnumValueType;
import net.wakamesoba98.saltmanager.preferences.key.Prefs;

public class PreferenceUtil {

    public static final String PREF_NAME = "saltmanager";
    private SharedPreferences mPreferences;

    public PreferenceUtil(Context context) {
        mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void putPreference(Prefs pref, Object value) {
        SharedPreferences.Editor editor = mPreferences.edit();

        EnumValueType type = pref.getType();

        switch (type){
            case STRING:
                editor.putString(pref.getKey(), (String) value);
                break;

            case INTEGER: case LONG:
                editor.putString(pref.getKey(), String.valueOf(value));
                break;

            case BOOLEAN:
                editor.putBoolean(pref.getKey(), (Boolean) value);
                break;
        }

        editor.apply();
    }

    public String getStringPreference(Prefs pref) {
        return mPreferences.getString(pref.getKey(), (String) pref.getDefaultValue());
    }

    public int getIntPreference(Prefs pref) {
        String intStr = mPreferences.getString(pref.getKey(), String.valueOf(pref.getDefaultValue()));
        if (intStr.matches("[0-9]+")) {
            return Integer.parseInt(intStr);
        } else {
            return (Integer) pref.getDefaultValue();
        }
    }

    public long getLongPreference(Prefs pref) {
        String longStr = mPreferences.getString(pref.getKey(), String.valueOf(pref.getDefaultValue()));
        if (longStr.matches("[0-9]+")) {
            return Long.parseLong(longStr);
        } else {
            return (Long) pref.getDefaultValue();
        }
    }

    public boolean getBooleanPreference(Prefs pref) {
        return mPreferences.getBoolean(pref.getKey(), (Boolean) pref.getDefaultValue());
    }
}
