package com.example.javaquiz.Utils;

import com.example.javaquiz.Models.Question;
import com.example.javaquiz.Models.QuestionExam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONParserExam {

    public static List<QuestionExam> parseQuestions(String jsonString, String difficulty) {
        List<QuestionExam> questions = new ArrayList<>();

        try {
            // Parser le JSON
            JSONArray questionsArray = new JSONArray(jsonString);

            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject questionObject = questionsArray.getJSONObject(i);

                // Récupérer les informations nécessaires
                String questionText = questionObject.getString("question");
                JSONArray optionsArray = questionObject.getJSONArray("options");
                String[] options = new String[optionsArray.length()];
                for (int j = 0; j < optionsArray.length(); j++) {
                    options[j] = optionsArray.getString(j);
                }
                String correctAnswer = questionObject.getString("answer");
                String category = questionObject.getString("category");
                String difficultyLevel = questionObject.getString("difficulty");

                // Créer l'objet QuestionExam
                QuestionExam question = new QuestionExam(questionText, options, correctAnswer, category, difficultyLevel);

                // Ajouter à la liste
                if (difficulty == null || difficultyLevel.equalsIgnoreCase(difficulty)) {
                    questions.add(question);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return questions;
    }
}

