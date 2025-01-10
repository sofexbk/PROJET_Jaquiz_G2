package com.example.javaquiz.Utils;

import com.example.javaquiz.Models.Question;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONParser {

    public static List<Question> parseQuestions(String jsonString, String category) {
        List<Question> questions = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(jsonString);
            JSONObject categories = root.getJSONObject("categories");
            JSONArray categoryArray = categories.getJSONArray(category);

            for (int i = 0; i < categoryArray.length(); i++) {
                JSONObject questionObject = categoryArray.getJSONObject(i);

                int id = questionObject.getInt("id");
                String questionText = questionObject.getString("question");
                JSONArray optionsArray = questionObject.getJSONArray("options");
                String[] options = new String[optionsArray.length()];

                for (int j = 0; j < optionsArray.length(); j++) {
                    options[j] = optionsArray.getString(j);
                }

                String answer = questionObject.getString("answer");
                String explanation = questionObject.getString("explanation");

                Question question = new Question(id,questionText, options, answer, explanation);
                questions.add(question);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return questions;
    }
}
