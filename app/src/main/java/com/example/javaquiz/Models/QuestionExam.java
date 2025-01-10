package com.example.javaquiz.Models;

public class QuestionExam {
    private String questionText;
    private String[] options;
    private String correctAnswer;
    private String category;
    private int points;

    public QuestionExam(String questionText, String[] options, String correctAnswer, String category) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.category = category;
    }

    public int getPoints() {
        return points;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String[] getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getCategory() {
        return category;
    }

}
