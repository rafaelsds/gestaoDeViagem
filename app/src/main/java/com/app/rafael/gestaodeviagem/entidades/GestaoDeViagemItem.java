package com.app.rafael.gestaodeviagem.entidades;


import java.io.Serializable;

public class GestaoDeViagemItem implements Serializable {

    private Integer id, idGv;
    private String categoria, dtLancamento, valor, observacao;

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdGv() {
        return idGv;
    }

    public void setIdGv(Integer idGv) {
        this.idGv = idGv;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
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

    public GestaoDeViagemItem(Integer id, Integer idGv, String categoria, String dtLancamento, String valor, String observacao) {
        this.id = id;
        this.idGv = idGv;
        this.categoria = categoria;
        this.dtLancamento = dtLancamento;
        this.valor = valor;
        this.observacao = observacao;
    }
}
