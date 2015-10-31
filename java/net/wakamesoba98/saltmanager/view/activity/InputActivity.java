package net.wakamesoba98.saltmanager.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import net.wakamesoba98.saltmanager.R;
import net.wakamesoba98.saltmanager.database.DatabaseManager;
import net.wakamesoba98.saltmanager.database.SodiumDatabase;
import net.wakamesoba98.saltmanager.util.SodiumConverter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class InputActivity extends AppCompatActivity {

    private EditText textSodium;
    private EditText textSalt;
    private DecimalFormat df;
    private DateFormat sdf;
    private FocusHolder focusHolder;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        textSodium = (EditText) findViewById(R.id.editTextSodium);
        textSalt = (EditText) findViewById(R.id.editTextSalt);
        df = new DecimalFormat("0.00");

        textSalt.addTextChangedListener(new SaltTextWatcher());
        textSalt.setOnFocusChangeListener(new SaltTextFocusListener());
        textSodium.addTextChangedListener(new SodiumTextWatcher());
        textSodium.setOnFocusChangeListener(new SodiumTextFocusListener());

        Intent intent = getIntent();
        if (intent != null) {
            String date = intent.getStringExtra("date");
            TextView textDate = (TextView) findViewById(R.id.textViewDate);
            textDate.setText(date);

            sdf = new SimpleDateFormat(DatabaseManager.DATE_FORMAT, Locale.getDefault());
            calendar = Calendar.getInstance();
            try {
                calendar.setTime(sdf.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_input, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                putData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void calcSodium(String text) {
        try {
            double salt = Double.parseDouble(text);
            int sodium = SodiumConverter.toSodium(salt);
            textSodium.setText(String.valueOf(sodium));
        } catch (NumberFormatException e) {

        }
    }

    private void calcSalt(String text) {
        try {
            int sodium = Integer.parseInt(text);
            double salt = SodiumConverter.toSalt(sodium);
            textSalt.setText(df.format(salt));
        } catch (NumberFormatException e) {

        }
    }

    public void putData() {
        EditText textFood = (EditText) findViewById(R.id.editTextFood);

        try {
            int sodium = Integer.parseInt(textSodium.getText().toString());
            String food = textFood.getText().toString();
            String date = sdf.format(calendar.getTime());

            SodiumDatabase database = new SodiumDatabase(this);
            database.openDatabase();
            database.insert(new String[]{date, food, String.valueOf(sodium)});
            database.closeDatabase();

            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.toast_error, Toast.LENGTH_SHORT).show();
        }
    }

    private class SodiumTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (focusHolder == FocusHolder.TEXT_SODIUM) {
                calcSalt(s.toString());
            }
        }
    }

    private class SaltTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (focusHolder == FocusHolder.TEXT_SALT) {
                calcSodium(s.toString());
            }
        }
    }

    private class SodiumTextFocusListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                focusHolder = FocusHolder.TEXT_SODIUM;
            }
        }
    }

    private class SaltTextFocusListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                focusHolder = FocusHolder.TEXT_SALT;
            }
        }
    }

    private enum FocusHolder {
        TEXT_SODIUM,
        TEXT_SALT,
    }
}
