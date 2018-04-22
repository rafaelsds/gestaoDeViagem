package com.app.rafael.gestaodeviagem.db.tabelas;

public class GestaoDeViagensItens {
    public static final String TABELA = "gestao_viagens_itens";
    public static final String ID = "nr_sequencia";
    public static final String IDGV = "nr_seq_gv";
    public static final String CATEGORIA = "ie_categoria";
    public static final String DATA = "dt_lancamento";
    public static final String VALOR = "vl_lancamento";
    public static final String OBSERVACAO = "ds_observacao";

    public void GestaoDeViagensItens(){
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS "+ TABELA +"("
                + ID + " integer primary key autoincrement, "
                + IDGV + " integer, "
                + CATEGORIA + " integer, "
                + DATA + " text, "
                + VALOR + " text, "
                + OBSERVACAO + " text, "
                + "FOREIGN KEY ("+ IDGV +") REFERENCES "+GestaoDeViagens.TABELA+" ("+GestaoDeViagens.ID+"), "
                + "FOREIGN KEY ("+ CATEGORIA +") REFERENCES "+Categorias.TABELA+" ("+Categorias.ID+")"
                +")";
    }

    public static String alterTable(){
        return "ALTER TABLE "+ TABELA + " RENAME TO TEMP";
    }

    public static String populateTable(int oldVersion){
        if(oldVersion <= 2){
            return "INSERT INTO "+TABELA+" ("+ID+","+IDGV+","+CATEGORIA+","+DATA+","+VALOR+")"
                    +" SELECT "+ID+","+IDGV+","+CATEGORIA+","+DATA+","+VALOR
                    +" FROM TEMP";
        }else{
            return "INSERT INTO "+TABELA+" ("+ID+","+IDGV+","+CATEGORIA+","+DATA+","+VALOR+","+OBSERVACAO+")"
                    +" SELECT "+ID+","+IDGV+","+CATEGORIA+","+DATA+","+VALOR+","+OBSERVACAO
                    +" FROM TEMP";
        }
    }

    public static String dropTemp(){
        return "DROP  TABLE TEMP";
    }
}