package com.example.casodistudiomamange.model;

import java.util.ArrayList;

public class GroupOrder {
    private String codice;
    private boolean stato;
    private String codiceTavolo;

    public GroupOrder(String codice, boolean stato, String codiceTavolo) {
        this.codice = codice;
        this.stato = stato;
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

    public boolean isStato() {
        return stato;
    }

    public void setState(boolean stato) {
        this.stato = stato;
    }

    public String getCodiceTavolo() {
        return codiceTavolo;
    }

    public void setCodiceTavolo(String codiceTavolo) {
        this.codiceTavolo = codiceTavolo;
    }
}
