package com.example.casodistudiomamange.model;

public class PlateInOrder {
    private String nome;
    private long quantita;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getQuantita() {
        return quantita;
    }

    public void setQuantita(long quantita) {
        this.quantita = quantita;
    }

    public PlateInOrder(String nome, long quantita) {
        this.nome = nome;
        this.quantita = quantita;
    }
}
