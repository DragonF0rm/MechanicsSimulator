package com.example.forstugying.mechanicssimulator;

import android.graphics.Color;

import java.io.Serializable;

public class Space implements Serializable {
    //Конструктор среды
    //В данной версии приложения не используется
    protected int id;
    protected double ro;//Плотность
    protected String name;//Название вещества
    protected Aggregate state;//Агрегатное состояние со всеми специфическими свойствами
    protected int color;
    protected Space(String s1, double f1, Aggregate a1, int C){
        //Конструктор класса
        name = s1;
        ro = f1;
        state = a1;
        color = C;
    }
    public static class Aggregate{
        //Конструктор агрегатных состояний
        protected String state;//Состояние
        protected double c;//Удельная теплоёмкость
    }
    final static public class Solid extends Aggregate{
        //Конструктор для твёрдых веществ
        protected String state = "solid";//Состояние твёрдое
        protected float c;//Удельная теплоёмкость
        protected float T;//Температура плавления
        protected float l;//Удельная теплота плавления
        protected Solid(float f1,float f2,float f3){
            c=f1;
            T=f2;
            l=f3;
        }
    }
    final static public class Liquid  extends Aggregate{
        //Конструктор для жидких веществ
        protected String state = "liquid";//Состояние жидкое
        protected float c;//Удельная теплоёмкость
        protected float T;//Температура кипения
        protected float L;//Удельная теплота парообразования
        protected Liquid(float f1,float f2,float f3){
            c=f1;
            T=f2;
            L=f3;
        }
    }
    final static public class Gaseous  extends Aggregate{
        //Конструктор для газообразных веществ
        protected String state = "gaseous";//Состояние газообразное
        protected float c;//Удельная теплоёмкость
        protected float T;//Темпеатура конденсации
        protected Gaseous(float f1,float f2){
            c=f1;
            T=f2;
        }
    }
    //Несколько веществ
    final static Aggregate waterState = new Liquid(4200,100,2300000);
    final static Space Water = new Space("Water",1000,waterState,Color.BLUE);//Создаём вещество "вода"
    final static Aggregate iceState = new Solid(2100,0,334000);
    final static Space Ice = new Space("Ice",900,iceState,Color.WHITE);//Создфём вещество "лёд"
    final static Aggregate airState = new Gaseous(1010,0_0);
    final static Space Air = new Space("Air",1.29,airState, Color.WHITE);//Создаём вещество "воздух"
    final static Space NOTHING = new Space("NOTHING",0,null,Color.WHITE);
}
