package com.example.casodistudiomamange.model;

public class SingleOrder {
    private String codiceSingleOrder;
    private String codiceGroupOrder;
    private String codiceTavolo;
    private boolean isSingleOrderConfirmed;

    public SingleOrder() {
    }

    public SingleOrder(String codiceSingleOrder, String codiceGroupOrder, String codiceTavolo, boolean isSingleOrderConfirmed) {
        this.codiceSingleOrder = codiceSingleOrder;
        this.codiceGroupOrder = codiceGroupOrder;
        this.codiceTavolo = codiceTavolo;
        this.isSingleOrderConfirmed = isSingleOrderConfirmed;
    }

    public boolean isSingleOrderConfirmed() {
        return isSingleOrderConfirmed;
    }

    public void setSingleOrderConfirmed(boolean singleOrderConfirmed) {
        isSingleOrderConfirmed = singleOrderConfirmed;
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

    public String getCodiceTavolo() {
        return codiceTavolo;
    }

    public void setCodiceTavolo(String codiceTavolo) {
        this.codiceTavolo = codiceTavolo;
    }
}
