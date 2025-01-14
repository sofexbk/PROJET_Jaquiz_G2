package com.example.javaquiz.Models;

/**
 * La classe QuestionExam représente une question de quiz en mode examen.
 * Elle contient le texte de la question, les options de réponse,
 * la réponse correcte et la catégorie à laquelle la question appartient.
 */
public class QuestionExam {
    // Texte de la question
    private String questionText;

    // Tableau contenant les différentes options de réponse
    private String[] options;

    // Réponse correcte à la question
    private String correctAnswer;

    // Catégorie de la question (exemple : "Beginner")
    private String category;

    /**
     * Constructeur de la classe QuestionExam.
     *
     * @param questionText Le texte de la question.
     * @param options Un tableau contenant les options de réponse.
     * @param correctAnswer La réponse correcte.
     * @param category La catégorie de la question.
     */
    public QuestionExam(String questionText, String[] options, String correctAnswer, String category) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.category = category;
    }

    /**
     * Obtient le texte de la question.
     *
     * @return Le texte de la question.
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     * Obtient les options de réponse pour la question.
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
    public String getCorrectAnswer() {
        return correctAnswer;
    }

    /**
     * Obtient la catégorie de la question.
     *
     * @return La catégorie de la question.
     */
    public String getCategory() {
        return category;
    }
}
