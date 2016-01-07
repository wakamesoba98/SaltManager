package net.wakamesoba98.saltmanager.view.activity;

import android.content.Intent;
import android.os.Bundle;
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
import net.wakamesoba98.saltmanager.preferences.PreferenceUtil;
import net.wakamesoba98.saltmanager.preferences.key.EnumPrefs;
import net.wakamesoba98.saltmanager.util.SodiumConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChartActivity extends AppCompatActivity {

    private int threshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        BarChart chart = (BarChart) findViewById(R.id.chart);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setStartAtZero(true);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawValuesForWholeStack(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setDescription("");

        PreferenceUtil prefs = new PreferenceUtil(this);
        threshold = prefs.getIntPreference(EnumPrefs.SALT_THRESHOLD);

        Intent intent = getIntent();
        if (intent != null) {
            String start = intent.getStringExtra("start");
            String end = intent.getStringExtra("end");
            entry(chart, start, end);
        }
    }

    private List<SodiumData> loadDatabase(String start, String end) {
        SodiumDatabase database = new SodiumDatabase(this);
        database.openDatabase();
        List<SodiumData> sodiumDataList = database.getDataFromPeriod(start, end);
        database.closeDatabase();
        return sodiumDataList;
    }

    private List<String> createDateList(String start, String end) {
        DateFormat sdf = new SimpleDateFormat(DatabaseManager.DATE_FORMAT, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(start));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (calendar == null) {
            return null;
        }

        List<String> dateList = new ArrayList<>();

        String dateStr = sdf.format(calendar.getTime());
        while (!dateStr.equals(end)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dateStr = sdf.format(calendar.getTime());
            dateList.add(dateStr);
        }

        return dateList;
    }

    private Map<String, Integer> createDateSodiumMap(List<SodiumData> sodiumDataList) {
        Map<String, Integer> dateSodiumMap = new HashMap<>();
        for (SodiumData sodiumData : sodiumDataList) {
            String date = sodiumData.getDate();
            int sodium = sodiumData.getSodium();
            Integer value = dateSodiumMap.get(date);
            if (value == null) {
                dateSodiumMap.put(date, sodium);
            } else {
                dateSodiumMap.put(date, sodium + value);
            }
        }
        return dateSodiumMap;
    }

    private List<BarDataSet> createBarDataSets(List<BarEntry> values) {
        BarDataSet barDataSet = new BarDataSet(values, "");
        barDataSet.setColors(new int[] {
                ContextCompat.getColor(this, (R.color.accent)),
                ContextCompat.getColor(this, (R.color.primary))
        });
        barDataSet.setStackLabels(new String[] {
                getResources().getString(R.string.label_stack_over),
                getResources().getString(R.string.label_stack_salt)
        });
        List<BarDataSet> sets = new ArrayList<>();
        sets.add(barDataSet);
        return sets;
    }

    private void startAnimation(final BarChart chart, final BarData data, BarData dummy) {
        chart.setData(dummy);
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

    private void entry(BarChart chart, String start, String end) {
        List<String> dateList = createDateList(start, end);
        if (dateList == null) {
            return;
        }

        List<SodiumData> sodiumDataList = loadDatabase(start, end);
        Map<String, Integer> dateSodiumMap = createDateSodiumMap(sodiumDataList);

        List<BarEntry> values = new ArrayList<>();
        List<BarEntry> dummyValues = new ArrayList<>();
        float max = 0;
        for (int i = 0; i < dateList.size(); i++) {
            Integer sodium = dateSodiumMap.get(dateList.get(i));
            if (sodium == null) {
                sodium = 0;
            }
            float salt = (float) SodiumConverter.toSalt(sodium);
            if (salt > threshold) {
                values.add(new BarEntry(new float[]{salt - threshold, threshold}, i));
            } else {
                values.add(new BarEntry(new float[]{0.0f, salt}, i));
            }
            dummyValues.add(new BarEntry(new float[]{0.0f, 0,0f}, i));
            if (max < salt) {
                max = salt;
            }
        }

        BarData data = new BarData(dateList, createBarDataSets(values));
        BarData dummyData = new BarData(dateList, createBarDataSets(dummyValues));

        chart.getAxisLeft().setAxisMaxValue(max + 1.0f);
        chart.getAxisLeft().setAxisMinValue(0f);
        startAnimation(chart, data, dummyData);
    }
}
