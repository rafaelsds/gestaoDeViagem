package com.app.rafael.gestaodeviagem.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.rafael.gestaodeviagem.db.tabelas.Categorias;
import com.app.rafael.gestaodeviagem.db.tabelas.Configuracoes;
import com.app.rafael.gestaodeviagem.db.tabelas.Despesas;
import com.app.rafael.gestaodeviagem.db.tabelas.GestaoDeViagens;
import com.app.rafael.gestaodeviagem.db.tabelas.GestaoDeViagensItens;
import com.app.rafael.gestaodeviagem.db.tabelas.Movimentos;
import com.app.rafael.gestaodeviagem.db.tabelas.Rats;
import com.app.rafael.gestaodeviagem.db.tabelas.RatsItens;
import com.app.rafael.gestaodeviagem.utilidades.Alert;

public class CriaBanco extends SQLiteOpenHelper {

    public static final String NOME_BANCO = "gestaodeviagem.db";
    private static final int VERSAO = 3;
    private final Context contexto;

    Dml dml;

    public CriaBanco(Context context){
        super(context, NOME_BANCO,null,VERSAO);
        this.contexto = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        CreateTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        CreateTables(db);

        try {
            /*ADICIONA FK DE CATEGORIA NA TABELA GVITENS - V2*/
            /*ADICIONA ATRIBUTO DS_OBSERVACAO TABELA GVITENS - V3*/
            db.execSQL(GestaoDeViagensItens.alterTable());
            db.execSQL(GestaoDeViagensItens.createTable());
            db.execSQL(GestaoDeViagensItens.populateTable(oldVersion));
            db.execSQL(GestaoDeViagensItens.dropTemp());
        }catch (SQLException e){
            Alert a = new Alert(contexto, "Erro ao Alterar Tabela "+GestaoDeViagensItens.TABELA, e.getMessage().toString());
        }

        try {
            /*ADICIONA ATRIBUTO DS_OBSERVACAO TABELA DESPESAS - V3*/
            db.execSQL(Despesas.alterTable());
            db.execSQL(Despesas.createTable());
            db.execSQL(Despesas.populateTable(oldVersion));
            db.execSQL(Despesas.dropTemp());
        }catch (SQLException e){
            Alert a = new Alert(contexto, "Erro ao Alterar Tabela "+Despesas.TABELA, e.getMessage().toString());
        }

    }


    public void CreateTables(SQLiteDatabase db){
        try {
            db.execSQL(Categorias.createTable());
            db.execSQL(Movimentos.createTable());
            db.execSQL(Configuracoes.createTable());
            db.execSQL(GestaoDeViagens.createTable());
            db.execSQL(GestaoDeViagensItens.createTable());
            db.execSQL(Rats.createTable());
            db.execSQL(RatsItens.createTable());
            db.execSQL(Despesas.createTable());
        }catch (SQLException e){
            Alert a = new Alert(contexto, "Erro ao Criar Tabela", e.getMessage().toString());
        }
    }

}