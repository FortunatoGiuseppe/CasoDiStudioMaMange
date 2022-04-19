package com.example.casodistudiomamange.model;

/** Modello di dati per il tavolo
 * Proprietà:
 * - codiceTavolo identificativo del tavolo
 * - tableFree flag che indica se il tavolo è libero
 * **/
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
