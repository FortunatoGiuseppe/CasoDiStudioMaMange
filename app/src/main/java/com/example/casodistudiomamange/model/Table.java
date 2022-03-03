package com.example.casodistudiomamange.model;

public class Table {
    private String codiceTavolo;
    private boolean tableFree;

    public Table(String codiceTavolo, boolean tableFree) {
        this.codiceTavolo = codiceTavolo;
        this.tableFree = tableFree;
    }

    public Table() {
    }

    public String getCodiceTavolo() {
        return codiceTavolo;
    }

    public void setCodiceTavolo(String codiceTavolo) {
        this.codiceTavolo = codiceTavolo;
    }

    public boolean isTableFree() {
        return tableFree;
    }

    public void setTableFree(boolean tableFree) {
        this.tableFree = tableFree;
    }


}
