package com.example.javaquiz.Models;

public class QuestionExam {
    private String questionText;
    private String[] options;
    private String correctAnswer;
    private String category;
    private String difficulty;
    private int points;

    public QuestionExam(String questionText, String[] options, String correctAnswer, String category, String difficulty) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.category = category;
        this.difficulty = difficulty;
        this.points = getPointsForDifficulty(difficulty);
    }

    private int getPointsForDifficulty(String difficulty) {
        switch (difficulty) {
            case "beginner":
                return 1;
            case "intermediate":
                return 3;
            case "advanced":
                return 5;
            default:
                return 0;
        }
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

    public String getDifficulty() {
        return difficulty;
    }
}
