package com.example.forstugying.mechanicssimulator;

import java.io.Serializable;

public class Force implements Serializable {
    //Контруктор сил
    protected String forceName;//Имя силы
    protected double Fx;//Проекция силы на ось х
    protected double Fy;//Проекция силы на ось у
    protected double alpha;//Угол между осью х и вектором силы
    protected long firstTime;
    protected Force(String s1,double a,double F,long time){
        //Конструктор класса
        forceName = s1;
        firstTime = time;
        alpha = a;
        Fx = F*Math.cos(alpha);
        Fy = F*Math.sin(alpha);
    }
    protected double getFx(){
        //Метод, возвращающий проекцию резутирующей силы на ось X
        if(RedactionField.time>=firstTime){
            return Fx;
        }
        else{
            return 0;
        }
    }
    protected double getFy(){
        //Метод, возвращающий проекцию резутирующей силы на ось Y
        if(RedactionField.time>=firstTime){
            return Fy;
        }
        else{
            return 0;
        }
    }
}
