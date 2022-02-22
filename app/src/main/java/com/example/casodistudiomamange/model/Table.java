package com.example.casodistudiomamange.model;

public class Table {
    private String codicetavolo;
    private long flag;

    public String getCodicetavolo() {
        return codicetavolo;
    }

    public void setCodicetavolo(String codicetavolo) {
        this.codicetavolo = codicetavolo;
    }

    public long getFlag() {
        return flag;
    }

    public void setFlag(long flag) {
        this.flag = flag;
    }

    public Table() {
    }

    public Table(String codicetavolo, long flag) {
        this.codicetavolo = codicetavolo;
        this.flag = flag;
    }
}
