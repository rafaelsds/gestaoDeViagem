package com.app.rafael.gestaodeviagem.utilidades;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GetDate {

    public static String today(int addDay, int addMonth, int addYear){
        //Date d = new Date();
        Calendar data = Calendar.getInstance();
        data.add(Calendar.DAY_OF_MONTH,addDay);
        data.add(Calendar.MONTH,addMonth );
        data.add(Calendar.YEAR,addYear );

        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        return formatador.format(data.getTime());
    }

    public static String today(){
        //Date d = new Date();
        Calendar data = Calendar.getInstance();
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        return formatador.format(data.getTime());
    }
    
    public static String today(Boolean dayFirst, int month, int year){
        //Date d = new Date();
        Calendar data = Calendar.getInstance();

        data.set(Calendar.DAY_OF_MONTH,1);
        data.add(Calendar.MONTH,month );
        data.add(Calendar.YEAR,year );

        if(!dayFirst){
            int dias = data.getActualMaximum(Calendar.DAY_OF_MONTH);
            data.set(Calendar.DAY_OF_MONTH,dias);
        }

        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        return formatador.format(data.getTime());
    }

    public static long between(String dtInicialP, String dtFinalP) {

        DateFormat df = new SimpleDateFormat ("dd/MM/yyyy");

        try {
            Date dtInicial = df.parse (dtInicialP);
            Date dtFinal = df.parse (dtFinalP);
            return(dtFinal.getTime() - dtInicial.getTime() + 3600000L) / 86400000L;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0L;
    }

    public static String addDay(String date, int addDay){

        DateFormat df = new SimpleDateFormat ("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();

        try {
            Date dt = df.parse (date);
            calendar.setTime(dt);
            calendar.add(Calendar.DAY_OF_MONTH, addDay);

            return df.format(calendar.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

}