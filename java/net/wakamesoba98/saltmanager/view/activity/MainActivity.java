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
import android.widget.*;
import net.wakamesoba98.saltmanager.R;
import net.wakamesoba98.saltmanager.database.DatabaseManager;
import net.wakamesoba98.saltmanager.database.SodiumData;
import net.wakamesoba98.saltmanager.database.SodiumDatabase;
import net.wakamesoba98.saltmanager.dialog.ConfirmDialog;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static final int REQUEST_ADD = 1;
    private List<SodiumData> sodiumList;
    private Calendar calendar;
    private DateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        sdf = new SimpleDateFormat(DatabaseManager.DATE_FORMAT, Locale.getDefault());

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
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ADD:
                loadData();
                break;
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
                Intent intentPrefs = new Intent(this, PrefsActivity.class);
                startActivity(intentPrefs);
                break;

            case R.id.action_chart:
                Intent intentChart = new Intent(this, ChartActivity.class);
                Calendar now = Calendar.getInstance();
                Calendar twoWeekAgo = Calendar.getInstance();
                twoWeekAgo.add(Calendar.WEEK_OF_MONTH, -1);
                intentChart.putExtra("start", sdf.format(twoWeekAgo.getTime()));
                intentChart.putExtra("end", sdf.format(now.getTime()));
                startActivity(intentChart);
                break;

            case R.id.action_pick_date:
                showDatePicker();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
        setActionBarTitle();
        loadData();
    }

    public void setActionBarTitle() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(sdf.format(calendar.getTime()));
        }
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

    public void loadData() {
        List<Map<String, String>> list = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                list,
                android.R.layout.simple_list_item_2,
                new String[]{"main", "sub"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        DateFormat sdf = new SimpleDateFormat(DatabaseManager.DATE_FORMAT, Locale.getDefault());

        SodiumDatabase database = new SodiumDatabase(this);
        database.openDatabase();
        sodiumList = database.getDataFromDate(sdf.format(calendar.getTime()));
        database.closeDatabase();

        DecimalFormat df = new DecimalFormat("0.00");
        double total = 0;

        for (SodiumData sodium : sodiumList) {
            Map<String, String> map = new HashMap<>();
            map.put("main", df.format(sodium.getSalt()) + " g");
            map.put("sub", sodium.getFood());
            list.add(map);

            total += sodium.getSalt();
        }

        ListView listView = (ListView) findViewById(R.id.listViewData);
        listView.setAdapter(adapter);

        TextView textView = (TextView) findViewById(R.id.textViewTotalSalt);
        textView.setText(df.format(total) + " g");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                intent.putExtra("id", sodiumList.get(position).getId());
                startActivityForResult(intent, MainActivity.REQUEST_ADD);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                ConfirmDialog dialog = new ConfirmDialog() {
                    @Override
                    public void onPositiveButtonClick() {
                        SodiumDatabase db = new SodiumDatabase(MainActivity.this);
                        db.openDatabase();
                        db.delete(sodiumList.get(position).getId());
                        db.closeDatabase();

                        loadData();
                    }
                };
                dialog.build(MainActivity.this, R.string.dialog_title_confirm, R.string.dialog_delete_confirm);
                return true;
            }
        });
    }
}
