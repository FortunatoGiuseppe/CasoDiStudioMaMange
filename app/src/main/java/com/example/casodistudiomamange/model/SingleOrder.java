package com.example.casodistudiomamange.model;

public class SingleOrder {
    private String codiceordsin;
    private String data;
    private String username;

    public String getCodiceordsin() {
        return codiceordsin;
    }

    public void setCodiceordsin(String codiceordsin) {
        this.codiceordsin = codiceordsin;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public SingleOrder(String codiceordsin, String data, String username) {
        this.codiceordsin = codiceordsin;
        this.data = data;
        this.username = username;
    }
}
