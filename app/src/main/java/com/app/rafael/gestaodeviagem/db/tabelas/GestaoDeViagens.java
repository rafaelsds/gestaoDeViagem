package com.app.rafael.gestaodeviagem.db.tabelas;

public class GestaoDeViagens {
    public static final String TABELA = "gestao_viagens";
    public static final String ID = "nr_sequencia";
    public static final String NRGV = "nr_gv";
    public static final String STATUS = "ie_status";
    public static final String DTINICIAL = "dt_inicial";
    public static final String DTFINAL = "dt_final";

    public void GestaoDeViagem(){
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS "+ TABELA +"("
                + ID + " integer primary key autoincrement, "
                + NRGV + " text, "
                + STATUS + " text, "
                + DTINICIAL + " text, "
                + DTFINAL + " text"
                +")";
    }
}