package net.wakamesoba98.saltmanager.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import net.wakamesoba98.saltmanager.R;
import net.wakamesoba98.saltmanager.database.DatabaseManager;
import net.wakamesoba98.saltmanager.database.SodiumData;
import net.wakamesoba98.saltmanager.database.SodiumDatabase;
import net.wakamesoba98.saltmanager.view.activity.MainActivity;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        loadData(activity.getCalendar());
    }

    public void loadData(Calendar calendar) {
        final List<Map<String, String>> list = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(
                getActivity(),
                list,
                android.R.layout.simple_list_item_2,
                new String[] {"main", "sub"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );

        DateFormat sdf = new SimpleDateFormat(DatabaseManager.DATE_FORMAT, Locale.getDefault());

        SodiumDatabase database = new SodiumDatabase(getActivity());
        database.openDatabase();
        List<SodiumData> sodiumList = database.getDataFromDate(sdf.format(calendar.getTime()));
        database.closeDatabase();

        DecimalFormat df = new DecimalFormat("0.00");
        double total = 0;

        for(SodiumData sodium : sodiumList) {
            Map<String, String> map = new HashMap<>();
            map.put("main", df.format(sodium.getSalt()) + " g");
            map.put("sub", sodium.getFood());
            list.add(map);

            total += sodium.getSalt();
        }

        ListView listView = (ListView) getView().findViewById(R.id.listViewData);
        listView.setAdapter(adapter);

        TextView textView = (TextView) getActivity().findViewById(R.id.textViewTotalSalt);
        textView.setText(df.format(total) + " g");
    }
}
