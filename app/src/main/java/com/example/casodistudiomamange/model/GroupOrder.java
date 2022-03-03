package com.example.casodistudiomamange.model;

public class GroupOrder {
    private String codice;
    private boolean tableFree;
    private String codiceTavolo;

    public GroupOrder(String codice, boolean tableFree, String codiceTavolo) {
        this.codice = codice;
        this.tableFree = tableFree;
        this.codiceTavolo = codiceTavolo;
    }

    public GroupOrder() {
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public boolean isTableFree() {
        return tableFree;
    }

    public void setTableFree(boolean tableFree) {
        this.tableFree = tableFree;
    }

    public String getCodiceTavolo() {
        return codiceTavolo;
    }

    public void setCodiceTavolo(String codiceTavolo) {
        this.codiceTavolo = codiceTavolo;
    }
}
