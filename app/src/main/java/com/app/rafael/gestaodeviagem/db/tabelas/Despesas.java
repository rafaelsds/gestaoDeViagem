package com.app.rafael.gestaodeviagem.db.tabelas;

public class Despesas {
    public static final String TABELA = "despesas";
    public static final String ID = "nr_sequencia";
    public static final String CATEGORIA = "ie_categoria";
    public static final String DATA = "dt_lancamento";
    public static final String VALOR = "vl_lancamento";
    public static final String OBSERVACAO = "ds_observacao";

    public void Despesas(){
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS "+ TABELA +"("
                + ID + " integer primary key autoincrement, "
                + CATEGORIA + " text, "
                + DATA + " text, "
                + VALOR + " text, "
                + OBSERVACAO + " text, "
                + "FOREIGN KEY ("+ CATEGORIA +") REFERENCES "+Categorias.TABELA+" ("+Categorias.ID+")"
                +")";
    }

    public static String alterTable(){
        return "ALTER TABLE "+ TABELA + " RENAME TO TEMP";
    }

    public static String populateTable(int oldVersion){
        if(oldVersion <= 2){
            return "INSERT INTO "+TABELA+" ("+ID+","+CATEGORIA+","+DATA+","+VALOR+")"
                    +" SELECT "+ID+","+CATEGORIA+","+DATA+","+VALOR
                    +" FROM TEMP";
        }else{
            return "INSERT INTO "+TABELA+" ("+ID+","+CATEGORIA+","+DATA+","+VALOR+","+OBSERVACAO+")"
                    +" SELECT "+ID+","+CATEGORIA+","+DATA+","+VALOR+","+OBSERVACAO
                    +" FROM TEMP";
        }
    }

    public static String dropTemp(){
        return "DROP  TABLE TEMP";
    }

}