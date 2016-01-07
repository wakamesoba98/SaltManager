package net.wakamesoba98.saltmanager.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import net.wakamesoba98.saltmanager.view.fragment.PrefsFragment;

public class PrefsActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }
}
