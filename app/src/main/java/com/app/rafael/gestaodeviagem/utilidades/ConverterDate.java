package com.app.rafael.gestaodeviagem.utilidades;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConverterDate {


    public static String InserePadraoData(String data) {

        String ano = data.substring(0,4);
        String mes = data.substring(4,6);
        String dia = data.substring(6,8);

        return dia+"/"+mes+"/"+ano;

    }

    public static String RemovePadraoData(String data) {

        String dia = data.substring(0,2);
        String mes = data.substring(3,5);
        String ano = data.substring(6,10);

        return ano+mes+dia;

    }

    public static Date StrToDate(String date) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date d = null;

        try {
            d = (Date)formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return d;
    }

    public static String StrToStr(String date, String format) {

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat Dateformatter = new SimpleDateFormat(format);
        Date d = null;

        try {
            d = (Date)formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return Dateformatter.format(d);

    }


    public static String StrToStr(String date, String formartIn, String formatOut) {

        DateFormat formatter = new SimpleDateFormat(formartIn);
        DateFormat Dateformatter = new SimpleDateFormat(formatOut);
        Date d = null;

        try {
            d = (Date)formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return Dateformatter.format(d);

    }

}