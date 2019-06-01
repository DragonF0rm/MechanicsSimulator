package com.example.forstugying.mechanicssimulator;

import java.io.Serializable;
import java.util.ArrayList;

import static com.example.forstugying.mechanicssimulator.Objects.*;

public class Constructor implements Serializable {
    //Основной конструктор приложения
    //Собираем все переменные эксперимента в один объект этого класса

    protected String name;//Имя эксперимента
    protected boolean gravitationExists;//Информация о наличии гравитации в эксперименте
    protected long time;//Время выполнения эксперимента
    protected ArrayList<RoundBody> roundBodyList = new ArrayList<RoundBody>();//Все тела эксперимента
    protected Gravitation gravitation;//Гравитация эксперимента
    protected ArrayList<Surface> surfaceList = new ArrayList<Surface>();//Плоскости эксперимента

    protected Constructor() {
        //Конструктор эксперимента
        //Все значения присваиваем нулевые, так как начальные данные неизвестны
        //В последствии данные изменятся
        name = null;
        time = 0;
        gravitationExists = false;
    }

    protected Constructor(String Name, Space s, long t) {
        //Конструктор эксперимента
        //Переменная s в представленной версии программы не используется
        //В последствии, с её помощью можно будет установить среду, вкоторой проводится эксперимент
        name = Name;
        time = t;
        gravitationExists = false;
    }

    protected void AddRoundBody(String Name, int mass, float x, float y, long firstTime) {
        //Метод добавления тела в эксперимент
        roundBodyList.add(new RoundBody(Name, mass, x, y, firstTime));
    }

    protected void AddGravity() {
        //Метод добавления гравитации в эксперимент
        if (!gravitationExists) {
            gravitation = new Gravitation(RedactionField.time);
            gravitationExists = true;
            for (int i = 0; i < roundBodyList.size(); i++) {
                //Добавление силы тяжести каждому телу
                roundBodyList.get(i).addForce("Gravitational force", Math.PI / 2, roundBodyList.get(i).m * gravitation.number, RedactionField.time);
            }
        }
    }
    protected void AddGravity(String name,long time,double n) {
        //Метод добавления гравитации в эксперимент с изменённым значением ускорения свободного падения
        //В данной версии программы не используется
        if (!gravitationExists) {
            gravitation = new Gravitation(name,time,n);
            gravitationExists = true;
            for (int i = 0; i < roundBodyList.size(); i++) {
                //Доавление силы тяжести каждому телу
                roundBodyList.get(i).addForce("Gravitational force", Math.PI / 2, roundBodyList.get(i).m * gravitation.number, RedactionField.time);
            }
        }
    }

    protected void allObjectsZeroCharacteristics() {
        //Сброс всех характеристик тела на начальные
        //Используется при рестарте эксперимента
        for (int i = 0; i < roundBodyList.size(); i++) {
            roundBodyList.get(i).zeroCharacteristics();
        }
    }

    protected void addSurface(ArrayList<float[]> list) {
        //Метод добавления плоскости в эксперимент
        surfaceList.add(new Surface("Surface"+surfaceList.size(), RedactionField.time, list));
    }

}
