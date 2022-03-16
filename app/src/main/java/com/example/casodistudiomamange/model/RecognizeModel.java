package com.example.casodistudiomamange.model;

public class RecognizeModel {
    private String question,correctAns;
    int img1;

    public RecognizeModel(String correctAns, int image1) {
        this.correctAns = correctAns;
        this.img1 = image1;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(String correctAns) {
        this.correctAns = correctAns;
    }

    public int getImg1() {
        return img1;
    }

    public void setImg1(int img1) {
        this.img1 = img1;
    }
}
