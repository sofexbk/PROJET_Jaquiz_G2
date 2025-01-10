package com.example.javaquiz.Utils;

import android.util.Log;

import com.example.javaquiz.Models.QuestionExam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JSONParserExam {

    public static List<QuestionExam> parseQuestions(String jsonString) {
        List<QuestionExam> questions = new ArrayList<>();

        try {
            // Créez un objet JSON à partir de la chaîne donnée
            JSONObject jsonObject = new JSONObject(jsonString);
            Log.d("QuizData", "Parsed JSON object: " + jsonObject.toString()); // Afficher l'objet JSON complet

            // Accéder à l'objet "categories"
            JSONObject categories = jsonObject.getJSONObject("categories");

            // Itérer sur chaque catégorie (beginner, intermediate, advanced)
            Iterator<String> keys = categories.keys();  // Use keys() instead of keySet()

            while (keys.hasNext()) {
                String difficulty = keys.next(); // Retrieve each difficulty category
                JSONArray categoryArray = categories.getJSONArray(difficulty);

                // Parcourir les questions de cette catégorie
                for (int i = 0; i < categoryArray.length(); i++) {
                    JSONObject questionObject = categoryArray.getJSONObject(i);

                    // Récupérer la question et ses options
                    String questionText = questionObject.getString("question");
                    JSONArray optionsArray = questionObject.getJSONArray("options");
                    String[] options = new String[optionsArray.length()];
                    for (int j = 0; j < optionsArray.length(); j++) {
                        options[j] = optionsArray.getString(j);
                    }

                    // Récupérer la réponse correcte
                    String correctAnswer = questionObject.getString("answer");


                    // Créer l'objet QuestionExam
                    QuestionExam question = new QuestionExam(questionText, options, correctAnswer, difficulty);
                    questions.add(question);
                }
            }
        } catch (JSONException e) {
            Log.e("QuizData", "Erreur lors du parsing du JSON", e);
        }

        Log.d("QuizData", "Total questions parsed: " + questions.size()); // Afficher le nombre de questions lues
        return questions;
    }
}
