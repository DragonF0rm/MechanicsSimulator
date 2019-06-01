package com.example.forstugying.mechanicssimulator;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    //Активность настроек
    Background field;
    RelativeLayout layout;
    TextView logo;
    CheckBox shouldSave;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        field = new Background(SettingsActivity.this);
        LayoutInflater li = (SettingsActivity.this).getLayoutInflater();
        layout = new RelativeLayout(SettingsActivity.this);
        layout.addView(field);
        li.inflate(R.layout.activity_settings, layout);
        setContentView(layout);

        int p = getResources().getConfiguration().screenHeightDp/17;
        logo = (TextView)findViewById(R.id.testTextView);
        logo.setTextSize(2 * p);
        logo.setTextColor(Color.BLUE);

        shouldSave = (CheckBox)findViewById(R.id.settingsActivityCheckBox);
        shouldSave.setTextSize(p);
        shouldSave.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        shouldSave.setChecked(MainActivity.DB.shouldSave());//Включаем в зависимости от настроек

        saveButton = (Button)findViewById(R.id.saveActivityButton);
        saveButton.setTextSize(p);
        saveButton.setTextColor(Color.WHITE);
        saveButton.setBackgroundColor(Color.BLUE);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.DB.changeShouldSave(shouldSave.isChecked());
                Toast.makeText(SettingsActivity.this,getResources().getString(R.string.SettingsToast),Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        field.resume();//Запускаем процесс рисования
    }

    @Override
    protected void onPause() {
        super.onPause();
        field.pause();//Приостанавливаем процесс рисования
    }
}