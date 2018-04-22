package com.app.rafael.gestaodeviagem.db.tabelas;

public class RatsItens {
    public static final String TABELA = "rats_itens";
    public static final String ID = "nr_sequencia";
    public static final String IDRAT = "nr_seq_rat";
    public static final String DATA = "dt_lancamento";
    public static final String VALOR = "vl_lancamento";
    public static final String HORAS = "qt_horas";

    public void RatsItens(){
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS "+ TABELA +"("
                + ID + " integer primary key autoincrement, "
                + IDRAT + " integer, "
                + DATA + " text, "
                + VALOR + " text, "
                + HORAS + " text, "
                + "FOREIGN KEY ("+ IDRAT +") REFERENCES "+Rats.TABELA+" ("+Rats.ID+")"
                +")";
    }
}