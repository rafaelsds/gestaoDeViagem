package com.app.rafael.gestaodeviagem.db.tabelas;

public class Rats {
    public static final String TABELA = "rats";
    public static final String ID = "nr_sequencia";
    public static final String NRRAT = "nr_rat";
    public static final String STATUS = "ie_status";
    public static final String DTINICIAL = "dt_inicial";
    public static final String DTFINAL = "dt_final";
    public static final String IDGV = "nr_seq_gv";

    public void Rats(){
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS "+ TABELA +"("
                + ID + " integer primary key autoincrement, "
                + NRRAT + " text, "
                + STATUS + " text, "
                + DTINICIAL + " text, "
                + DTFINAL + " text, "
                + IDGV +" integer, "
                + "FOREIGN KEY ("+ IDGV +") REFERENCES "+GestaoDeViagens.TABELA+" ("+GestaoDeViagens.ID+")"
                +")";
    }
}