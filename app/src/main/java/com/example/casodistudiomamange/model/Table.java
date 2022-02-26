package com.example.casodistudiomamange.model;

public class Table {
    private String codiceTavolo;
    private long flag;

    public String getCodiceTavolo() {
        return codiceTavolo;
    }

    public void setCodiceTavolo(String codiceTavolo) {
        this.codiceTavolo = codiceTavolo;
    }

    public long getFlag() {
        return flag;
    }

    public void setFlag(long flag) {
        this.flag = flag;
    }

    public Table() {
    }

    public Table(String codiceTavolo, long flag) {
        this.codiceTavolo = codiceTavolo;
        this.flag = flag;
    }
}
