package com.app.rafael.gestaodeviagem.entidades;


import java.io.Serializable;

public class GestaoDeViagem implements Serializable {

    private Integer id;
    private String nrGv, status, dtInicial, dtFinal;
    private Float valorTotal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNrGv() {
        return nrGv;
    }

    public void setNrGv(String nrGv) {
        this.nrGv = nrGv;
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


    public GestaoDeViagem(Integer id, String nrGv, String status, String dtInicial, String dtFinal, Float valorTotal) {
        this.id = id;
        this.nrGv = nrGv;
        this.status = status;
        this.dtInicial = dtInicial;
        this.dtFinal = dtFinal;
        this.valorTotal = valorTotal;
    }

    public GestaoDeViagem(Integer id, String nrGv) {
        this.id = id;
        this.nrGv = nrGv;
    }

    public GestaoDeViagem(){}

    public GestaoDeViagem(GestaoDeViagem gv){
        this.id = gv.getId();
        this.nrGv = gv.getNrGv();
        this.status = gv.getStatus();
        this.dtInicial = gv.getDtInicial();
        this.dtFinal = gv.getDtFinal();
        this.valorTotal = gv.getValorTotal();
    }

    @Override
    public String toString() {
        return  nrGv ;

    }
}
