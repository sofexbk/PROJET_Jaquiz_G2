package com.example.javaquiz.Utils;

import android.util.Log;

import com.example.javaquiz.Models.QuestionExam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * La classe JSONParserExam fournit des utilitaires pour parser des données JSON
 * et les convertir en objets {@link QuestionExam}.
 * Ce parser est adapté pour les questions de quiz en mode examen.
 */
public class JSONParserExam {
    private static final String TAG = "JSONParserExam";

    /**
     * Parse une chaîne JSON pour extraire une liste de questions d'examen.
     *
     * @param jsonString La chaîne JSON contenant les questions structurées par catégories.
     * @return Une liste d'objets {@link QuestionExam} contenant les questions extraites.
     */
    public static List<QuestionExam> parseQuestions(String jsonString) {
        // Liste pour stocker les questions extraites
        List<QuestionExam> questions = new ArrayList<>();

        try {
            // Crée un objet JSON à partir de la chaîne donnée
            JSONObject jsonObject = new JSONObject(jsonString);
            Log.d("QuizData", "Parsed JSON object: " + jsonObject.toString());

            // Accède à l'objet "categories" dans le JSON
            JSONObject categories = jsonObject.getJSONObject("categories");

            // Itère sur chaque clé représentant une catégorie de difficulté
            Iterator<String> keys = categories.keys();

            while (keys.hasNext()) {
                String difficulty = keys.next(); // Nom de la catégorie (exemple : beginner, intermediate, advanced)
                JSONArray categoryArray = categories.getJSONArray(difficulty);

                // Parcourt les questions de la catégorie
                for (int i = 0; i < categoryArray.length(); i++) {
                    JSONObject questionObject = categoryArray.getJSONObject(i);

                    // Extrait les données de la question
                    String questionText = questionObject.getString("question");
                    JSONArray optionsArray = questionObject.getJSONArray("options");

                    // Convertit les options JSON en tableau Java
                    String[] options = new String[optionsArray.length()];
                    for (int j = 0; j < optionsArray.length(); j++) {
                        options[j] = optionsArray.getString(j);
                    }

                    // Récupère la réponse correcte
                    String correctAnswer = questionObject.getString("answer");

                    // Crée un objet QuestionExam et l'ajoute à la liste
                    QuestionExam question = new QuestionExam(questionText, options, correctAnswer, difficulty);
                    questions.add(question);
                }
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Erreur d'argument : " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "Erreur lors du parsing du JSON", e);
        } catch (Exception e) {
            Log.e(TAG, "Erreur inattendue", e);
        }

        // Logue le nombre total de questions extraites
        Log.d(TAG, "Nombre total de questions extraites : " + questions.size());
        return questions;
    }
}