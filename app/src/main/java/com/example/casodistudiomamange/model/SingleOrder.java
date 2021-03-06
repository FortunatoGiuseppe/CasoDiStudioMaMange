package com.example.casodistudiomamange.model;

/** Modello di dati dell'ordinazione singola
 * Proprietà:
 * - codiceSingleOrder identificativo dell'ordinazione singola
 * - codiceGroupOrder identificativo dell'ordinazione di gruppo
 * - codiceTavolo identificativo del tavolo
 * - singleorderConfirmed flag boolean che indica se l'ordinazione è confermata
 * **/
public class SingleOrder {
    private String codiceSingleOrder;
    private String codiceGroupOrder;
    private String codiceTavolo;
    private boolean singleOrderConfirmed;

    public SingleOrder() {
    }

    public SingleOrder(String codiceSingleOrder, String codiceGroupOrder, String codiceTavolo, boolean singleOrderConfirmed) {
        this.codiceSingleOrder = codiceSingleOrder;
        this.codiceGroupOrder = codiceGroupOrder;
        this.codiceTavolo = codiceTavolo;
        this.singleOrderConfirmed = singleOrderConfirmed;
    }

    public boolean isSingleOrderConfirmed() {
        return singleOrderConfirmed;
    }

    public void setSingleOrderConfirmed(boolean singleOrderConfirmed) {
        this.singleOrderConfirmed = singleOrderConfirmed;
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
