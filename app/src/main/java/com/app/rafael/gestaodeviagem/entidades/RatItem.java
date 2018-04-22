package com.app.rafael.gestaodeviagem.entidades;


import java.io.Serializable;

public class RatItem implements Serializable {

    private Integer id, idRat;
    private String horas, dtLancamento, valor;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdRat() {
        return idRat;
    }

    public void setIdRat(Integer idRat) {
        this.idRat = idRat;
    }

    public String getHoras() {
        return horas;
    }

    public void setHoras(String horas) {
        this.horas = horas;
    }

    public String getDtLancamento() {
        return dtLancamento;
    }

    public void setDtLancamento(String dtLancamento) {
        this.dtLancamento = dtLancamento;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public RatItem(Integer id, Integer idRat, String horas, String dtLancamento, String valor) {
        this.id = id;
        this.idRat = idRat;
        this.horas = horas;
        this.dtLancamento = dtLancamento;
        this.valor = valor;
    }
}
