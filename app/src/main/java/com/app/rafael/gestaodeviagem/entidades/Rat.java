package com.app.rafael.gestaodeviagem.entidades;


import java.io.Serializable;

public class Rat implements Serializable {

    private Integer id;
    private String nrRat, status, dtInicial, dtFinal;
    private Float valorTotal, horas;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNrRat() {
        return nrRat;
    }

    public void setNrRat(String nrRat) {
        this.nrRat = nrRat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDtInicial() {
        return dtInicial;
    }

    public void setDtInicial(String dtInicial) {
        this.dtInicial = dtInicial;
    }

    public String getDtFinal() {
        return dtFinal;
    }

    public void setDtFinal(String dtFinal) {
        this.dtFinal = dtFinal;
    }

    public Float getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Float valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Float getHoras() {
        return horas;
    }

    public void setHoras(Float horas) {
        this.horas = horas;
    }

    public Rat(Integer id, String nrRat, String status, String dtInicial, String dtFinal, Float valorTotal, Float horas) {
        this.id = id;
        this.nrRat = nrRat;
        this.status = status;
        this.dtInicial = dtInicial;
        this.dtFinal = dtFinal;
        this.valorTotal = valorTotal;
        this.horas = horas;
    }
}
