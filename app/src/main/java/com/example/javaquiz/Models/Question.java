package com.example.javaquiz.Models;

/**
 * La classe Question représente un modèle pour une question de quiz en mode pratique.
 * Elle contient des informations telles que l'identifiant de la question,
 * l'énoncé, les options de réponse, la réponse correcte et une explication.
 */
public class Question {
    // Identifiant unique de la question
    private int id;

    // Énoncé de la question
    private String question;

    // Tableau contenant les différentes options de réponse
    private String[] options;

    // Réponse correcte à la question
    private String answer;

    // Explication de la réponse correcte
    private String explanation;

    /**
     * Constructeur de la classe Question.
     *
     * @param id L'identifiant unique de la question.
     * @param question L'énoncé de la question.
     * @param options Le tableau des options de réponse.
     * @param answer La réponse correcte.
     * @param explanation L'explication de la réponse correcte.
     */
    public Question(int id, String question, String[] options, String answer, String explanation) {
        this.id = id;
        this.question = question;
        this.options = options;
        this.answer = answer;
        this.explanation = explanation;
    }

    /**
     * Obtient l'identifiant de la question.
     *
     * @return L'identifiant unique de la question.
     */
    public int getId() {
        return id;
    }

    /**
     * Obtient l'énoncé de la question.
     *
     * @return L'énoncé de la question.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Obtient les options de réponse disponibles pour la question.
     *
     * @return Un tableau contenant les options de réponse.
     */
    public String[] getOptions() {
        return options;
    }

    /**
     * Obtient la réponse correcte à la question.
     *
     * @return La réponse correcte sous forme de chaîne de caractères.
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
}
