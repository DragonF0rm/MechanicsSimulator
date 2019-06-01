package com.example.forstugying.mechanicssimulator;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static android.support.v4.app.ActivityCompat.startActivity;

public class Database {
    //Класс базы данных
    //Так как процесс загрузки экспериментов сейчас активно дорабатывается,
    //класс довольно часто меняется и может содержать нерациональный/ненужный код
    DBHelper dbHelper;
    static SQLiteDatabase db;
    static ContentValues cv = new ContentValues();
    Cursor c;
    static Context context;
    final static String suffix =".out";
    static File file;

    class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, "myDB", null, 1);
        }
        //Создание базы данны
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table saves("
                    + "id integer primary key autoincrement,"
                    + "name text,"
                    + "time long,"
                    + "serialization text"
                    + ");");

            c = db.query("saves",null, null, null, null, null, null);

            db.execSQL("create table settings("
                    + "id integer primary key autoincrement,"
                    + "shouldSave integer"
                    + ");");

            Cursor cursor = db.query("settings",null, null, null, null, null, null);
            if(!cursor.moveToFirst()){
                //Если в таблице настроек нет данных, выставляем настройки по-умолчанию
                cv.clear();
                cv.put("shouldSave", 1);
                db.insert("settings",null,cv);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public boolean shouldSave(){
        //Метод, возвращающий настроки
        Cursor cursor = db.query("settings",null, null, null, null, null, null);
        cursor.moveToLast();

        if(cursor.getInt(cursor.getColumnIndex("shouldSave"))==0){
            return false;
        }
        else{
            return true;
        }
    }

    public void changeShouldSave(boolean newValue){
        //Метод для изменения настроек
        db.delete("settings", "id = " + 1, null);
        cv.clear();
        if(newValue) {
            cv.put("shouldSave", 1);
        }
        else{
            cv.put("shouldSave", 0);
        }
        db.insert("settings",null,cv);
    }

    public Database(Context con) {
        //Конструктор класса
        context = con;
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public int getSize(){
        //Метод, возвращающий размер таблицы
        int n = 0;
        c = db.query("saves", null, null, null, null, null, null);
        if(c.moveToFirst()) {
            do {
                n++;
            } while (c.moveToNext());
        }
        return n;
    }

    public static void save(Context context) throws IOException {
        //Метод сохранения эксперимента
        String prefix = "temp";
        file = new File(context.getFilesDir(),prefix);
        FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(ExpRedactorActivity.Experiment);
        oos.flush();
        oos.close();
        cv.clear();
        cv.put("name", ExpRedactorActivity.Experiment.name);
        cv.put("time",ExpRedactorActivity.Experiment.time);

        cv.put("serialization", prefix);
        db.insert("saves", null, cv);
        Toast.makeText(context,R.string.saveToast,Toast.LENGTH_SHORT).show();//Подсказка для пользователя
    }
    public void load(int position,Context context){
        //Метод загрузки эксперимета
        c = db.query("saves",null, null, null, null, null, null);
        c.move(position);
        Intent i = new Intent(context,ExpRedactorActivity.class);
        i.putExtra("Loading",true);
        i.putExtra("Serialization",c.getString(c.getColumnIndex("serialization")));
        i.putExtra("Name",c.getString(c.getColumnIndex("name")));
        i.putExtra("Time",c.getLong(c.getColumnIndex("time")));
        startActivity((Activity) context,i,null);
    }
    public void deleteSave(int id){
        //Метод удаления сохранения
        c = db.query("saves",null, null, null, null, null, null);
        db.delete("saves", "id = " + id, null);
    }

    public String getName(int position){
        //Метод, возвращающий имя эксперимента из сохранения
        c = db.query("saves",null, null, null, null, null, null);
        c.move(position);
        return c.getString(c.getColumnIndex("name"));
    }

    public long getTime(int position) {
        //Метод, возвращающий время эксперимента из сохранения
        c = db.query("saves",null, null, null, null, null, null);
        c.move(position);
        return c.getLong(c.getColumnIndex("time"));
    }

    public int getID(int position) {
        //Метод, возвращающий ID эксперимента из сохранения
        c = db.query("saves",null, null, null, null, null, null);
        c.move(position);
        return c.getInt(c.getColumnIndex("id"));
    }
}
