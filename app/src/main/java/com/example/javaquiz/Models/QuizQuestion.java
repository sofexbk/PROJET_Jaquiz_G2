package com.example.javaquiz.Models;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class QuizQuestion {
    private String question;
    private List<String> options;
    private String answer;
    private String explanation;
    private int points;

    public QuizQuestion(String question, String optionsJson, String answer,
                        String explanation, int points) {
        this.question = question;
        this.answer = answer;
        this.explanation = explanation;
        this.points = points;

        this.options = new ArrayList<>();
        try {
            JSONArray optionsArray = new JSONArray(optionsJson);
            for (int i = 0; i < optionsArray.length(); i++) {
                this.options.add(optionsArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Getters
    public String getQuestion() { return question; }
    public List<String> getOptions() { return options; }
    public String getAnswer() { return answer; }
    public String getExplanation() { return explanation; }
    public int getPoints() { return points; }
}