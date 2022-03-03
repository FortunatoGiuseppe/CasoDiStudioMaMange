package com.example.casodistudiomamange.model;

public class SoPlate {
    private String codiceSingleOrder;
    private String nomePiatto;
    private long quantita;
    private String codiceGroupOrder;
    private String codiceTavolo;
    private String username;

    public SoPlate() {

    }

    public SoPlate(String codiceSingleOrder, String nomePiatto, long quantita, String codiceGroupOrder, String codiceTavolo, String username) {
        this.codiceSingleOrder = codiceSingleOrder;
        this.nomePiatto = nomePiatto;
        this.quantita = quantita;
        this.codiceGroupOrder = codiceGroupOrder;
        this.codiceTavolo = codiceTavolo;
        this.username = username;
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

    public String getCodiceGroupOrder() {
        return codiceGroupOrder;
    }

    public void setCodiceGroupOrder(String codiceGroupOrder) {
        this.codiceGroupOrder = codiceGroupOrder;
    }

    public String getCodiceTavolo() {
        return codiceTavolo;
    }

    public void setCodiceTavolo(String codiceTavolo) {
        this.codiceTavolo = codiceTavolo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
