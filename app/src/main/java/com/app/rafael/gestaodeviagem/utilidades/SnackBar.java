package com.app.rafael.gestaodeviagem.utilidades;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.widget.LinearLayout;

public class SnackBar {

    private AlertDialog alerta;

    public SnackBar(String message, CoordinatorLayout layout, int time){
        Snackbar mySnackbar = Snackbar.make(layout, message, time);
        mySnackbar.show();
    }

    public SnackBar(String message, LinearLayout layout, int time){
        Snackbar mySnackbar = Snackbar.make(layout, message, time);
        mySnackbar.show();
    }

}
