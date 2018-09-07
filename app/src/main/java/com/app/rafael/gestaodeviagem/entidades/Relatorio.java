package com.app.rafael.gestaodeviagem.entidades;


public class Relatorio {

    private String descricao, valor, media, valorDespesa;

    public Relatorio(String descricao, String valor, String media) {
        this.descricao = descricao;
        this.valor = valor;
        this.media = media;
    }

    public Relatorio(String descricao, String valor, String media, String valorDespesa) {
        this.descricao = descricao;
        this.valor = valor;
        this.media = media;
        this.valorDespesa = valorDespesa;
    }

    public Relatorio(String descricao, String valor) {
        this.descricao = descricao;
        this.valor = valor;
        this.media = media;
    }

    public String getValorDespesa() {
        return valorDespesa;
    }

    public void setValorDespesa(String valorDespesa) {
        this.valorDespesa = valorDespesa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }
}
