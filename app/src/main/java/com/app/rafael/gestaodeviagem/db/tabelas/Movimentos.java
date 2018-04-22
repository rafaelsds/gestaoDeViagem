package com.app.rafael.gestaodeviagem.db.tabelas;

public class Movimentos {
    public static final String TABELA = "movimentos";
    public static final String ID = "nr_sequencia";
    public static final String TIPO = "tipo";
    public static final String CATEGORIA = "nr_seq_categoria";
    public static final String VALOR = "vl_lancamento";
    public static final String DATA = "dt_lancamento";

    public void Movimentos(){
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS "+ TABELA +"("
                + ID + " integer primary key autoincrement,"
                + TIPO + " text,"
                + CATEGORIA + "integer,"
                + VALOR + " text,"
                + DATA + " integer"
                +")";
    }

}