package com.app.rafael.gestaodeviagem.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.app.rafael.gestaodeviagem.utilidades.Alert;


public class Dml {

    private SQLiteDatabase db;
    private CriaBanco banco;
    private final Context context;

    public Dml(Context context)
    {
        banco = new CriaBanco(context);
        this.context = context;
    }


    public void insert(String tabela, ContentValues contentValues){
        db = banco.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON");
        try{
            banco.getWritableDatabase().insertOrThrow(tabela, null, contentValues);
        }catch (SQLiteException e){
            Alert a = new Alert(context, "Erro ao Inserir Registro", e.getMessage().toString());
        }
        finally {
            db.close();
        }
    }

    public Cursor getAll(String tabela, String[] campos, String where, String[] valorWhere, String ordem){
        Cursor cursor;

        db = banco.getReadableDatabase();
        try{
            cursor = db.query(tabela, campos, where, valorWhere, null, null, ordem, null);

            if(cursor!=null){
                cursor.moveToFirst();
            }

            db.close();
            return cursor;
        }catch (SQLException e){
            Alert a = new Alert(context, "Erro ao Buscar Registros", e.getMessage().toString());
        }
        return null;
    }


    public Cursor getAll(String tabela, String[] campos, String where, String[] valorWhere, String ordem, String group){
        Cursor cursor;

        db = banco.getReadableDatabase();
        try{
            cursor = db.query(tabela, campos, where, valorWhere, group, null, ordem, null);

            if(cursor!=null){
                cursor.moveToFirst();
            }

            db.close();
            return cursor;
        }catch (SQLException e){
            Alert a = new Alert(context, "Erro ao Buscar Registros", e.getMessage().toString());
        }
        return null;
    }


    public String getItem(String tabelaP, String campoP, String whereP){
        String where="";

        if(!whereP.isEmpty()){
            where = " where "+whereP;
        }else{
            Alert a = new Alert(context, "Erro ao Buscar Registro","Cláusula where não encontrada!");
        }

        String sql = "select "+campoP+" from "+tabelaP+where;

        try {
            db = banco.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            String row = cursor.getString(0);
            db.close();
            return row;
        }catch (SQLException e){
            Alert a = new Alert(context, "Erro ao Buscar Registro", e.getMessage().toString());
            return "";
        }
    }


    public float getSum(String tabelaP, String campoSumP, String whereP){
        String where="";

        if(!whereP.isEmpty()){
            where = " where "+whereP;
        }

        String sql = "select sum(cast("+campoSumP+" as float)) from "+tabelaP+where;

        try {
            db = banco.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            float count = cursor.getFloat(0);
            db.close();
            return count;
        }catch (SQLException e){
            Alert a = new Alert(context, "Erro ao Buscar Registro", e.getMessage().toString());
            return 0;
        }
    }


    public int getCount(String tabela){
        String sql = "select count(*) from "+tabela;

        try {
            db = banco.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            db.close();
            return count;
        }catch (SQLException e){
            Alert a = new Alert(context, "Erro ao Buscar Registro", e.getMessage().toString());
            return 0;
        }
    }

    public int getCount(String tabela, String where){
        String sql = "select count(*) from "+tabela+" where "+where;

        try {
            db = banco.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            db.close();
            return count;
        }catch (SQLException e){
            Alert a = new Alert(context, "Erro ao Buscar Registro", e.getMessage().toString());
            return 0;
        }
    }

    public void delete(String tabela, String where){
        db = banco.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON");
        try {
            if (where == null || where.isEmpty()) {
                db.delete(tabela, null, null);
            } else {
                db.delete(tabela, where, null);
            }
        }catch(SQLException e){
            Alert a = new Alert(context, "Erro ao excluir registro", "Registro filho localizado\n"+e.getMessage().toString());
        }

        db.close();
    }


    public void update(String tabela, ContentValues contentValues, String where){
        db = banco.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON");
        if(where == null || where.isEmpty()){
            db.update(tabela, contentValues, null, null);
        }else{
            db.update(tabela, contentValues, where, null);
        }
    }

    public void updateDynamic(String table, String values, String where){
        db = banco.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON");

        if(where == null || where.isEmpty()){
            db.execSQL("update "+table+" set "+values);
        }else{
            db.execSQL("update "+table+" set "+values+" where "+where);
        }
    }

}