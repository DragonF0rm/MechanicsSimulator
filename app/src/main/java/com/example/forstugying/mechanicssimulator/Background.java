package com.example.forstugying.mechanicssimulator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Background extends SurfaceView implements Runnable {
    //Класс View элемента, отвечающего за отрисовку фона в активностях
    Canvas canvas;//Основная канва
    Thread t;//Поток отрисовки
    SurfaceHolder holder;//Основной холст
    boolean drawing;//Переменная, регулирующая старт и остановку отрисовки

    public Background(Context context,AttributeSet set){
        //Конструктор класса
        super(context);
        holder = getHolder();//Старт отрисовки
    }
    public Background(Context context){
        super(context);
        holder = getHolder();//Старт отрисовки
    }

    @Override
    public void run() {

        while(drawing) {//Начинаем отрисовку
            if (holder.getSurface().isValid()) {//Проверка доступности холста
                canvas = holder.lockCanvas();//Блокировка холста

                paint(canvas);//Процесс рисования

                holder.unlockCanvasAndPost(canvas);//Вывод на экран отрисованного изображния и разблокировка холста
            }
        }
    }

    public  void resume(){
        //Метод, вызывающийся для старта отрисовки
        drawing = true;
        t = new Thread(this);
        t.start();//Старт потока
    }

    public void pause(){
        //Метод, вызывающийся для остановки отрисовки
        drawing = false;
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void paint(final Canvas canvas){
        //Метод рисования
        Paint mainPaint = new Paint();
        mainPaint.setAntiAlias(true);
        canvas.drawColor(Color.WHITE);

        int size = 15;
        for (float i = getHeight();i >=0;i = i-size){
            //Рисование горизонтальных линий
            mainPaint.setColor(getResources().getColor(R.color.backgroundColor));
            canvas.drawLine(0,i,getWidth(),i,mainPaint);
        }

        for (float i = getWidth();i >=0;i = i-size){
            //Рисование вертикальных линий
            mainPaint.setColor(getResources().getColor(R.color.backgroundColor));
            if(i == getWidth()-((int)(0.13*(getWidth()/size)))*size){
                //Рисование полей
                mainPaint.setColor(getResources().getColor(R.color.colorAccent));
            }
            canvas.drawLine(i,0,i,getHeight(),mainPaint);
        }
    }
}
