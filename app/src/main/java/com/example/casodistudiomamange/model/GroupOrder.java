package com.example.casodistudiomamange.model;

import java.util.ArrayList;

public class GroupOrder {
    private long codice;
    private ArrayList<SingleOrder> singleOrders;

    public long getCodice() {
        return codice;
    }

    public void setCodice(long codice) {
        this.codice = codice;
    }

    public ArrayList<SingleOrder> getSingleOrders() {
        return singleOrders;
    }

    public void setSingleOrders(ArrayList<SingleOrder> singleOrders) {
        this.singleOrders = singleOrders;
    }

    public GroupOrder(long codice, ArrayList<SingleOrder> singleOrders) {
        this.codice = codice;
        this.singleOrders = singleOrders;
    }

    public GroupOrder(long codice){
        this.codice = codice;
    }
}
