//Основная активность программы, с неё начинается выполнение приложения
package com.example.forstugying.mechanicssimulator;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    Background background;
    RelativeLayout layout;
    static Database DB;//База данных

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        background = new Background(MainActivity.this);
        layout = new RelativeLayout(MainActivity.this);
        layout.addView(background);
        LayoutInflater li = (MainActivity.this).getLayoutInflater();
        li.inflate(R.layout.activity_main,layout);

        setContentView(layout);

        DB = new Database(MainActivity.this);

        TextView logo1 = (TextView)findViewById(R.id.logoMainActivity1);
        TextView logo2 = (TextView)findViewById(R.id.logoMainActivity2);

        Button button1 = (Button)findViewById(R.id.button1MainActivity);
        Button button2 = (Button)findViewById(R.id.button2MainActivity);
        Button button3 = (Button)findViewById(R.id.button3MainActivity);
        Button button4 = (Button)findViewById(R.id.button4MainActivity);
        int p = getResources().getConfiguration().screenHeightDp/17;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            logo1.setTextColor(Color.BLUE);
            logo1.setPadding(0, 0, 0, 0);
            logo1.setTextSize(2 * p);
            logo2.setTextColor(Color.BLUE);
            logo2.setPadding(0, 0, 0, p / 2);
            logo2.setTextSize(2 * p);

            button1.setTextSize(p);
            button2.setTextSize(p);
            button3.setTextSize(p);
            button4.setTextSize(p);
            button1.setPadding(p / 4, p / 2, p / 4, p / 2);
            button2.setPadding(p / 4, p / 2, p / 4, p / 2);
            button3.setPadding(p / 4, p / 2, p / 4, p / 2);
            button4.setPadding(p / 4, p / 2, p / 4, p / 2);
            button1.setTextColor(Color.WHITE);
            button2.setTextColor(Color.WHITE);
            button3.setTextColor(Color.WHITE);
            button4.setTextColor(Color.WHITE);

            //Подключение кастомного шрифта
            Typeface keys = Typeface.createFromAsset(getAssets(),getString(R.string.digit_keyboard_font));
            logo1.setTypeface(keys);
            logo2.setTypeface(keys);
        }
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            logo1.setTextColor(Color.BLUE);
            logo1.setPadding(0, p, 0, p);
            logo1.setTextSize(3 * p);

            button1.setTextSize(p);
            button2.setTextSize(p);
            button3.setTextSize(p);
            button4.setTextSize(p);
            button1.setPadding(p / 4, p, p / 4, p);
            button2.setPadding(p / 4, p, p / 4, p);
            button3.setPadding(p / 4, p, p / 4, p);
            button4.setPadding(p / 4, p, p / 4, p);
            button1.setTextColor(Color.WHITE);
            button2.setTextColor(Color.WHITE);
            button3.setTextColor(Color.WHITE);
            button4.setTextColor(Color.WHITE);

            //Подключение кастомного шрифта
            Typeface keys = Typeface.createFromAsset(getAssets(),getString(R.string.digit_keyboard_font));
            logo1.setTypeface(keys);
        }

        View.OnClickListener l = new View.OnClickListener() {//создаём обработчик
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button1MainActivity: {
                    Log.i(TAG, "button is onClick");
                    Intent i = new Intent(MainActivity.this, NewExpActivity.class);
                    Log.i(TAG, "next activity is starting");
                    startActivity(i);//Обращаемся к активности
                    break;
                }
                case R.id.button2MainActivity: {
                    Intent i = new Intent(MainActivity.this, LoadActivity.class);
                    startActivity(i);
                    break;
                }
                case R.id.button3MainActivity:
                    Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(i);
                    break;
                case R.id.button4MainActivity: {
                    startGuide();
                    break;
                }
            }
        }
    };
        button1.setOnClickListener(l);
        button2.setOnClickListener(l);
        button3.setOnClickListener(l);
        button4.setOnClickListener(l);
    }
    public void startGuide(){
        //Метод старта обучения
        Toast.makeText(MainActivity.this, R.string.GuideToast, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        background.resume();//Запускаем процесс рисования
    }

    @Override
    protected void onPause() {
        super.onPause();
        background.pause();//Приостанавливаем процесс рисования
    }
}
