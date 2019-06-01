package com.example.forstugying.mechanicssimulator;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ExpRedactorActivity extends AppCompatActivity{
    //Активность основного редактора эксперементов

    final private String TAG = "ExpRedactorActivity";//Поле класса, применяемое для логирования
    RedactionField field;//Поле класса, предусмотренное для визуальной составляющей активности
    static Constructor Experiment;//Поле класса, содержащее вес эксперимент
    static RelativeLayout layout;
    static EditText[] layoutEditTexts = new EditText[2];
    static TextView layoutTextView;
    static Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        i = getIntent();//Переменная, получающая отправленный интент
        field = new RedactionField(this);//Создаём новую переменную класса RedactionField
        layout  = new RelativeLayout(ExpRedactorActivity.this);
        layout.addView(field);

        //Закрепляем альбомную ориентацию экрана
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(layout);//Выводим field на экран устройства

        //Проверяем, новый ди это эксперимент или загружаемый
        //От этого зависит, какие данные мы будем получать из intent
        if(i.getBooleanExtra("Loading",false)){

            try {
                Log.i("Serialization", i.getStringExtra("Serialization")+Database.suffix);
                loadExp(i.getStringExtra("Serialization") + Database.suffix);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Toast.makeText(ExpRedactorActivity.this, R.string.loadToast, Toast.LENGTH_SHORT).show();//Подсказка для пользователя

        }else {

            RedactionField.maxTime = i.getStringExtra("Time");

            Experiment = new Constructor(i.getStringExtra("Name"), Space.NOTHING, RedactionField.StringToInt(i.getStringExtra("Time")));

        }
    }

    public static void loadExp(String str) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(Database.file.getAbsoluteFile());
        ObjectInputStream oin = new ObjectInputStream(fis);
        Experiment = (Constructor) oin.readObject();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        field.resume();//Запускаем процесс рисования
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        field.pause();//Приостанавливаем процесс рисования
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(MainActivity.DB.shouldSave()) {
            try {
                MainActivity.DB.save(ExpRedactorActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void addEditText(Context con, int id,float X, float Y,int maxWidth,int maxHeight, String Text,float textSize,int color,/*int backgroundColor,*/boolean onlyNumbers) {
        //Метод добавления EditText на layout
        layoutEditTexts[id] = new EditText(con);
        if(id<layoutEditTexts.length) {
            layoutEditTexts[id].setText(Text);
            layoutEditTexts[id].setTextSize(textSize);
            layoutEditTexts[id].setX(X);
            layoutEditTexts[id].setY(Y);
            layoutEditTexts[id].setMaxWidth(maxWidth);
            layoutEditTexts[id].setMaxHeight(maxHeight);
            layoutEditTexts[id].setTextColor(color);
            layoutEditTexts[id].setMinWidth(maxWidth);
            layoutEditTexts[id].setMaxLines(1);
            if(onlyNumbers) {
                layoutEditTexts[id].setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            layout.addView(layoutEditTexts[id]);
        }
    }
    public static void deleteEditText(int id){
        //Метод удаления EditText с layout
        layout.removeView(layoutEditTexts[id]);
        layoutEditTexts[id] = null;
    }
    public static String getTextFromLayout(int id){
        //Метод получения текста из EditText
        return layoutEditTexts[id].getText().toString();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void addTextView(Context con,String text,float X, float Y,int maxWidth,int maxHeight,float textSize){
        //Метод добавления TextView на layout
        layoutTextView = new TextView(con);
        layoutTextView.setX(X);
        layoutTextView.setY(Y);
        layoutTextView.setMaxWidth(maxWidth);
        layoutTextView.setMaxHeight(maxHeight);
        layoutTextView.setTextSize(textSize);
        layoutTextView.setText(text);
        layoutTextView.setTextColor(Color.BLACK);
        layout.addView(layoutTextView);
    }

    public static void deleteTextView(){
        //Метод удаления TextView с layout
        layout.removeView(layoutTextView);
    }

    @Override
    public void onBackPressed() {
        //Метод, вызывающийся при нажатии клавиши Back
        Intent i = new Intent(ExpRedactorActivity.this,MainActivity.class);
        startActivity(i);
        super.onBackPressed();
    }
}