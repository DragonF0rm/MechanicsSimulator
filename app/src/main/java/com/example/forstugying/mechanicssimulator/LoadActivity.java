package com.example.forstugying.mechanicssimulator;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class LoadActivity extends AppCompatActivity {
    //Активность загрузок
    Background field;
    RelativeLayout layout;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        field = new Background(LoadActivity.this);
        LayoutInflater li = (LoadActivity.this).getLayoutInflater();
        layout = new RelativeLayout(LoadActivity.this);
        layout.addView(field);
        li.inflate(R.layout.activity_load, layout);
        setContentView(layout);
        final ListView list =(ListView) findViewById(R.id.list);
        TextView title = (TextView)findViewById(R.id.titleLoad);
        final LinearLayout menu =(LinearLayout) findViewById(R.id.menuLoadActivity);
        menu.setVisibility(View.INVISIBLE);



        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                list.getChildAt(position).setBackgroundColor(Color.argb(0, 0, 0, 0));
                position = pos;
                list.getChildAt(position).setBackgroundColor(Color.argb(150, 0, 39, 255));
                menu.setVisibility(View.VISIBLE);
            }
        });

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            int p = getResources().getConfiguration().screenWidthDp / 17;
            title.setTextSize(2 * p);
            title.setTextColor(Color.BLUE);
        }
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            int p = getResources().getConfiguration().screenHeightDp / 17;
            title.setTextSize(2 * p);
            title.setTextColor(Color.BLUE);
        }
        final ArrayList<String> listStrings = new ArrayList<String>();
        final ArrayList<Integer> listIDs = new ArrayList<Integer>();
        for(int i = 0;i<MainActivity.DB.getSize();i++){
            listStrings.add(MainActivity.DB.getName(i+1)+" "+RedactionField.TimeFormat(MainActivity.DB.getTime(i+1)));
            listIDs.add(MainActivity.DB.getID(i + 1));
        }

        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.menuLoadLoadActivity: {
                        MainActivity.DB.load(position+1,LoadActivity.this);
                        menu.setVisibility(View.INVISIBLE);
                        break;
                    }
                    case R.id.menuDeleteLoadActivity:{
                        MainActivity.DB.deleteSave(listIDs.get(position));
                        listIDs.remove(position);
                        listIDs.trimToSize();
                        listStrings.remove(position);
                        listStrings.trimToSize();
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoadActivity.this,R.layout.list_element, listStrings);
                        // присваиваем адаптер списку
                        list.setAdapter(adapter);
                        menu.setVisibility(View.INVISIBLE);
                        break;
                    }
                    case R.id.menuCancelLoadActivity:{
                        list.getChildAt(position).setBackgroundColor(Color.argb(0,0,0,0));
                        menu.setVisibility(View.INVISIBLE);
                        break;
                    }
                }
            }
        };

        Button load = (Button)findViewById(R.id.menuLoadLoadActivity);
        load.setBackgroundColor(Color.BLUE);
        load.setTextColor(Color.WHITE);
        load.setOnClickListener(l);

        Button delete = (Button)findViewById(R.id.menuDeleteLoadActivity);
        delete.setBackgroundColor(Color.BLUE);
        delete.setTextColor(Color.WHITE);
        delete.setOnClickListener(l);

        Button cancel = (Button)findViewById(R.id.menuCancelLoadActivity);
        cancel.setBackgroundColor(Color.BLUE);
        cancel.setTextColor(Color.WHITE);
        cancel.setOnClickListener(l);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_element, listStrings);
        // присваиваем адаптер списку
        list.setAdapter(adapter);

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

    @Override
    public void onBackPressed() {
        //Метод, вызывающийся при нажатии клавиши Back
        Intent i = new Intent(LoadActivity.this,MainActivity.class);
        startActivity(i);
        super.onBackPressed();
    }


}
