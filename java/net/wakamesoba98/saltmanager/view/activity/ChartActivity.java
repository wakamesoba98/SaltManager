package net.wakamesoba98.saltmanager.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import net.wakamesoba98.saltmanager.R;
import net.wakamesoba98.saltmanager.database.DatabaseManager;
import net.wakamesoba98.saltmanager.database.SodiumData;
import net.wakamesoba98.saltmanager.database.SodiumDatabase;
import net.wakamesoba98.saltmanager.handler.UiHandler;
import net.wakamesoba98.saltmanager.util.SodiumConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Handler;

public class ChartActivity extends AppCompatActivity {

    private BarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        chart = (BarChart) findViewById(R.id.chart);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setStartAtZero(true);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setDescription("");

        Intent intent = getIntent();
        if (intent != null) {
            String start = intent.getStringExtra("start");
            String end = intent.getStringExtra("end");
            entry(start, end);
        }
    }

    private void entry(String start, String end) {
        SodiumDatabase database = new SodiumDatabase(this);
        database.openDatabase();
        List<SodiumData> sodiumDataList = database.getDataFromPeriod(start, end);
        database.closeDatabase();

        DateFormat sdf = new SimpleDateFormat(DatabaseManager.DATE_FORMAT, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(start));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (calendar == null) {
            return;
        }

        List<String> dateList = new ArrayList<>();

        String dateStr = sdf.format(calendar.getTime());
        while (!dateStr.equals(end)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dateStr = sdf.format(calendar.getTime());
            dateList.add(dateStr);
        }

        Map<String, Integer> map = new HashMap<>();
        for (SodiumData sodiumData : sodiumDataList) {
            String date = sodiumData.getDate();
            int sodium = sodiumData.getSodium();
            Integer value = map.get(date);
            if (value == null) {
                map.put(date, sodium);
            } else {
                map.put(date, sodium + value);
            }
        }

        List<BarEntry> values = new ArrayList<>();
        List<BarEntry> dummyValues = new ArrayList<>();
        float max = 0;
        for (int i = 0; i < dateList.size(); i++) {
            Integer value = map.get(dateList.get(i));
            if (value == null) {
                value = 0;
            }
            float salt = (float) SodiumConverter.toSalt(value);
            values.add(new BarEntry(salt, i));
            dummyValues.add(new BarEntry(0.0f, i));
            if (max < salt) {
                max = salt;
            }
        }

        BarDataSet barDataSet = new BarDataSet(values, "塩分量");
        BarDataSet dummyBarDataSet = new BarDataSet(dummyValues, "塩分量");
        barDataSet.setColor(ContextCompat.getColor(this, (R.color.primary)));

        List<BarDataSet> sets = new ArrayList<>();
        List<BarDataSet> dummySets = new ArrayList<>();
        sets.add(barDataSet);
        dummySets.add(dummyBarDataSet);

        final BarData data = new BarData(dateList, sets);
        BarData dummyData = new BarData(dateList, dummySets);
        chart.getAxisLeft().setAxisMaxValue(max + 1.0f);
        chart.getAxisLeft().setAxisMinValue(0f);
        chart.setData(dummyData);
        chart.invalidate();

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                }catch(InterruptedException e){
                }
                new UiHandler() {
                    @Override
                    public void run() {
                        chart.animateY(1000, Easing.EasingOption.EaseOutQuad);
                        chart.setData(data);
                        chart.invalidate();
                    }
                }.post();
            }
        }).start();
    }
}
