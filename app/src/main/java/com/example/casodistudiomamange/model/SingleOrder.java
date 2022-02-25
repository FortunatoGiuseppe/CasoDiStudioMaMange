package com.example.casodistudiomamange.model;

public class SingleOrder {
    private String codiceSingleOrder;
    private String codiceGroupOrder;

    public SingleOrder() {
    }

    public SingleOrder(String codiceSingleOrder, String codiceGroupOrder) {
        this.codiceSingleOrder = codiceSingleOrder;
        this.codiceGroupOrder = codiceGroupOrder;
    }

    public String getCodiceSingleOrder() {
        return codiceSingleOrder;
    }

    public void setCodiceSingleOrder(String codiceSingleOrder) {
        this.codiceSingleOrder = codiceSingleOrder;
    }

    public String getCodiceGroupOrder() {
        return codiceGroupOrder;
    }

    public void setCodiceGroupOrder(String codiceGroupOrder) {
        this.codiceGroupOrder = codiceGroupOrder;
    }
}
