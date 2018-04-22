package com.app.rafael.gestaodeviagem.db.tabelas;

public class Configuracoes {
    public static final String TABELA = "configuracao";
    public static final String ID = "nr_sequencia";
    public static final String VLRAT = "vl_rat";
    public static final String VLALIMENTACAO = "vl_alimentacao";


    public void Configuracao(){
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS "+ TABELA +"("
                + ID + " integer primary key autoincrement,"
                + VLRAT + " text,"
                + VLALIMENTACAO + " text"
                +")";
    }

}