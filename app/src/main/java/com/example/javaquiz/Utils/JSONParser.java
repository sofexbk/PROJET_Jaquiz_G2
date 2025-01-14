package com.example.javaquiz.Utils;

import com.example.javaquiz.Models.Question;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe JSONParser contient des méthodes utilitaires pour parser des données JSON
 * et les convertir en objets du modèle {@link Question}.
 */
public class JSONParser {

    /**
     * Parse une chaîne JSON et extrait les questions associées à une catégorie spécifique.
     *
     * @param jsonString La chaîne JSON contenant les questions classées par catégorie.
     * @param category La catégorie pour laquelle les questions doivent être extraites.
     * @return Une liste d'objets {@link Question} correspondant à la catégorie donnée.
     */
    public static List<Question> parseQuestions(String jsonString, String category) {
        // Liste pour stocker les questions extraites
        List<Question> questions = new ArrayList<>();

        try {
            // Convertit la chaîne JSON en un objet JSON
            JSONObject root = new JSONObject(jsonString);

            // Accède à l'objet "categories" dans le JSON
            JSONObject categories = root.getJSONObject("categories");

            // Récupère le tableau des questions pour la catégorie spécifiée
            JSONArray categoryArray = categories.getJSONArray(category);

            // Parcourt chaque question dans le tableau de la catégorie
            for (int i = 0; i < categoryArray.length(); i++) {
                JSONObject questionObject = categoryArray.getJSONObject(i);

                // Extrait les informations de la question
                int id = questionObject.getInt("id");
                String questionText = questionObject.getString("question");

                // Extrait et convertit le tableau JSON des options en tableau Java
                JSONArray optionsArray = questionObject.getJSONArray("options");
                String[] options = new String[optionsArray.length()];
                for (int j = 0; j < optionsArray.length(); j++) {
                    options[j] = optionsArray.getString(j);
                }

                // Extrait la réponse correcte et l'explication
                String answer = questionObject.getString("answer");
                String explanation = questionObject.getString("explanation");

                // Crée un objet Question et l'ajoute à la liste
                Question question = new Question(id, questionText, options, answer, explanation);
                questions.add(question);
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Erreur : " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("Erreur de format JSON : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Une erreur inattendue s'est produite : " + e.getMessage());
        }

        // Retourne la liste des questions extraites
        return questions;
    }
}