//Конструктор объектов
package com.example.forstugying.mechanicssimulator;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class Objects implements Serializable {
    static public abstract class ObjectOfExp {
        //Родительский класс для всех объектов эксперимента
        protected String name;
        protected long firstTime;
        protected ObjectOfExp(String n,long fT){
            name = n;
            firstTime = fT;
        }

    }
    static public class Trajectory extends ObjectOfExp implements Serializable{
        //Класс объекта траектория
        //Этот объект ещё не реализован и, пока что, не используется в приложении

        protected int[] x;//Абцисса для каждого момента времени
        protected int[] y;//Ордината для каждого момента времени
        protected Trajectory(String s1,long FirstTime){
            //Конструктор траектории движения объекта
            super(s1,FirstTime);
        }
    }
    static public class RoundBody extends ObjectOfExp implements Serializable{
        //Класс объекта тело

        protected int m;//Масса тела
        protected float x;
        protected float x0;
        protected float y;
        protected float y0;
        protected double Vx;
        protected float Vx0;
        protected double Vy;
        protected float Vy0;
        protected double ax;
        protected float ax0;
        protected double ay;
        protected long ay0;
        protected ArrayList<Force> F = new ArrayList<Force>();

        protected void zeroCharacteristics(){
            //Метод, присваивающий всем характеристикам начальные значения
            x=x0;
            y=y0;
            Vx=Vx0;
            Vy=Vy0;
            ax=ax0;
            ay=ay0;
        }

        protected void addForce(String name,double alpha,double f,long time) {
            //Метод добавления силы телу
            if(name == "Gravitational force") {
                F.add(0,new Force(name,alpha,f,time));
            }
            else {
                F.add(new Force(name, alpha, f, time));
            }
        }

        protected RoundBody(String s1,int mass, float X, float Y,long FirstTime){
            //Конструктор класса
            super(s1,FirstTime);
            m = mass;
            x=X;
            x0=X;
            y=Y;
            y0=Y;
            Vx = 0;
            Vy = 0;
        }
        protected void AddV(double VX,double VY){
            //Метод добавления скорости
            //В данной версии программы не используется
            Vx=Vx + VX;
            Vy=Vy+VY;
        }
        protected void Made_a_FromForces(){
            //Метод, находящий ускорение тела
            double sigmaFx =0;
            double sigmaFy =0;
            for (int i = 0;i<F.size();i++){
                sigmaFx = sigmaFx+F.get(i).getFx();
                sigmaFy = sigmaFy+F.get(i).getFy();
            }
            ax =sigmaFx/m;
            ay =sigmaFy/m;
        }
        protected double getFrx(){
            //Метод, возвращающий проекцию резутирующей силы на ось X
            //В данной версии программы не используется
            double sigmaFx =0;
            for (int i = 0;i<F.size();i++){
                sigmaFx = sigmaFx+F.get(i).getFx();
            }
            return sigmaFx;
        }

        protected double getFry(){
            //Метод, возвращающий проекцию резутирующей силы на ось Y
            double sigmaFy =0;
            for (int i = 0;i<F.size();i++){
                sigmaFy = sigmaFy+F.get(i).getFy();
            }
            return sigmaFy;
        }
    }
    static public class Gravitation extends ObjectOfExp implements Serializable{
        //Класс объекта гравитация
        protected double number;

        protected Gravitation(long FirstTime){
            //Конструктор класса
            super("Gravitation",FirstTime);
            number = 9.8;
        }

        protected Gravitation(long FirstTime,double n){
            //Конструктор класса
            super("Gravitation",FirstTime);
            number = n;
        }

        protected Gravitation(String name,long FirstTime,double n){
            //Конструктор класса
            super(name,FirstTime);
            number = n;
        }

        public String toString(){
            String str = "";
            str = str +name+"/";
            str = str + Long.toString(firstTime)+"/";
            str = str + Double.toString(number)+"/";
            return str;
        }
    }
    static public class Surface extends ObjectOfExp implements Serializable{
        protected ArrayList<float[]> coordinates;
        protected static int size = 10;

        protected float left;
        protected float top;
        protected float right;
        protected float bottom;

        protected Surface(String name,long firstTime){
            //Конструктор класса
            super(name,firstTime);
        }
        protected Surface(String name,long firstTime,ArrayList<float[]> c){
            //Конструктор класса
            super(name, firstTime);
            coordinates= c;

            left = coordinates.get(0)[0];//coordinates.get(0)[0]-size;
            Log.i("KEK1", Float.toString(left));
            top = coordinates.get(0)[1]-size;
            right = coordinates.get(0)[0]+size;
            bottom = coordinates.get(0)[1]+size;
            for (int i = 0;i<coordinates.size();i++){
                if(coordinates.get(i)[0]<left){
                    left = coordinates.get(i)[0]-size;
                }
                if(coordinates.get(i)[0]>right){
                    right = coordinates.get(i)[0]+size;
                }
                if(coordinates.get(i)[1]<top){
                    top = coordinates.get(i)[1]-size;
                }
                if(coordinates.get(i)[1]>bottom){
                    bottom = coordinates.get(i)[1]+size;
                }
            }
        }
    }
}

