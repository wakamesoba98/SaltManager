package net.wakamesoba98.saltmanager.view.fragment;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import net.wakamesoba98.saltmanager.R;
import net.wakamesoba98.saltmanager.preferences.PreferenceUtil;

public class PrefsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(PreferenceUtil.PREF_NAME);
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);

        addPreferencesFromResource(R.xml.pref);
        showVersion();
    }

    private void showVersion() {
        // バージョン情報を表示
        Preference prefAbout = findPreference("pref_about");
        PackageManager pm = getActivity().getPackageManager();
        String packageName = getActivity().getPackageName();
        String appName = getString(R.string.app_name);
        String versionName = "";
        try {
            if (pm != null) {
                PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                if (info != null) {
                    versionName = info.versionName;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (prefAbout != null){
            prefAbout.setTitle(appName + " ver." + versionName);
        }
    }
}
