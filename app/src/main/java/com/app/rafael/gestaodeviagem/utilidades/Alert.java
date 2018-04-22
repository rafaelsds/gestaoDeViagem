package com.app.rafael.gestaodeviagem.utilidades;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Alert{

    private AlertDialog alerta;

    public Alert(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        alerta = builder.create();
        alerta.show();

    }

}
