package com.example.casodistudiomamange.model;

public class Question {

    private int question, option1, option2, option3;
    private int correctAnsNo, image;

    public Question(int question, int option1, int option2, int option3, int correctAnsNo, int image) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.correctAnsNo = correctAnsNo;
        this.image = image;
    }

    public int getQuestion() {
        return question;
    }

    public void setQuestion(int question) {
        this.question = question;
    }

    public int getOption1() {
        return option1;
    }

    public void setOption1(int option1) {
        this.option1 = option1;
    }

    public int getOption2() {
        return option2;
    }

    public void setOption2(int option2) {
        this.option2 = option2;
    }

    public int getOption3() {
        return option3;
    }

    public void setOption3(int option3) {
        this.option3 = option3;
    }

    public int getCorrectAnsNo() {
        return correctAnsNo;
    }

    public void setCorrectAnsNo(int correctAnsNo) {
        this.correctAnsNo = correctAnsNo;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
