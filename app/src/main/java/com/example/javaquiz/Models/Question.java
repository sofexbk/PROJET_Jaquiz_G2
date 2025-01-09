package com.example.javaquiz.Models;

public class Question {
    private int id;
    private String question;
    private String[] options;
    private String answer;
    private String explanation;

    public Question(int id, String question, String[] options, String answer, String explanation) {
        this.id = id;
        this.question = question;
        this.options = options;
        this.answer = answer;
        this.explanation = explanation;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }

    public String getAnswer() {
        return answer;
    }

    public String getExplanation() {
        return explanation;
    }
}

