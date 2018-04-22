package com.app.rafael.gestaodeviagem.entidades;

public enum TipoCategoria {
    RECEITA("GV", 0),
    DESPESA("Despesa", 1);

    private String descricaoTipo;
    private int idTipo;

    private TipoCategoria(String descricaoTipo, int idTipo) {
        this.descricaoTipo = descricaoTipo;
        this.idTipo = idTipo;
    }

    public static String getId(String descricaoTipo){

        for (TipoCategoria e : TipoCategoria.values()){
            if (e.descricaoTipo.toUpperCase().equals(descricaoTipo.toUpperCase()))
                return String.valueOf(e.idTipo);
        }

        return "";
    }

    public static String getDescricao(String idTipo){

        for (TipoCategoria e : TipoCategoria.values()){
            if (String.valueOf(e.idTipo).equals(idTipo))
                return String.valueOf(e.descricaoTipo);
        }

        return "";
    }

    @Override
    public String toString() {
        return descricaoTipo;
    }
}
