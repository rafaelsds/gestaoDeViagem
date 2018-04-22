package com.app.rafael.gestaodeviagem.db.tabelas;

public class Categorias {
    public static final String TABELA = "categorias";
    public static final String ID = "nr_sequencia";
    public static final String DESCRICAO = "ds_categoria";
    public static final String TIPO = "ie_tipo";

    public void Categorias(){
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS "+ TABELA +"("
                + ID + " integer primary key autoincrement, "
                + DESCRICAO + " text, "
                + TIPO + " text"
                +")";
    }

}