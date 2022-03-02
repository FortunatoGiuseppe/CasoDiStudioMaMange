package com.example.casodistudiomamange.model;

public class Profile {

    private String nomeProfilo;
    private String testoProva;
    private boolean expandable;

    public Profile() {
    }

    public String getTestoProva() {
        return testoProva;
    }

    public void setTestoProva(String testoProva) {
        this.testoProva = testoProva;
    }

    public Profile(String nomeProfilo, String testoProva) {
        this.nomeProfilo = nomeProfilo;
        this.expandable = false;
        this.testoProva = testoProva;
    }

    public String getNomeProfilo() {
        return nomeProfilo;
    }

    public void setNomeProfilo(String nomeProfilo) {
        this.nomeProfilo = nomeProfilo;
    }
    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }
}
