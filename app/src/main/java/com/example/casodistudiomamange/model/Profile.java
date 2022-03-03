package com.example.casodistudiomamange.model;

import java.util.ArrayList;

public class Profile {

    private String nomeProfilo;

    private boolean expandable;
    private ArrayList<SoPlate> soPlates;

    public Profile() {
    }

    public Profile(String nomeProfilo) {
        this.nomeProfilo = nomeProfilo;
    }

    public Profile(String nomeProfilo, ArrayList<SoPlate> soPlates) {
        this.nomeProfilo = nomeProfilo;
        this.expandable = false;

        this.soPlates = soPlates;
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

    public ArrayList<SoPlate> getSoPlates() {
        return soPlates;
    }

    public void setSoPlates(ArrayList<SoPlate> soPlates) {
        this.soPlates = soPlates;
    }
}
