package com.example.forstugying.mechanicssimulator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.v4.app.ActivityCompat.startActivity;

//Этот класс описывает отрисовку графики в активности основного редактора экспериментов
public class RedactionField extends SurfaceView implements Runnable{

    //Переменные для класса
    Canvas canvas;
    Thread t;//Поток отрисовки
    SurfaceHolder holder;//Основной холст
    final private String TAG = "RedactionField";
    double x=0;//Длина полосы загрузки (изменяемая)
    boolean drawing;//Переменная, регулирующая старт и остановку отрисовки
    Timer timer;//Таймер, выполняющий отсчёт времени и смену кадров
    TimerTask timerTask;//Основное задание таймера
    static long time = 0;//Переменная, содержащая ТЕКУЩЕЕ ВРЕМЯ В СЕКУНДАХ!
    static String maxTime = "0";//Переменная, содержащая полное время эксперимента
    boolean count = false;//Регулирует процесс счёта
    Paint textPaint;//Инструмент для рисования строк
    Paint mainPaint;//Основной инструмент для рисования
    boolean onLoad = false;//Пременная, регулирующая старт и остановку подгрузки новых кадров
    int N = 18;//Число готовых для использования объектов
    int[][]matrixOfObjects;
    //Кнопки
    Rect dst;//Прямоугольник с координатами отрисовки основных кнопок управления (pause, play, restart)
    Rect src;//Прямоугольник с областью отрисовки кнопок управления
    Rect dst1;//Прямоугольник с координатами отрисовки кнопки terminate
    Bitmap menu;//Битмап кнопки меню
    Bitmap darkMenu;//Битмап нажатой кнопки меню
    boolean menuOnClick = false;//Переменная, отражающая состояние меню (открыто/закрыто)
    Bitmap objectListButton;
    boolean objectMenuClose = true;//Переменная, отражающая состояние меню объектов (свёрнуто/развёрнуто)
    Path trapezoid;
    Paint buttonTextPaint;
    Bitmap objectMenuLeftButton;
    Bitmap objectMenuRightButton;
    boolean objectMenuLeftButtonOnClick = false;
    boolean objectMenuRightButtonOnClick = false;
    Rect objectMenuRect;
    float scroll = 0;
    boolean scrolling = false;
    Bitmap objectMenuScrollBar;
    Bitmap objectMenuScroller;
    float s;
    float k =1;
    Bitmap objectMenuList;
    Bitmap[] objectList;
    Bitmap mainMenu;
    //Взаимодействие с объектами
    boolean dragging_0 = false;
    boolean dragging_1 = false;
    boolean dragging_2 = false;
    boolean dragging_3 = false;
    boolean dragging_4 = false;
    boolean zeroPointExists = false;
    float X0;
    float Y0;
    float X1;
    float Y1;
    Path obj_2;
    //Диалоги
    boolean AnyDialogOpen = false;//Остановит кликабельность фоновых объектов при открытии диалога
    static boolean BodyDialogOpen = false;
    boolean SaveDialogOpen = false;
    boolean LoadDialogOpen = false;
    boolean ExceptionDialogOpen = false;
    boolean OK_ButtonOnClick = false;
    boolean Cancel_ButtonOnClick = false;
    float dialogTextSize;
    float w = 0;

    public RedactionField(Context context){
        //Метод конструктора
        super(context);//Вызов конструктора родительского класса
        Log.i(TAG, "RedactionField was created");
        holder = getHolder();
    }
    @Override
    public void run() {
        //Метод, вызываемый при старте потока

        Log.i(TAG, "Thread is running");
        count();//Даём добро на начало отсчёта
        /*Примечание: при вызове предыдущего метода отсчёт НЕ начинается, так как переменная count по-умолчанию false*/
        while(drawing){//Начинаем отрисовку
            if(holder.getSurface().isValid()){//Проверка доступности холста
                canvas = holder.lockCanvas();//Блокировка холста
                dialogTextSize = (getWidth() / 2 - 20) / 21;
                //Определение матрицы, позволяющей отлавливать касания объеков
                matrixOfObjects = new int[getWidth()][getHeight()];
                for(int i = 0;i<matrixOfObjects.length;i++){
                    for(int j = 0;j<matrixOfObjects[i].length;j++){
                        matrixOfObjects[i][j] = 0;
                    }
                }
                for(int i = 0;i<matrixOfObjects.length;i = i+matrixOfObjects.length-1){
                    for(int j = 0;j<matrixOfObjects[i].length;j++){
                        matrixOfObjects[i][j] = 2;
                    }
                }
                paint(canvas);//Процесс рисования

                holder.unlockCanvasAndPost(canvas);//Вывод на экран отрисованного изображния и разблокировка холста
            }
            else{
                Log.wtf(TAG, "Holder is not valid");
            }
        }
    }
    private void paint(final Canvas canvas){
        //Метод, составляющий изображения для отрисовки
        mainPaint = new Paint();//Инструмент для рисования основных объектов
        mainPaint.setColor(Color.BLUE);
        mainPaint.setAntiAlias(true);

        drawWorkPlace();
        Objects();//Полключаем поддержку объектов

        Rect header = new Rect(0, 0, getWidth(), getHeight() / 10);//Прямоугольник шапки
        mainPaint.setColor(Color.BLUE);
        canvas.drawRect(header, mainPaint);//Рисуем шапку
        Rect loading_white = new Rect(getWidth() / 4, getHeight() / 10 / 4, getWidth() - getWidth() / 4, getHeight() / 10 - getHeight() / 10 / 4);//Прямоугольник фона загрузки
        mainPaint.setColor(Color.WHITE);
        canvas.drawRect(loading_white, mainPaint);//Рисуем фон для полосы загрузки

        player(canvas);//Запускаем плеер (кнопки, подгрузка кадров)
        Dialogs();//Подключаем поддержку диалогов



    }
    private void drawWorkPlace(){
        int workPlaceHeight = getHeight() - getHeight()/10;
        int workPlaceWidth = getWidth();
        Bitmap workPlace = Bitmap.createBitmap(workPlaceWidth, workPlaceHeight, Bitmap.Config.ARGB_8888);
        Canvas workPlaceCanvas = new Canvas(workPlace);
        workPlaceCanvas.drawColor(Color.WHITE/*ExpRedactorActivity.Experiment.space.color*/);
        canvas.drawBitmap(workPlace, 0, getHeight() / 10, mainPaint);
    }
    private void load(Canvas canvas){
        //Метод для перемещения полоски загрузки
        if(x<getWidth() / 2&&time<StringToInt(maxTime)&&onLoad){
            count = true;//Начинаем отсчёт
            Rect loading_color = new Rect(getWidth() / 4, getHeight() / 10 / 4, getWidth() / 4 + (int)x, getHeight() / 10 - getHeight() / 10 / 4);//Прямоугольник полосы загрузки
            mainPaint.setColor(Color.GREEN);
            canvas.drawRect(loading_color, mainPaint);//Рисуем ролосу загрузки
            textPaint = new Paint();//Создаём инструмент для рисования строк
            textPaint.setAntiAlias(true);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(getHeight() / 10 - getHeight() / 10 / 4);
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(TimeFormat(time)+"/"+TimeFormat(maxTime),getWidth()*7/8,getHeight() / 10 - getHeight() / 10 / 4, textPaint);//Выводим счётчик времени
        }
        else {
            onLoad=false;
            count = false;//Останавливаем счёт
            if(time == StringToInt(maxTime)) {
                x = getWidth() / 2;//Фиксируем цветную часть полосы загрузки в её конечном положении
            }
            Rect loading_color = new Rect(getWidth() / 4, getHeight() / 10 / 4, getWidth() / 4 + (int)x, getHeight() / 10 - getHeight() / 10 / 4);//Прямоугольник полосы загрузки
            mainPaint.setColor(Color.GREEN);
            canvas.drawRect(loading_color, mainPaint);//Рисуем ролосу загрузки
            textPaint = new Paint();//Создаём инструмент для рисования строк
            textPaint.setAntiAlias(true);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(getHeight() / 10 - getHeight() / 10 / 4);
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(TimeFormat(time)+"/"+TimeFormat(maxTime),getWidth()*7/8,getHeight() / 10 - getHeight() / 10 / 4, textPaint);//Выводим счётчик времени
        }
    }
    public void pause(){
        //Метод для приостановки рисования
        Log.i(TAG,"Thread is pausing");
        drawing = false;
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume(){
        //Метод для продолжения рисования
        Log.i(TAG,"Thread is resuming");
        drawing = true;
        t = new Thread(this);
        t.start();//Старт потока
    }
    private void count(){
        //Метод, считающий время
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (count) {
                    x = x + (double)getWidth() / (double)(2 * StringToInt(maxTime));//Устанавливаем шаг для полосы загрузки
                    time = time + 1;//Ведём отсчёт времени
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }
    public static int StringToInt(String s){
        //Метод, переводящий числовую строку в целое число
        int N=0;
        for(int i = 0;i<s.length();i++){
            switch (s.charAt(i)){
                case '0':{
                    N=N*10;
                    break;
                }
                case '1':{
                    N=N*10+1;
                    break;
                }
                case '2':{
                    N=N*10+2;
                    break;
                }
                case '3':{
                    N=N*10+3;
                    break;
                }
                case '4':{
                    N=N*10+4;
                    break;
                }
                case '5':{
                    N=N*10+5;
                    break;
                }
                case '6':{
                    N=N*10+6;
                    break;
                }
                case '7':{
                    N=N*10+7;
                    break;
                }
                case '8':{
                    N=N*10+8;
                    break;
                }
                case '9':{
                    N=N*10+9;
                    break;
                }
                default:{
                    Log.e("RedactionField" ,"Argument string have wrong format");
                }
            }
        }
        return N;
    }

    public static double StringToDouble(String s){
        //Метод, переводящий числовую строку в дробное число
        int N=0;
        int count = 0;
        boolean startToCount = false;
        for(int i = 0;i<s.length();i++){
            switch (s.charAt(i)){
                case '0':{
                    if(startToCount){
                        count++;
                    }
                    N=N*10;
                    break;
                }
                case '1':{
                    if(startToCount){
                        count++;
                    }
                    N=N*10+1;
                    break;
                }
                case '2':{
                    if(startToCount){
                        count++;
                    }
                    N=N*10+2;
                    break;
                }
                case '3':{
                    if(startToCount){
                        count++;
                    }
                    N=N*10+3;
                    break;
                }
                case '4':{
                    if(startToCount){
                        count++;
                    }
                    N=N*10+4;
                    break;
                }
                case '5':{
                    if(startToCount){
                        count++;
                    }
                    N=N*10+5;
                    break;
                }
                case '6':{
                    if(startToCount){
                        count++;
                    }
                    N=N*10+6;
                    break;
                }
                case '7':{
                    if(startToCount){
                        count++;
                    }
                    N=N*10+7;
                    break;
                }
                case '8':{
                    if(startToCount){
                        count++;
                    }
                    N=N*10+8;
                    break;
                }
                case '9':{
                    if(startToCount){
                        count++;
                    }
                    N=N*10+9;
                    break;
                }
                case '.':{
                    startToCount = true;
                }
                default:{
                    Log.e("RedactionField" ,"Argument string have wrong format");
                }
            }
        }
        return N/Math.pow(10,count);
    }

    public static String TimeFormat(int N){
        //Метод, приводящий время к формату ММ:СС и ограничивающий максимальное время одним часом
        if(N>3600||N<0){
            N=3600;
        }
        String s = (N/60)+":"+(N%60);
        return s;
    }
    public static String TimeFormat(long N){
        //Первая перегрузка
        if(N>3600||N<0){
            N=3600;
        }
        String s = (N/60)+":"+(N%60);
        return s;
    }
    public static String TimeFormat(String s){
        //Вторая перегрузка
        int N = StringToInt(s);
        if(N>3600||N<0){
            N=3600;
        }
        s = (N/60)+":"+(N%60);
        return s;
    }
    private void player(Canvas canvas){
        //Метод регулирующий поведение кнопок и лоадинга
        //Рисуем битмап кнопки меню
        menu = Bitmap.createBitmap(getHeight() / 10, getHeight() / 10, Bitmap.Config.RGB_565);
        Canvas menuCanvas = new Canvas(menu);
        menuCanvas.drawColor(Color.BLUE);
        Paint menuPaint = new Paint();
        menuPaint.setAntiAlias(true);
        menuPaint.setColor(Color.WHITE);
        menuCanvas.drawCircle(menuCanvas.getWidth() / 2, menuCanvas.getHeight() / 5, menuCanvas.getHeight() / 10, menuPaint);
        menuCanvas.drawCircle(menuCanvas.getWidth() / 2, menuCanvas.getHeight() / 2, menuCanvas.getHeight() / 10, menuPaint);
        menuCanvas.drawCircle(menuCanvas.getWidth() / 2, 4 * menuCanvas.getHeight() / 5, menuCanvas.getHeight() / 10, menuPaint);

        //Рисуем битмап кнопкри меню, выводящийся при нажатии
        darkMenu = Bitmap.createBitmap(getHeight() / 10, getHeight() / 10, Bitmap.Config.ARGB_8888);
        Canvas darkMenuCanvas = new Canvas(darkMenu);
        darkMenuCanvas.drawColor(getResources().getColor(R.color.colorPrimary));/*???*/
        menuPaint.setAntiAlias(true);
        menuPaint.setColor(Color.WHITE);
        darkMenuCanvas.drawCircle(menuCanvas.getWidth() / 5, menuCanvas.getHeight() / 2, menuCanvas.getWidth() / 10, menuPaint);
        darkMenuCanvas.drawCircle(menuCanvas.getWidth() / 2, menuCanvas.getHeight() / 2, menuCanvas.getWidth() / 10, menuPaint);
        darkMenuCanvas.drawCircle(4 * menuCanvas.getWidth() / 5, menuCanvas.getHeight() / 2, menuCanvas.getWidth() / 10, menuPaint);

        //Рисуем битмап кнопки, открывающей список объектов для создания
        objectListButton = Bitmap.createBitmap(getHeight()/3,getHeight()/10/3, Bitmap.Config.ARGB_8888);
        Canvas objectListButtonCanvas = new Canvas(objectListButton);
        menuPaint.setColor(Color.BLUE);
        menuPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        trapezoid = new Path();
        trapezoid.reset();
        trapezoid.moveTo(0, objectListButtonCanvas.getHeight());
        trapezoid.lineTo(getHeight() / 10 / 3, 0);
        trapezoid.lineTo(objectListButtonCanvas.getWidth() - getHeight() / 10 / 3, 0);
        trapezoid.lineTo(objectListButtonCanvas.getWidth(), objectListButtonCanvas.getHeight());
        trapezoid.lineTo(0, objectListButtonCanvas.getHeight());

        buttonTextPaint = new Paint();
        buttonTextPaint.setAntiAlias(true);
        buttonTextPaint.setColor(Color.WHITE);
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);
        buttonTextPaint.setTextSize(objectListButtonCanvas.getHeight() - 1);

        objectListButtonCanvas.drawPath(trapezoid, menuPaint);
        objectListButtonCanvas.drawText(getResources().getString(R.string.Menu_of_objects), objectListButtonCanvas.getWidth() / 2, objectListButtonCanvas.getHeight() - 2 * objectListButtonCanvas.getHeight() / 10, buttonTextPaint);

        if(objectMenuClose) {
            canvas.drawBitmap(objectListButton, getWidth() / 2 - getHeight() / 3 / 2, getHeight() - getHeight() / 10 / 3, mainPaint);
        }
        else{
            objectMenuAnimationUp();
        }

        if(menuOnClick){
            canvas.drawBitmap(darkMenu,0, 0, mainPaint);

            mainMenu = Bitmap.createBitmap(getHeight()*3/20, getHeight()*3/40, Bitmap.Config.ARGB_8888);
            Canvas mainMenuCanvas = new Canvas(mainMenu);
            mainPaint.setColor(Color.BLUE);
            mainMenuCanvas.drawColor(Color.WHITE);
            mainMenuCanvas.drawLine(0, 0, mainMenu.getWidth(), 0, mainPaint);
            mainMenuCanvas.drawLine(0, mainMenu.getHeight() / 2, mainMenu.getWidth(), mainMenu.getHeight() / 2, mainPaint);
            mainMenuCanvas.drawLine(0,mainMenu.getHeight(),mainMenu.getWidth(),mainMenu.getHeight(),mainPaint);
            mainMenuCanvas.drawLine(0, 0, 0, mainMenu.getHeight(), mainPaint);
            mainMenuCanvas.drawLine(mainMenu.getWidth(), 0, mainMenu.getWidth(), mainMenu.getHeight(), mainPaint);

            mainPaint.setColor(Color.BLACK);
            mainPaint.setTextAlign(Paint.Align.CENTER);
            mainPaint.setTextSize(mainMenu.getHeight()/3);

            mainMenuCanvas.drawText(getResources().getString(R.string.Save).toString(), mainMenu.getWidth() / 2, mainMenu.getHeight() / 3, mainPaint);
            mainMenuCanvas.drawText(getResources().getString(R.string.Load).toString(),mainMenu.getWidth()/2,mainMenu.getHeight()/6*5,mainPaint);

            canvas.drawBitmap(mainMenu,0,getHeight()/10,mainPaint);
        }
        else{
            canvas.drawBitmap(menu,0, 0, mainPaint);
        }

        Bitmap pause = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
        Bitmap play = BitmapFactory.decodeResource(getResources(), R.drawable.play);
        Bitmap reload = BitmapFactory.decodeResource(getResources(), R.drawable.reload);
        Bitmap terminate = BitmapFactory.decodeResource(getResources(), R.drawable.terminate);

        int playWidth = getWidth()/20;

        dst = new Rect (getWidth()/4-getHeight() /10 /4-playWidth,getHeight() /10/4,getWidth()/4-getHeight() /10/4,getHeight() /  10 - getHeight()/10/4);
        src = new Rect(0,0,play.getWidth(),play.getHeight());
        dst1 = new Rect(getWidth()/4-2*getHeight() /10 /4-2*playWidth,getHeight() /10/4,getWidth()/4-2*getHeight() /10 /4-playWidth,getHeight() /  10 - getHeight()/10/4);

        if (onLoad) {
            canvas.drawBitmap(terminate,src,dst1,mainPaint);
            canvas.drawBitmap(pause,src,dst,mainPaint);//Выводим кнопку остановки, если идёт подгрузка кадров
            load(canvas);//Запускаем подгрузку
        }
        else{
            if(x<getWidth() / 2&&time<StringToInt(maxTime)){
                canvas.drawBitmap(terminate,src,dst1,mainPaint);
                canvas.drawBitmap(play,src,dst,mainPaint);//Если подгрузка остановлена и строка не загружена до конца, выводим кнопку возобновления
                load(canvas);//Запускаем подгрузку
            }
            else{
                canvas.drawBitmap(terminate,src,dst1,mainPaint);
                canvas.drawBitmap(reload,src,dst,mainPaint);//Если подгрузка остановлена и строка загружена до конца, выводим кнопку рестарта
                load(canvas);//Запускаем подгрузку
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Обработка касаний
        float X = event.getX();
        float Y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN: {
                if(zeroPointExists){
                    X1 = X;
                    Y1 = Y;
                    break;
                }
                if(AnyDialogOpen){
                    if(X>=getWidth()/4+20&&X<=getWidth()/2-5&&Y>=3*getHeight()/4-dialogTextSize-40&&Y<=3 * getHeight() / 4 - 20){
                        OK_ButtonOnClick = true;
                        break;
                    }
                    if(X>=getWidth()/2+5&&X<=3*getWidth()/4-20&&Y>=3*getHeight()/4-dialogTextSize-40&&Y<=3 * getHeight() / 4 - 20){
                        Cancel_ButtonOnClick = true;
                        break;
                    }

                    if(X>0&&X<getWidth()&&Y>0&&Y<getHeight()){
                        keyBoardDown();
                        break;
                    }
                }
                else {
                    if(menuOnClick&&X<=mainMenu.getWidth()&&Y>=getHeight()/10&&Y<=getHeight()/10+mainMenu.getHeight()/2){
                        SaveDialogOpen = true;
                        ExpRedactorActivity.addTextView(getContext(),getResources().getString(R.string.SaveDialog).toString(),getWidth() / 4 + 20, getHeight() / 4 + 2 * dialogTextSize,getWidth()/2-40,getHeight()/2,dialogTextSize);
                        break;
                    }
                    if(menuOnClick&&X<=mainMenu.getWidth()&&Y>=getHeight()/10+mainMenu.getHeight()/2&&Y<=getHeight()/10+mainMenu.getHeight()){
                        LoadDialogOpen = true;
                        ExpRedactorActivity.addTextView(getContext(),getResources().getString(R.string.LoadDialog).toString(),getWidth() / 4 + 20, getHeight() / 4 + 2 * dialogTextSize,getWidth()/2-40,getHeight()/2,dialogTextSize);
                        break;
                    }
                    if(!objectMenuClose&&Y <= getHeight() - objectMenuScrollBar.getHeight() && Y >= getHeight() - objectMenuRect.height() &&X>=objectMenuLeftButton.getWidth()+objectList[0].getWidth()*4- k * scroll&&X<=objectMenuLeftButton.getWidth()+objectList[0].getWidth()*5- k * scroll&&X>6*objectMenuLeftButton.getWidth()/5){
                        dragging_4 = true;
                        onLoad = false;
                        X0 = X;
                        Y0 = Y;
                        break;
                    }
                    if(!objectMenuClose&&Y <= getHeight() - objectMenuScrollBar.getHeight() && Y >= getHeight() - objectMenuRect.height() &&X>=objectMenuLeftButton.getWidth()+objectList[0].getWidth()*3 - k*scroll&&X<=objectMenuLeftButton.getWidth()+objectList[0].getWidth()*4-k*scroll&&X>6*objectMenuLeftButton.getWidth()/5){
                        dragging_3 = true;
                        onLoad = false;
                        X0 = X;
                        Y0 = Y;
                        break;
                    }
                    if(!objectMenuClose&&Y <= getHeight() - objectMenuScrollBar.getHeight() && Y >= getHeight() - objectMenuRect.height() &&X >= (float) (6 * objectMenuLeftButton.getWidth()) / 5 - k * scroll +objectList[0].getWidth()*2 && X <= (float) (6 * objectMenuLeftButton.getWidth()) / 5 +objectList[0].getWidth()*2 + (float) objectList[2].getWidth() - k * scroll&&X>6*objectMenuLeftButton.getWidth()/5){
                        dragging_2 = true;
                        onLoad = false;
                        break;
                    }
                    if(!objectMenuClose&&Y <= getHeight() - objectMenuScrollBar.getHeight() && Y >= getHeight() - objectMenuRect.height() && X>=(float) (6 * objectMenuLeftButton.getWidth()) / 5 - k * scroll +objectList[0].getWidth()&&X<=(float) (6 * objectMenuLeftButton.getWidth()) / 5 +objectList[0].getWidth() + (float) objectList[2].getWidth() - k * scroll&&X>6*objectMenuLeftButton.getWidth()/5){
                        dragging_1 = true;
                    }
                    if (!objectMenuClose && Y <= getHeight() - objectMenuScrollBar.getHeight() && Y >= getHeight() - objectMenuRect.height() && X >= (float) (6 * objectMenuLeftButton.getWidth()) / 5 - k * scroll && X <= (float) (6 * objectMenuLeftButton.getWidth()) / 5 + (float) objectList[0].getWidth() - k * scroll&&X>6*objectMenuLeftButton.getWidth()/5) {
                        dragging_0 = true;
                        X0 = X;
                        Y0 = Y;
                        break;
                    }
                    if (!objectMenuClose && Y <= getHeight() && Y >= getHeight() - objectMenuScrollBar.getHeight() && X >= 6 * objectMenuLeftButton.getWidth() / 5 + scroll && X <= 6 * objectMenuLeftButton.getWidth() / 5 + objectMenuScroller.getWidth() + scroll) {
                        scrolling = true;
                        s = X - scroll;
                        break;
                    }
                    if (!objectMenuClose && X >= getWidth() - objectMenuLeftButton.getWidth() && X <= getWidth() && Y >= getHeight() - objectMenuRect.height() && Y <= getHeight()) {
                        objectMenuRightButtonOnClick = true;
                        break;
                    }
                    if (!objectMenuClose && X >= 0 && X <= objectMenuLeftButton.getWidth() && Y >= getHeight() - objectMenuRect.height() && Y <= getHeight()) {
                        objectMenuLeftButtonOnClick = true;
                        break;
                    }
                    if (!objectMenuClose && X <= getWidth() / 2 + objectListButton.getWidth() / 2 && X >= getWidth() / 2 - objectListButton.getWidth() / 2 && Y <= getHeight() - objectMenuRect.height() && Y >= getHeight() - objectMenuRect.height() - objectListButton.getHeight()) {
                        objectMenuClose = true;
                        break;
                    }
                    if (X <= objectListButton.getWidth() / 2 + getWidth() / 2 && X >= getWidth() / 2 - objectListButton.getWidth() / 2 && Y <= getHeight() && Y >= getHeight() - objectListButton.getHeight() && objectMenuClose) {
                        objectMenuClose = false;
                        break;
                    }
                    if (X >= 0 && X <= menu.getWidth() && Y >= 0 && Y <= menu.getHeight()) {
                        if (menuOnClick) {
                            menuOnClick = false;
                            break;
                        } else {
                            menuOnClick = true;
                            break;
                        }
                    }
                    if(matrixOfObjects[(int)X][(int)Y]==1){
                        Log.i("Force","touch");
                    }
                    if (X <= dst1.right && X >= dst1.left && Y <= dst1.bottom && Y >= dst1.top) {
                        //Terminate
                        Log.i(TAG, "onTouch");
                        onLoad = false;
                        time = 0;
                        x = 0;
                        ExpRedactorActivity.Experiment.allObjectsZeroCharacteristics();
                        break;
                    }
                    if (X <= dst.right && X >= dst.left && Y <= dst.bottom && Y >= dst.top) {
                        //Pause
                        if (onLoad) {
                            Log.i(TAG, "buttonOnTouch");
                            onLoad = false;
                        } else {
                            if (time == StringToInt(maxTime)) {
                                //Reload
                                Log.i(TAG, "buttonOnTouch");
                                ExpRedactorActivity.Experiment.allObjectsZeroCharacteristics();
                                time = 0;
                                x = 0;
                                onLoad = true;
                            } else {
                                //Play
                                Log.i(TAG, "buttonOnTouch");
                                onLoad = true;
                            }
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                if(zeroPointExists){
                    X1=X;
                    Y1=Y;
                }
                if(dragging_4){
                    if(!zeroPointExists) {
                        X0 = X;
                        Y0 = Y;
                    }
                }
                if(dragging_3){
                    if(!zeroPointExists) {
                        X0 = X;
                        Y0 = Y;
                    }
                }

                if(dragging_0){
                    X0=X;
                    Y0=Y;
                }
                if(scrolling){
                    if(scroll>=0&&scroll<=objectMenuScrollBar.getWidth()-objectMenuScroller.getWidth()-objectMenuLeftButton.getWidth()/5) {
                        scroll  = X-s;
                    }
                    else{
                        if(scroll<0){
                            scrolling=false;
                            scroll=0;
                        }
                        if(scroll>objectMenuScrollBar.getWidth()-objectMenuScroller.getWidth()-objectMenuLeftButton.getWidth()/5){
                            scrolling=false;
                            scroll = (float)objectMenuScrollBar.getWidth()-(float)objectMenuScroller.getWidth()-(float)objectMenuLeftButton.getWidth()/5;
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:{
                if (dragging_1){
                    dragging_1 = false;
                    Toast.makeText(getContext(),R.string.Trajectory,Toast.LENGTH_SHORT).show();
                }
                if(dragging_4){
                    if(zeroPointExists) {
                        X1 = X;
                        Y1 = Y;
                        if(surfaceTest(getVList(X0,Y0,Y1),time)) {
                            ExpRedactorActivity.Experiment.addSurface(getVList(X0, Y0, Y1));
                        }
                        dragging_4 = false;
                        zeroPointExists = false;
                    }
                    else{
                        zeroPointExists = true;
                    }
                    X1 = 0;
                    Y1 = 0;
                    break;
                }
                if(dragging_3){
                    if(zeroPointExists) {
                        X1 = X;
                        Y1 = Y;
                        if(surfaceTest(getHList(X0, Y0, X1),time)) {
                            ExpRedactorActivity.Experiment.addSurface(getHList(X0, Y0, X1));
                        }
                        dragging_3 = false;
                        zeroPointExists = false;
                    }
                    else{
                        zeroPointExists = true;
                    }
                    X1 = 0;
                    Y1 = 0;
                    break;
                }
                if(dragging_2){
                    if(!ExpRedactorActivity.Experiment.gravitationExists) {
                        ExpRedactorActivity.Experiment.AddGravity();
                        dragging_2 = false;
                    }
                    else{
                        dragging_2 = false;
                        Toast.makeText(getContext(),R.string.GravitationToast,Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                if(OK_ButtonOnClick){
                    if(BodyDialogOpen) {
                        OK_ButtonOnClick = false;
                        if(ExpRedactorActivity.getTextFromLayout(0).length()>=1&&ExpRedactorActivity.getTextFromLayout(1).length()>=1) {
                            ExpRedactorActivity.Experiment.AddRoundBody(ExpRedactorActivity.getTextFromLayout(0), StringToInt(ExpRedactorActivity.getTextFromLayout(1)), X0, Y0, time);
                            ExpRedactorActivity.deleteEditText(0);
                            ExpRedactorActivity.deleteEditText(1);
                            CancelTheDialog();
                        }
                        else{
                            Toast.makeText(getContext(),getResources().getText(R.string.RoundBodyDialogToast),Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                    if(SaveDialogOpen){
                        OK_ButtonOnClick = false;
                        try {
                            MainActivity.DB.save(getContext());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ExpRedactorActivity.deleteTextView();
                        CancelTheDialog();
                        break;
                    }
                    if(LoadDialogOpen){
                        OK_ButtonOnClick = false;
                        Intent i = new Intent(getContext(),LoadActivity.class);
                        try {
                            MainActivity.DB.save(getContext());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ExpRedactorActivity.deleteTextView();
                        startActivity((Activity) getContext(), i, null);
                        CancelTheDialog();
                        break;
                    }
                    break;

                }
                if(Cancel_ButtonOnClick){
                    if(BodyDialogOpen) {
                        CancelTheDialog();
                        Cancel_ButtonOnClick = false;
                        ExpRedactorActivity.deleteEditText(0);
                        ExpRedactorActivity.deleteEditText(1);
                        break;
                    }
                    if(SaveDialogOpen){
                        ExpRedactorActivity.deleteTextView();
                        CancelTheDialog();
                        Cancel_ButtonOnClick = false;
                        break;
                    }
                    if(LoadDialogOpen){
                        ExpRedactorActivity.deleteTextView();
                        CancelTheDialog();
                        Cancel_ButtonOnClick = false;
                        Intent i = new Intent(getContext(),LoadActivity.class);
                        startActivity((Activity)getContext(),i,null);
                        break;
                    }
                    break;
                }
                if(dragging_0){
                    dragging_0=false;
                    if(roundBodyTest(X,Y,time)) {
                        BodyDialogOpen = true;
                        mainPaint.setTextSize(dialogTextSize);
                        float[][] width = new float[2][];
                        width[0] = new float[getResources().getText(R.string.name).toString().length()];
                        width[1] = new float[getResources().getText(R.string.Mass).toString().length()];
                        mainPaint.getTextWidths(getResources().getText(R.string.name).toString(), width[0]);
                        mainPaint.getTextWidths(getResources().getText(R.string.Mass).toString(), width[1]);
                        for (int i = 0; i < width.length; i++) {
                            float w1 = 0;
                            for (int j = 0; j < width[i].length; j++) {
                                w1 = w1 + width[i][j];
                            }
                            if (w1 > w) {
                                w = w1;
                            }
                        }
                        ExpRedactorActivity.addEditText(getContext(), 0, getWidth() / 4 + w + 30, getHeight() / 4 + dialogTextSize + 10,(int)( 2 * getWidth() / 4 - 52 - w), 2*(int)dialogTextSize, null, dialogTextSize, Color.BLUE, false);
                        ExpRedactorActivity.addEditText(getContext(), 1, getWidth() / 4 + w + 30, getHeight() / 4 + 2 * dialogTextSize + 30,(int)( 2 * getWidth() / 4 - 52 - w), 2*(int)dialogTextSize, null, dialogTextSize, Color.BLUE, true);
                    }
                    break;
                }
                if(objectMenuLeftButtonOnClick){
                    objectMenuLeftButtonOnClick = false;
                    break;
                }
                if(objectMenuRightButtonOnClick){
                    objectMenuRightButtonOnClick = false;
                    break;
                }
                if(scrolling){
                    scrolling = false;
                    break;
                }
                break;
            }
        }
        return true;
    }
    private void objectMenuAnimationUp(){
        //Метод, отвечающий за отрисовку открытого меню объектов
        Bitmap objectMenu = Bitmap.createBitmap(getWidth(),getHeight()/5+getHeight()/10/3, Bitmap.Config.ARGB_8888);
        Canvas objectMenuCanvas = new Canvas(objectMenu);
        mainPaint.setColor(Color.BLUE);
        objectMenuRect = new Rect(0, getHeight() / 10 / 3, objectMenu.getWidth(), objectMenu.getHeight());
        objectMenuCanvas.drawRect(0, getHeight() / 10 / 3, objectMenu.getWidth(), objectMenu.getHeight(), mainPaint);
        Matrix moveTrapezoid = new Matrix();
        moveTrapezoid.reset();
        moveTrapezoid.setTranslate(objectMenuCanvas.getWidth() / 2 - objectListButton.getWidth() / 2, 0);
        trapezoid.transform(moveTrapezoid);
        objectMenuCanvas.drawPath(trapezoid, mainPaint);
        objectMenuCanvas.drawText(getResources().getString(R.string.Hide), getWidth() / 2, getHeight() / 10 / 3 - 2 * getHeight() / 10 / 10 / 3, buttonTextPaint);

        objectMenuLeftButton = Bitmap.createBitmap(objectMenuCanvas.getWidth()/20,objectMenuRect.height(), Bitmap.Config.ARGB_8888);
        Canvas leftButtonCanvas = new Canvas(objectMenuLeftButton);
        if(objectMenuLeftButtonOnClick||objectMenuRightButtonOnClick){
            leftButtonCanvas.drawColor(getResources().getColor(R.color.colorPrimary));
        }
        else{
            leftButtonCanvas.drawColor(Color.BLUE);
        }
        Path triangle = new Path();
        triangle.reset();
        triangle.moveTo(leftButtonCanvas.getWidth() / 5, leftButtonCanvas.getHeight() / 2);
        triangle.lineTo(4 * leftButtonCanvas.getWidth() / 5, leftButtonCanvas.getHeight() / 5);
        triangle.lineTo(4 * leftButtonCanvas.getWidth() / 5, 4 * leftButtonCanvas.getHeight() / 5);
        triangle.lineTo(leftButtonCanvas.getWidth() / 5, leftButtonCanvas.getHeight() / 2);
        mainPaint.setColor(Color.WHITE);
        mainPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        leftButtonCanvas.drawPath(triangle, mainPaint);

        objectMenuRightButton = Bitmap.createBitmap(objectMenuLeftButton);
        Matrix changeButton = new Matrix();
        changeButton.reset();
        changeButton.setRotate(180, objectMenuRightButton.getWidth() / 2, objectMenuRightButton.getHeight() / 2);
        changeButton.postTranslate(objectMenuRect.width() - objectMenuLeftButton.getWidth(), objectMenuRect.top);

        objectMenuScrollBar = Bitmap.createBitmap(objectMenuRect.width()-objectMenuLeftButton.getWidth()-objectMenuRightButton.getWidth(),objectMenuLeftButton.getHeight()/5, Bitmap.Config.ARGB_8888);
        Canvas objectMenuScrollBarCanvas = new Canvas(objectMenuScrollBar);
        Path scrollBar = new Path();
        scrollBar.reset();
        scrollBar.set(DrawRoundRect(objectMenuLeftButton.getWidth() / 5, objectMenuScrollBarCanvas.getHeight() / 4, objectMenuScrollBarCanvas.getWidth() - objectMenuLeftButton.getWidth() / 5, objectMenuScrollBarCanvas.getHeight() - objectMenuScrollBarCanvas.getHeight() / 4, objectMenuScrollBar.getHeight() / 4));
        mainPaint.setColor(Color.WHITE);
        objectMenuScrollBarCanvas.drawPath(scrollBar, mainPaint);

        objectMenuList = Bitmap.createBitmap(N*(4*objectMenuLeftButton.getHeight()/5+objectMenuLeftButton.getWidth()/5),objectMenuRect.height()-objectMenuScrollBar.getHeight()-objectMenuScrollBarCanvas.getHeight() / 4, Bitmap.Config.ARGB_8888);
        Canvas objectMenuListCanvas = new Canvas (objectMenuList);
        objectList = new Bitmap[N];
        for (int i = 0;i<objectList.length;i++){
            objectList[i] = Bitmap.createBitmap(4*objectMenuLeftButton.getHeight()/5+objectMenuLeftButton.getWidth()/5,objectMenuList.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas objectListCanvas = new Canvas (objectList[i]);
            mainPaint.setColor(Color.WHITE);
            mainPaint.setTextAlign(Paint.Align.CENTER);
            mainPaint.setTextSize(objectList[i].getHeight()-3 * objectMenuLeftButton.getHeight() / 5-2);
            objectListCanvas.drawPath(DrawRoundRect(objectMenuLeftButton.getWidth()/5, objectList[i].getHeight() - 3 * objectMenuLeftButton.getHeight() / 5, 4 * objectMenuLeftButton.getHeight() / 5, objectList[i].getHeight(), 5), mainPaint);
            switch (i){
                case 0:{
                    mainPaint.setColor(Color.WHITE);
                    mainPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    objectListCanvas.drawText(getResources().getString(R.string.object_0), 2 * objectMenuLeftButton.getHeight() / 5 + objectMenuLeftButton.getWidth() / 10, objectList[i].getHeight() - 3 * objectMenuLeftButton.getHeight() / 5 - objectMenuScrollBar.getHeight() / 4, mainPaint);
                    mainPaint.setColor(Color.BLACK);
                    objectListCanvas.drawCircle(objectList[0].getWidth()/2,(8*objectMenuLeftButton.getHeight()/5-objectList[0].getHeight())/2,objectList[0].getHeight()/4,mainPaint);
                    break;
                }
                case 1:{
                    mainPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    mainPaint.setColor(Color.WHITE);
                    objectListCanvas.drawText(getResources().getString(R.string.object_1),2*objectMenuLeftButton.getHeight()/5+objectMenuLeftButton.getWidth()/10,objectList[i].getHeight() - 3 * objectMenuLeftButton.getHeight() / 5 - objectMenuScrollBar.getHeight()/4,mainPaint);
                    break;
                }
                case 2:{
                    mainPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    mainPaint.setColor(Color.WHITE);
                    objectListCanvas.drawText(getResources().getString(R.string.object_2), 2 * objectMenuLeftButton.getHeight() / 5 + objectMenuLeftButton.getWidth() / 10, objectList[i].getHeight() - 3 * objectMenuLeftButton.getHeight() / 5 - objectMenuScrollBar.getHeight() / 4, mainPaint);
                    mainPaint.setColor(Color.BLACK);
                    Canvas obj_2Canvas = new Canvas(objectList[2]);
                    obj_2 = new Path();
                    obj_2.reset();
                    obj_2.moveTo(7 * obj_2Canvas.getWidth() / 80 + 2 * obj_2Canvas.getHeight() / 5, 5 * obj_2Canvas.getHeight() / 6);
                    obj_2.lineTo(2 * obj_2Canvas.getWidth() / 40 + 2 * obj_2Canvas.getHeight() / 5, 3 * obj_2Canvas.getHeight() / 5);
                    obj_2.lineTo(3 * obj_2Canvas.getWidth() / 40 + 2*obj_2Canvas.getHeight()/5, 3 * obj_2Canvas.getHeight() / 5);
                    obj_2.lineTo(3 * obj_2Canvas.getWidth() / 40 + 2*obj_2Canvas.getHeight() / 5, obj_2Canvas.getHeight() / 4);
                    obj_2.lineTo(4 * obj_2Canvas.getWidth() / 40 + 2*obj_2Canvas.getHeight() / 5, obj_2Canvas.getHeight() / 4);
                    obj_2.lineTo(4 * obj_2Canvas.getWidth() / 40 + 2*obj_2Canvas.getHeight() / 5, 3 * obj_2Canvas.getHeight() / 5);
                    obj_2.lineTo(5 * obj_2Canvas.getWidth() / 40 + 2*obj_2Canvas.getHeight() / 5, 3 * obj_2Canvas.getHeight() / 5);
                    obj_2.lineTo(7 * obj_2Canvas.getWidth() / 80 + 2 * obj_2Canvas.getHeight() / 5, 5 * obj_2Canvas.getHeight() / 6);
                    mainPaint.setColor(Color.BLACK);
                    mainPaint.setTextSize(textPaint.getTextSize()/3*2);
                    obj_2Canvas.drawPath(obj_2, mainPaint);
                    obj_2Canvas.drawText("g", 17 * obj_2Canvas.getWidth() / 40 + obj_2Canvas.getHeight() / 5, 3 * obj_2Canvas.getHeight() / 5, mainPaint);
                    break;
                }
                case 3:{
                    objectListCanvas.drawText(getResources().getString(R.string.object_3), 2 * objectMenuLeftButton.getHeight() / 5 + objectMenuLeftButton.getWidth() / 10, objectList[i].getHeight() - 3 * objectMenuLeftButton.getHeight() / 5 - objectMenuScrollBar.getHeight() / 4, mainPaint);
                    ArrayList<float[]> c = new ArrayList<float[]>();
                    int j=0;
                    while(j<(objectList[i].getWidth()-7*objectMenuLeftButton.getWidth()/5)/Objects.Surface.size){
                        j++;
                        float[] arr = new float[2];
                        arr[0] = objectMenuLeftButton.getWidth()/5+j*2*Objects.Surface.size;
                        arr[1] = objectList[i].getHeight()/2;
                        c.add(arr);
                    }
                    Canvas can = new Canvas(objectList[i]);
                    drawSurface(c,can,Color.BLACK);
                    break;
                }
                case 4:{
                    objectListCanvas.drawText(getResources().getString(R.string.object_4),2*objectMenuLeftButton.getHeight()/5+objectMenuLeftButton.getWidth()/10,objectList[i].getHeight() - 3 * objectMenuLeftButton.getHeight() / 5 - objectMenuScrollBar.getHeight()/4,mainPaint);
                    ArrayList<float[]> c = new ArrayList<float[]>();
                    int j=0;
                    while(j<(objectList[i].getWidth()-7*objectMenuLeftButton.getWidth()/5)/Objects.Surface.size){
                        j++;
                        float[] arr = new float[2];
                        arr[0] = objectList[i].getWidth()/2;
                        arr[1] = objectList[i].getHeight()+objectMenuLeftButton.getWidth()/10-j*2*Objects.Surface.size;
                        c.add(arr);
                    }
                    Canvas can = new Canvas(objectList[i]);
                    drawSurface(c,can,Color.BLACK);
                    break;
                }
                default:{
                    break;
                }
            }
            objectMenuListCanvas.drawBitmap(objectList[i],i*(4*objectMenuLeftButton.getHeight()/5+objectMenuLeftButton.getWidth()/5),0,mainPaint);
        }
        k = (float)objectMenuList.getWidth()/(float)objectMenuScrollBar.getWidth();
        Rect objectMenuListDST = new Rect(6*objectMenuLeftButton.getWidth()/5,objectMenuRect.top+objectMenuScrollBar.getHeight()/4,objectMenu.getWidth()-7*objectMenuRightButton.getWidth()/5,objectMenu.getHeight()-objectMenuScrollBar.getHeight());
        Rect objectMenuListSRC = new Rect((int)(k*scroll),0,(int)(objectMenuRect.width()-objectMenuLeftButton.getWidth()*12/5+(k*scroll)),objectMenuRect.height()-objectMenuScrollBar.getHeight()-objectMenuScrollBarCanvas.getHeight() / 4);

        //Scrolling mechanism
        int v =(int)(objectMenuScrollBar.getWidth()-objectMenuLeftButton.getWidth()/5-(objectMenuList.getWidth()-objectMenuListSRC.width())/k);
        objectMenuScroller = Bitmap.createBitmap(v,objectMenuLeftButton.getHeight()/5, Bitmap.Config.ARGB_8888);
        mainPaint.setColor(Color.GRAY);
        Canvas objectMenuScrollerCanvas = new Canvas(objectMenuScroller);
        Path scroller = new Path();
        scroller.reset();
        scroller.set(DrawRoundRect(objectMenuLeftButton.getWidth() / 5, objectMenuScrollBarCanvas.getHeight() / 4, objectMenuScroller.getWidth(), objectMenuScrollBarCanvas.getHeight() - objectMenuScrollBarCanvas.getHeight() / 4, objectMenuScrollBar.getHeight() / 4));
        objectMenuScrollerCanvas.drawPath(scroller,mainPaint);
        objectMenuScrollBarCanvas.drawBitmap(objectMenuScroller, 0+scroll,0, mainPaint);

        objectMenuCanvas.drawBitmap(objectMenuScrollBar,objectMenuLeftButton.getWidth(),objectMenu.getHeight() - objectMenuLeftButton.getHeight()/5,mainPaint);

        objectMenuCanvas.drawBitmap(objectMenuList,objectMenuListSRC,objectMenuListDST, mainPaint);
        objectMenuCanvas.drawBitmap(objectMenuRightButton,changeButton,mainPaint);
        objectMenuCanvas.drawBitmap(objectMenuLeftButton,0,getHeight()/10/3,mainPaint);
        canvas.drawBitmap(objectMenu, 0, getHeight() - objectMenu.getHeight(),mainPaint);

    }

    private void  Objects(){
        //Метод, отвечающий за функционал объектов

        /*Временные свойства*/

        if(dragging_0){
            Paint p = new Paint();
            p.setColor(Color.BLACK);
            p.setAntiAlias(true);
            canvas.drawCircle(X0, Y0, objectList[0].getHeight() / 4, p);
        }
        if(dragging_3&&zeroPointExists){
            if((X1==0&&Y1==0)||(X1==X0&&Y1==Y0)) {
                ArrayList<float[]> list = new ArrayList<float[]>();
                list.add(new float[]{X0, Y0});
                if(surfaceTest(list,time)){
                    drawSurface(X0, Y0, canvas, Color.GREEN);
                }
                else{
                    drawSurface(X0, Y0, canvas, Color.RED);
                }
            }
            else {
                if(surfaceTest(getHList(X0, Y0, X1),time)) {
                    drawSurface(getHList(X0, Y0, X1), canvas, Color.GREEN);
                }
                else{
                    drawSurface(getHList(X0, Y0, X1), canvas, Color.RED);
                }
            }
        }
        else if(dragging_3){
            ArrayList<float[]> list = new ArrayList<float[]>();
            list.add(new float[]{X0, Y0});
            if(surfaceTest(list,time)){
                drawSurface(X0, Y0, canvas, Color.GREEN);
            }
            else{
                drawSurface(X0, Y0, canvas, Color.RED);
            }
        }
        if(dragging_4&&zeroPointExists){
            if((X1==0&&Y1==0)||(X1==X0&&Y1==Y0)) {
                ArrayList<float[]> list = new ArrayList<float[]>();
                list.add(new float[]{X0, Y0});
                if(surfaceTest(list,time)){
                    drawSurface(X0, Y0, canvas, Color.GREEN);
                }
                else{
                    drawSurface(X0, Y0, canvas, Color.RED);
                }
            }
            else {
                if(surfaceTest(getVList(X0,Y0,Y1),time)) {
                    drawSurface(getVList(X0, Y0, Y1), canvas, Color.GREEN);
                }
                else{
                    drawSurface(getVList(X0, Y0, Y1), canvas, Color.RED);
                }
            }
        }
        else if(dragging_4){
            ArrayList<float[]> list = new ArrayList<float[]>();
            list.add(new float[]{X0, Y0});
            if(surfaceTest(list,time)){
                drawSurface(X0, Y0, canvas, Color.GREEN);
            }
            else{
                drawSurface(X0, Y0, canvas, Color.RED);
            }
        }

        /*Постоянные свойства*/
        for(int i = 0;i<ExpRedactorActivity.Experiment.surfaceList.size();i++){
            if(ExpRedactorActivity.Experiment.surfaceList.get(i).firstTime<=time) {
                int left = (int)ExpRedactorActivity.Experiment.surfaceList.get(i).left;
                int top = (int)ExpRedactorActivity.Experiment.surfaceList.get(i).top;
                int right = (int)ExpRedactorActivity.Experiment.surfaceList.get(i).right;
                int bottom = (int)ExpRedactorActivity.Experiment.surfaceList.get(i).bottom;

                for (int j = left;j<=right;j++){
                    matrixOfObjects[j][top] = 2;
                    matrixOfObjects[j][bottom] = 2;
                }
                for(int j = top;j<=bottom;j++){
                    matrixOfObjects[left][j] = 2;
                    matrixOfObjects[right][j] = 2;
                }
                drawSurface(ExpRedactorActivity.Experiment.surfaceList.get(i).coordinates, canvas, Color.BLACK);
            }
        }

        for (int i = 0;i<ExpRedactorActivity.Experiment.roundBodyList.size();i++) {
            if (ExpRedactorActivity.Experiment.roundBodyList.get(i).firstTime <= time) {

                ArrayList<Integer> pointListX = new ArrayList<Integer>();
                ArrayList<Integer> pointListY = new ArrayList<Integer>();
                int r = objectList[0].getHeight()/4;
                int X = (int)ExpRedactorActivity.Experiment.roundBodyList.get(i).x;
                int Y = (int)ExpRedactorActivity.Experiment.roundBodyList.get(i).y;
                pointListX.add(X);
                pointListY.add(Y);
                for(int j = 1;j<=r;j++){
                    pointListX.add(X-j);
                    pointListY.add(Y);

                    pointListX.add(X);
                    pointListY.add(Y-j);

                    pointListX.add(X+j);
                    pointListY.add(Y);

                    pointListX.add(X);
                    pointListY.add(Y+j);
                }

                for(int j = 0;j<r/Math.sqrt(2);j++){
                    pointListX.add(X+j);
                    pointListY.add(Y+j);

                    pointListX.add(X-j);
                    pointListY.add(Y+j);

                    pointListX.add(X+j);
                    pointListY.add(Y-j);

                    pointListX.add(X-j);
                    pointListY.add(Y-j);
                }

                for(int j = 0;j<pointListX.size();j++){
                    if(pointListX.get(j)>=0&&pointListX.get(j)<matrixOfObjects.length&&pointListY.get(j)>=0&&pointListY.get(j)<matrixOfObjects[0].length) {
                        switch (matrixOfObjects[pointListX.get(j)][pointListY.get(j)]) {
                            case (0): {
                                matrixOfObjects[pointListX.get(j)][pointListY.get(j)] = 1;
                                break;
                            }
                            case (1): {
                                matrixOfObjects[pointListX.get(j)][pointListY.get(j)] = 1;
                                break;
                            }
                            case (2): {
                                ExpRedactorActivity.Experiment.roundBodyList.get(i).y = pointListY.get(j) - r; //ExpRedactorActivity.Experiment.roundBodyList.get(i).y-r;//(float)(r-Math.sqrt(r*r-(ExpRedactorActivity.Experiment.roundBodyList.get(i).x-pointListX.get(j))*(ExpRedactorActivity.Experiment.roundBodyList.get(i).x-pointListX.get(j))));
                                ExpRedactorActivity.Experiment.roundBodyList.get(i).addForce("N", 1.5 * Math.PI, ExpRedactorActivity.Experiment.roundBodyList.get(i).getFry(), time);
                                ExpRedactorActivity.Experiment.roundBodyList.get(i).Vy = 0;
                                break;
                            }
                        }
                    }
                }

                mainPaint.setColor(Color.BLACK);
                canvas.drawCircle(ExpRedactorActivity.Experiment.roundBodyList.get(i).x, ExpRedactorActivity.Experiment.roundBodyList.get(i).y, objectList[0].getHeight() / 4, mainPaint);
                if(ExpRedactorActivity.Experiment.roundBodyList.get(i).F.size() == 0) {
                    if(ExpRedactorActivity.Experiment.gravitationExists) {
                        ExpRedactorActivity.Experiment.roundBodyList.get(i).addForce("Gravitational force", Math.PI / 2, ExpRedactorActivity.Experiment.roundBodyList.get(i).m * ExpRedactorActivity.Experiment.gravitation.number, time);
                    }
                }
                else {
                    if (ExpRedactorActivity.Experiment.gravitationExists&&ExpRedactorActivity.Experiment.roundBodyList.get(i).F.get(0).forceName != "Gravitational force") {
                        ExpRedactorActivity.Experiment.roundBodyList.get(i).addForce("Gravitational force", Math.PI / 2, ExpRedactorActivity.Experiment.roundBodyList.get(i).m * ExpRedactorActivity.Experiment.gravitation.number, time);
                    }
                }
                if(onLoad) {
                    ExpRedactorActivity.Experiment.roundBodyList.get(i).Made_a_FromForces();
                    ExpRedactorActivity.Experiment.roundBodyList.get(i).Vx = ExpRedactorActivity.Experiment.roundBodyList.get(i).Vx + ExpRedactorActivity.Experiment.roundBodyList.get(i).ax;
                    ExpRedactorActivity.Experiment.roundBodyList.get(i).Vy = ExpRedactorActivity.Experiment.roundBodyList.get(i).Vy + ExpRedactorActivity.Experiment.roundBodyList.get(i).ay;
                    ExpRedactorActivity.Experiment.roundBodyList.get(i).x = ExpRedactorActivity.Experiment.roundBodyList.get(i).x + (float) ExpRedactorActivity.Experiment.roundBodyList.get(i).Vx;
                    ExpRedactorActivity.Experiment.roundBodyList.get(i).y = ExpRedactorActivity.Experiment.roundBodyList.get(i).y + (float) ExpRedactorActivity.Experiment.roundBodyList.get(i).Vy;
                }

            }
        }

        if (ExpRedactorActivity.Experiment.gravitationExists&&ExpRedactorActivity.Experiment.gravitation.firstTime <= time) {
            Matrix gravitationMatrix = new Matrix();
            gravitationMatrix.reset();
            gravitationMatrix.setTranslate(getWidth() - objectList[2].getWidth(), objectList[2].getHeight() - getHeight() / 20);
            Path gravitation = new Path(obj_2);
            gravitation.transform(gravitationMatrix);
            mainPaint.setColor(Color.BLACK);
            mainPaint.setTextSize(textPaint.getTextSize() / 3 * 2);
            canvas.drawPath(gravitation, mainPaint);
            canvas.drawText("g", getWidth() - objectList[2].getWidth() / 2, (float) (objectList[2].getHeight() * 1.25), mainPaint);
        }
    }

    private void Dialogs() {
        //Метод, отвечающий за отрисовку и наполнение диалоговых окон
        if (BodyDialogOpen) {
            DrawDialogBase();
            mainPaint.setTextAlign(Paint.Align.CENTER);
            mainPaint.setColor(Color.BLACK);
            mainPaint.setTextSize((getWidth() / 2 - 20) / 21);

            canvas.drawText(getResources().getText(R.string.body_creation).toString(), getWidth() / 2, getHeight() / 4 + 10 + mainPaint.getTextSize(), mainPaint);
            mainPaint.setTextAlign(Paint.Align.LEFT);
            mainPaint.setStyle(Paint.Style.FILL_AND_STROKE);

            canvas.drawText(getResources().getText(R.string.name).toString(), getWidth() / 4 + 20, getHeight() / 4 + 20 + 2 * mainPaint.getTextSize(), mainPaint);
            canvas.drawPath(DrawRoundRect(getWidth() / 4 + w + 30, getHeight() / 4 + 20 + mainPaint.getTextSize(), 3 * getWidth() / 4 - 20, getHeight() / 4 + 30 + 2 * mainPaint.getTextSize(), 10), mainPaint);
            mainPaint.setColor(Color.WHITE);
            canvas.drawPath(DrawRoundRect(getWidth() / 4 + w + 30 + 1, getHeight() / 4 + 20 + mainPaint.getTextSize() + 1, 3 * getWidth() / 4 - 20 - 1, getHeight() / 4 + 30 + 2 * mainPaint.getTextSize() - 1, 8), mainPaint);
            mainPaint.setColor(Color.BLACK);
            canvas.drawText(getResources().getText(R.string.Mass).toString(), getWidth() / 4 + 20, getHeight() / 4 + 40 + 3 * mainPaint.getTextSize(), mainPaint);
            canvas.drawPath(DrawRoundRect(getWidth() / 4 + w + 30, getHeight() / 4 + 40 + 2 * mainPaint.getTextSize(), 3 * getWidth() / 4 - 20, getHeight() / 4 + 50 + 3 * mainPaint.getTextSize(), 10), mainPaint);
            mainPaint.setColor(Color.WHITE);
            canvas.drawPath(DrawRoundRect(getWidth() / 4 + w + 30 + 1, getHeight() / 4 + 40 + 2 * mainPaint.getTextSize() + 1, 3 * getWidth() / 4 - 20 - 1, getHeight() / 4 + 50 + 3 * mainPaint.getTextSize() - 1, 8), mainPaint);
        }

        if(SaveDialogOpen){
            DrawDialogBase();
            }

        if(LoadDialogOpen){
            DrawDialogBase();
            }
    }
    private void DrawDialogBase(){
        //Шаблон диалогового окна
        dialogTextSize = (getWidth() / 2 - 20) / 21;
        AnyDialogOpen = true;
        onLoad = false;
        Paint basePaint = new Paint();
        basePaint.setAntiAlias(true);
        basePaint.setColor(Color.BLACK);
        basePaint.setAlpha(170);
        canvas.drawRect(0, 0, getWidth(), getHeight(), basePaint);
        basePaint.setAlpha(256);
        basePaint.setColor(Color.BLUE);
        canvas.drawPath(DrawRoundRect(getWidth() / 4, getHeight() / 4, 3 * getWidth() / 4, 3 * getHeight() / 4, 10), basePaint);
        basePaint.setColor(Color.WHITE);
        canvas.drawPath(DrawRoundRect(getWidth() / 4 + 10, getHeight() / 4 + 10, 3 * getWidth() / 4 - 10, 3 * getHeight() / 4 - 10, 6), basePaint);if(OK_ButtonOnClick){
            mainPaint.setColor(getResources().getColor(R.color.colorPrimaryDark));
            canvas.drawPath(DrawRoundRect(getWidth() / 4 + 20, 3 * getHeight() / 4 - mainPaint.getTextSize() - 40, getWidth() / 2 - 5, 3 * getHeight() / 4 - 20, 5), mainPaint);
        }
        else{
            mainPaint.setColor(Color.BLUE);
            canvas.drawPath(DrawRoundRect(getWidth() / 4 + 20, 3 * getHeight() / 4 - mainPaint.getTextSize() - 40, getWidth() / 2 - 5, 3 * getHeight() / 4 - 20, 5), mainPaint);
        }
        if(Cancel_ButtonOnClick){
            mainPaint.setColor(getResources().getColor(R.color.colorPrimaryDark));
            canvas.drawPath(DrawRoundRect(getWidth() / 2 + 5, 3 * getHeight() / 4 - mainPaint.getTextSize() - 40, 3 * getWidth() / 4 - 20, 3 * getHeight() / 4 - 20, 5), mainPaint);
        }
        else{
            mainPaint.setColor(Color.BLUE);
            canvas.drawPath(DrawRoundRect(getWidth() / 2 + 5, 3 * getHeight() / 4 - mainPaint.getTextSize() - 40, 3 * getWidth() / 4 - 20, 3 * getHeight() / 4 - 20, 5), mainPaint);

        }
        mainPaint.setColor(Color.WHITE);
        mainPaint.setTextAlign(Paint.Align.CENTER);
        mainPaint.setTextSize(2 * dialogTextSize/3);
        canvas.drawText(getResources().getText(R.string.OK_button).toString(), 3 * getWidth() / 8 + 15 / 2, 3 * getHeight() / 4 - 35, mainPaint);
        canvas.drawText(getResources().getText(R.string.Cancel_button).toString(), 5 * getWidth() / 8 - 15 / 2, 3 * getHeight() / 4 - 35, mainPaint);
    }
    public Path DrawRoundRect(float left,float top,float right,float bottom, float radius){
        //Метод, возвращающий Path прямоугольника с закруглёнными унлами
        //Подобный метод реализван в Андроид, но в версии, более поздней, чем заявленная в андроид манифесте приложения
        Path path = new Path();
        path.reset();
        path.addCircle(left + radius, top + radius, radius, Path.Direction.CW);
        path.addCircle(right - radius, top + radius, radius, Path.Direction.CW);
        path.addCircle(right - radius, bottom - radius, radius, Path.Direction.CW);
        path.addCircle(left + radius, bottom - radius, radius, Path.Direction.CW);
        path.moveTo(left, bottom - radius);
        path.lineTo(left, top + radius);
        path.lineTo(left + radius, top);
        path.lineTo(right - radius, top);
        path.lineTo(right, top + radius);
        path.lineTo(right, bottom - radius);
        path.lineTo(right - radius, bottom);
        path.lineTo(left + radius, bottom);
        path.lineTo(left, bottom - radius);
        return path;
    }

    public void keyBoardUp(){
        //Метод, открывающий клавиатуру
        //В данной версии программы не используется
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 0);
    }
    public void keyBoardDown(){
        //Метод, опускающий клавиатуру
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
    }
    private void CancelTheDialog(){
        //Метод, выполняющий общие действия при закрытии любого диалога
        keyBoardDown();
        AnyDialogOpen = false;
        BodyDialogOpen = false;
        SaveDialogOpen = false;
        LoadDialogOpen = false;
        ExceptionDialogOpen = false;
    }

    private void drawSurface(float x, float y,Canvas canvas,int color){
        //Метод отрисовки плоскости
        int size = Objects.Surface.size;
        mainPaint.setColor(color);
        canvas.drawLine(x - size, y - size, x + size, y - size, mainPaint);
        canvas.drawLine(x+size,y-size,x+size,y+size,mainPaint);
        canvas.drawLine(x+size,y+size,x-size,y+size,mainPaint);
        canvas.drawLine(x-size,y+size,x-size,y-size,mainPaint);
        canvas.drawLine(x - size, y - size, x + size, y + size, mainPaint);
    }

    private void drawSurface(ArrayList<float[]> c, Canvas canvas,int color){
        //Метод отрисовки плоскости
        int size = Objects.Surface.size;
        mainPaint.setColor(color);
        for (int i = 0;i<c.size();i++){
            boolean topClear =true;
            boolean rightClear = true;
            boolean bottomClear = true;
            boolean leftClear = true;
            canvas.drawLine(c.get(i)[0]-size,c.get(i)[1]-size,c.get(i)[0]+size,c.get(i)[1]+size,mainPaint);
            for (int j=0;j<c.size();j++){
                if (c.get(i)[0]+2*size==c.get(j)[0])rightClear=false;
                if (c.get(i)[0]-2*size==c.get(j)[0])leftClear=false;
                if (c.get(i)[1]-2*size==c.get(j)[1])bottomClear=false;
                if (c.get(i)[1]+2*size==c.get(j)[1])topClear=false;
            }
            if(topClear){
                canvas.drawLine(c.get(i)[0] - size, c.get(i)[1] + size, c.get(i)[0] + size, c.get(i)[1] + size, mainPaint);
                canvas.drawLine(c.get(i)[0] - size, c.get(i)[1] + size, c.get(i)[0] + size, c.get(i)[1] + size, mainPaint);
            }
            if(rightClear){
                canvas.drawLine(c.get(i)[0]+size,c.get(i)[1]+size,c.get(i)[0]+size,c.get(i)[1]-size,mainPaint);
                canvas.drawLine(c.get(i)[0]+size,c.get(i)[1]+size,c.get(i)[0]+size,c.get(i)[1]-size,mainPaint);
            }
            if(bottomClear){
                canvas.drawLine(c.get(i)[0]-size,c.get(i)[1]-size,c.get(i)[0]+size,c.get(i)[1]-size,mainPaint);
                canvas.drawLine(c.get(i)[0]-size,c.get(i)[1]-size,c.get(i)[0]+size,c.get(i)[1]-size,mainPaint);
            }
            if(leftClear){
                canvas.drawLine(c.get(i)[0]-size,c.get(i)[1]-size,c.get(i)[0]-size,c.get(i)[1]+size,mainPaint);
                canvas.drawLine(c.get(i)[0]-size,c.get(i)[1]-size,c.get(i)[0]-size,c.get(i)[1]+size,mainPaint);
            }
        }
    }

    private void drawSurface(float[] x, float[] y,Canvas canvas,int color){
        //Метод отрисовки плоскости
        ArrayList<float[]> list = new ArrayList<float[]>();
        if(x.length<=y.length) {
            for (int i = 0; i < x.length; i++) {
                float[] arr = {x[i],y[i]};
                list.add(arr);
            }
        }
        else{
            for (int i = 0; i < y.length; i++) {
                float[] arr = {x[i],y[i]};
                list.add(arr);
            }
        }
        drawSurface(list,canvas,color);
    }
    private ArrayList<float[]> getHList(float X0,float Y0,float X1){
        //Метод, возвращающий координаты для горизонтальной плоскости
        float x;
        ArrayList<float[]> list = new ArrayList<float[]>();
        if(X0<X1){
            x = X0;
            while(x<X1){
                float[] arr = {x,Y0};
                list.add(arr);
                x = x+2*Objects.Surface.size;
            }
        }
        else{
            x = X0;
            while(x>X1){
                float[] arr = {x,Y0};
                list.add(arr);
                x = x-2*Objects.Surface.size;
            }
        }
        return list;
    }
    private ArrayList<float[]> getVList(float X0,float Y0,float Y1){
        //Метод, возвращающий координаты для вертикальной плоскости
        float y;
        ArrayList<float[]> list = new ArrayList<float[]>();
        if(Y0<Y1){
            y = Y0;
            while(y<Y1){
                float[] arr = {X0,y};
                list.add(arr);
                y = y+2*Objects.Surface.size;
            }
        }
        else{
            y = Y0;
            while(y>Y1){
                float[] arr = {X0,y};
                list.add(arr);
                y = y-2*Objects.Surface.size;
            }
        }
        return list;
    }

    private boolean roundBodyTest(float x,float y,long t){
        //Метод проверяющий корректность координат создания тела
        float r = objectList[0].getHeight()/4;
        for(int i = 0;i<ExpRedactorActivity.Experiment.roundBodyList.size();i++){
            if(ExpRedactorActivity.Experiment.roundBodyList.get(i).firstTime <= t && Math.pow(Math.pow(Math.abs(x-ExpRedactorActivity.Experiment.roundBodyList.get(i).x),2)+Math.pow(Math.abs(y-ExpRedactorActivity.Experiment.roundBodyList.get(i).y),2),0.5)<2*r) return false;
        }
        for(int i = 0;i<ExpRedactorActivity.Experiment.surfaceList.size();i++){
            for(int j = 0;j<ExpRedactorActivity.Experiment.surfaceList.get(i).coordinates.size();j++) {
                if (ExpRedactorActivity.Experiment.surfaceList.get(i).firstTime <= t && Math.abs(x - ExpRedactorActivity.Experiment.surfaceList.get(i).coordinates.get(j)[0]) < r + Objects.Surface.size && Math.abs(y - ExpRedactorActivity.Experiment.surfaceList.get(i).coordinates.get(j)[1]) < r + Objects.Surface.size) return false;
            }
        }
        return true;
    }

    private boolean surfaceTest(ArrayList<float[]> list,long t){
        //Метод, проверяющий корректность координат для создания плоскости
        for (int i = 0;i<ExpRedactorActivity.Experiment.roundBodyList.size();i++){
            for (int j = 0;j<list.size();j++){
                float r = objectList[0].getHeight()/4;
                if(ExpRedactorActivity.Experiment.roundBodyList.get(i).firstTime <= t&&Math.abs(list.get(j)[0] - ExpRedactorActivity.Experiment.roundBodyList.get(i).x) < r + Objects.Surface.size && Math.abs(list.get(j)[1] - ExpRedactorActivity.Experiment.roundBodyList.get(i).y) < r + Objects.Surface.size) return false;
            }
        }
        return true;
    }
}
