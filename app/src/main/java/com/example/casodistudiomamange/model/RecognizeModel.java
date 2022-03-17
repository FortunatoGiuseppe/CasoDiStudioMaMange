package com.example.casodistudiomamange.model;

public class RecognizeModel {

    String correctIT, correctEN;
    int img1;

    public RecognizeModel(String correctIT, String correctEN, int image1) {
        this.correctEN = correctEN;
        this.correctIT = correctIT;
        this.img1 = image1;
    }

    public String getCorrectIT() {
        return correctIT;
    }

    public void setCorrectIT(String correctIT) {
        this.correctIT = correctIT;
    }

    public String getCorrectEN() {
        return correctEN;
    }

    public void setCorrectEN(String correctEN) {
        this.correctEN = correctEN;
    }

    public int getImg1() {
        return img1;
    }

    public void setImg1(int img1) {
        this.img1 = img1;
    }
}
