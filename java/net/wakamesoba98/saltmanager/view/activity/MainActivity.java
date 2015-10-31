package net.wakamesoba98.saltmanager.view.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import net.wakamesoba98.saltmanager.R;
import net.wakamesoba98.saltmanager.database.DatabaseManager;
import net.wakamesoba98.saltmanager.view.fragment.MainFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static final int REQUEST_ADD = 1;
    private MainFragment fragment;
    private Calendar calendar;
    private DateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment = new MainFragment();
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        sdf = new SimpleDateFormat(DatabaseManager.DATE_FORMAT, Locale.getDefault());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                intent.putExtra("date", sdf.format(calendar.getTime()));
                startActivityForResult(intent, REQUEST_ADD);
            }
        });

        setActionBarTitle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ADD:
                fragment.loadData(calendar);
                break;
        }
    }

    public void setActionBarTitle() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(sdf.format(calendar.getTime()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;

            case R.id.action_chart:
                Intent intent = new Intent(this, ChartActivity.class);
                Calendar now = Calendar.getInstance();
                Calendar twoWeekAgo = Calendar.getInstance();
                twoWeekAgo.add(Calendar.WEEK_OF_MONTH, -1);
                intent.putExtra("start", sdf.format(twoWeekAgo.getTime()));
                intent.putExtra("end", sdf.format(now.getTime()));
                startActivity(intent);
                break;

            case R.id.action_pick_date:
                showDatePicker();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
        setActionBarTitle();
        fragment.loadData(calendar);
    }

    public Calendar getCalendar() {
        return calendar;
    }
}
