package com.example.javaquiz.Models;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe QuizQuestion représente une question de quiz en mode multijoueur.
 * Elle contient le texte de la question, les options de réponse,
 * la réponse correcte, une explication, et le nombre de points attribués.
 */
public class QuizQuestion {
    // Texte de la question
    private String question;

    // Liste des options de réponse
    private List<String> options;

    // Réponse correcte à la question
    private String answer;

    // Explication de la réponse correcte
    private String explanation;

    // Points attribués pour une réponse correcte
    private int points;

    /**
     * Constructeur de la classe QuizQuestion.
     *
     * @param question Le texte de la question.
     * @param optionsJson Une chaîne JSON représentant les options de réponse.
     * @param answer La réponse correcte.
     * @param explanation L'explication de la réponse correcte.
     * @param points Le nombre de points attribués pour une réponse correcte.
     */
    public QuizQuestion(String question, String optionsJson, String answer,
                        String explanation, int points) {
        this.question = question;
        this.answer = answer;
        this.explanation = explanation;
        this.points = points;
        this.options = new ArrayList<>();

        try {
            // Convertit la chaîne JSON en une liste d'options
            JSONArray optionsArray = new JSONArray(optionsJson);
            for (int i = 0; i < optionsArray.length(); i++) {
                this.options.add(optionsArray.getString(i));
            }
        } catch (JSONException e) {
            // Gestion des erreurs liées au parsing JSON
            e.printStackTrace();
        }
    }

    /**
     * Obtient le texte de la question.
     *
     * @return Le texte de la question.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Obtient les options de réponse pour la question.
     *
     * @return Une liste contenant les options de réponse.
     */
    public List<String> getOptions() {
        return options;
    }

    /**
     * Obtient la réponse correcte à la question.
     *
     * @return La réponse correcte.
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Obtient l'explication de la réponse correcte.
     *
     * @return L'explication de la réponse correcte.
     */
    public String getExplanation() {
        return explanation;
    }

    /**
     * Obtient le nombre de points attribués pour une réponse correcte.
     *
     * @return Le nombre de points attribués.
     */
    public int getPoints() {
        return points;
    }
}
