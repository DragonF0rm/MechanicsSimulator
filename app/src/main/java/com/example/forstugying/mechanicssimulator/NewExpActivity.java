package com.example.forstugying.mechanicssimulator;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewExpActivity extends AppCompatActivity {
final private String TAG ="NewExpActivity";
    Background field;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        field = new Background(NewExpActivity.this);
        LayoutInflater li = (NewExpActivity.this).getLayoutInflater();
        layout = new RelativeLayout(NewExpActivity.this);
        layout.addView(field);
        li.inflate(R.layout.activity_new_exp, layout);
        setContentView(layout);
        final TextView title;
        final Button button;
        final EditText time;
        final EditText name;
        final TextView error;
        button = (Button)findViewById(R.id.NewExpButton);
        time = (EditText)findViewById(R.id.setTimeNewExp);
        name = (EditText)findViewById(R.id.setNameNewExp);
        title = (TextView)findViewById(R.id.titleNewExp);
        error = (TextView)findViewById(R.id.NewExpErrorTextView);
        int p = getResources().getConfiguration().screenHeightDp / 17;
        title.setTextSize(2*p);
        title.setTextColor(Color.BLUE);
        button.setTextColor(Color.WHITE);
        button.setTextSize(p);
        time.setTextSize(p);
        name.setTextSize(p);
        button.setPadding(p / 2, 0, p / 2, 0);
        error.setTextColor(Color.RED);
        error.setTextSize(p);

        View.OnClickListener l = new View.OnClickListener() {//создаём обработчик
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                    case R.id.NewExpButton: {
                        Log.i(TAG, "button is onClick");
                        error.setText("");
                        //Обработка корректности заполнения полей
                        if(RedactionField.StringToInt(time.getText().toString())!=0&&time.getText().toString()!=""&&name.getText().toString().length()>=1) {
                            Intent i = new Intent(NewExpActivity.this, ExpRedactorActivity.class);
                            Log.i(TAG, "next activity is starting");
                            i.putExtra("Loading",false);
                            i.putExtra("Time", time.getText().toString());//Отправляем время считанное из поля
                            i.putExtra("Name", name.getText().toString());
                            startActivity(i);//Обращаемся к активности
                        }
                        else{
                            if(RedactionField.StringToInt(time.getText().toString())!=0||time.getText().toString()!=""){
                                error.setText(getResources().getString(R.string.error1));
                            }
                            if(name.getText().toString().length()<1){
                                error.setText(getResources().getString(R.string.error2));
                            }
                        }
                        break;
                    }
            }
            }
        };
        button.setOnClickListener(l);
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