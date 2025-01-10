package com.example.javaquiz.Models;

public class CategoryStats {
    private String categoryName;
    private int lastScore;
    private int bestScore;

    public CategoryStats(String categoryName, int lastScore, int bestScore) {
        this.categoryName = categoryName;
        this.lastScore = lastScore;
        this.bestScore = bestScore;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getLastScore() {
        return lastScore;
    }

    public int getBestScore() {
        return bestScore;
    }
}
