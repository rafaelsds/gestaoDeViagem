package com.app.rafael.gestaodeviagem.entidades;

public enum Status {
    NENHUM("",0),
    PENDENTE("Pendente", 1),
    ENVIADO("Enviado", 2),
    PAGO("Pago",3);

    private String descricaoStatus;
    private int idStatus;

    private Status(String descricaoStatus, int idStatus) {
        this.descricaoStatus = descricaoStatus;
        this.idStatus = idStatus;
    }

    public static String getId(String descricaoTipo){

        for (Status e : Status.values()){
            if (e.descricaoStatus.equals(descricaoTipo))
                return String.valueOf(e.idStatus);
        }

        return "";
    }

    public static String getDescricao(String idTipo){

        for (Status e : Status.values()){
            if (String.valueOf(e.idStatus).equals(idTipo))
                return String.valueOf(e.descricaoStatus);
        }

        return "";
    }

    @Override
    public String toString() {
        return descricaoStatus;
    }
}
