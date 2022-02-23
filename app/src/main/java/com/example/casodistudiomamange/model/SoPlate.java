package com.example.casodistudiomamange.model;

public class SoPlate {
    private String codiceSingleOrder;
    private String nomePiatto;
    private long quantita;

    public SoPlate() {

    }

    public SoPlate(String codiceSingleOrder, String nomePiatto, long quantita) {
        this.codiceSingleOrder = codiceSingleOrder;
        this.nomePiatto = nomePiatto;
        this.quantita = quantita;
    }

    public String getCodiceSingleOrder() {
        return codiceSingleOrder;
    }

    public void setCodiceSingleOrder(String codiceSingleOrder) {
        this.codiceSingleOrder = codiceSingleOrder;
    }

    public String getNomePiatto() {
        return nomePiatto;
    }

    public void setNomePiatto(String nomePiatto) {
        this.nomePiatto = nomePiatto;
    }

    public long getQuantita() {
        return quantita;
    }

    public void setQuantita(long quantita) {
        this.quantita = quantita;
    }
}
