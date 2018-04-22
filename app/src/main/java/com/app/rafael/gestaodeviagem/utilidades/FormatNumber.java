package com.app.rafael.gestaodeviagem.utilidades;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class FormatNumber {

    public static String numToString(Double num, String format){
        DecimalFormat df = new DecimalFormat(format);
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(num);
    }

    public static String numToString(Float num, String format){
        DecimalFormat df = new DecimalFormat(format);
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(num);
    }

    public static String numToString(Long num, String format){
        DecimalFormat df = new DecimalFormat(format);
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(num);
    }


    public static Float stringtoNum(String num){
        return  (Float.parseFloat(num.replace(".","").replace(",","."))  );
    }

}
