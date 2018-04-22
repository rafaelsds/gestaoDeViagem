package com.app.rafael.gestaodeviagem.db;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Backup {

    public static Boolean importar(Context context) {
        System.out.println(context.getApplicationInfo().packageName);
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + context.getApplicationInfo().packageName
                        + "//databases//" + CriaBanco.NOME_BANCO;

                String backupDBPath = CriaBanco.NOME_BANCO;
                File backupDB = new File(data, currentDBPath);
                File currentDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                return true;
            }
        }
        catch (Exception e) {
            return false;
        }

        return false;
    }

    public static Boolean exportar(Context context){

        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File dataDirectory = Environment.getDataDirectory();

        FileChannel source = null;
        FileChannel destination = null;

        String currentDBPath = "/data/" + context.getApplicationInfo().packageName + "/databases/"+CriaBanco.NOME_BANCO;
        String backupDBPath = CriaBanco.NOME_BANCO;
        File currentDB = new File(dataDirectory, currentDBPath);
        File backupDB = new File(externalStorageDirectory, backupDBPath);

        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());

            return true;

        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (source != null) source.close();
            } catch (IOException e) {
                return false;
            }
            try {
                if (destination != null) destination.close();
            } catch (IOException e) {
                return false;
            }
        }
    }

}

