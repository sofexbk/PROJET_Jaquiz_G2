package com.example.javaquiz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.javaquiz.Models.QuestionExam;
import com.example.javaquiz.Utils.JSONParserExam;
import com.example.javaquiz.Utils.QuizDatabaseHelper;
import com.example.javaquiz.Utils.loadQuizData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Activité représentant le mode examen du quiz.
 * L'utilisateur répond aux questions avec une limite de temps par question.
 */
public class ExamModeActivity extends AppCompatActivity {

    private TextView questionText, timerText, questionIndicator;
    private LinearLayout optionsLayout;
    private Button confirmButton;
    private List<QuestionExam> questions;
    private List<QuestionExam> selectedQuestions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int correctAnswers = 0;
    private long startTime;
    private int questionTimeLimit = 10; // Temps limite par question en secondes
    private CountDownTimer countDownTimer;
    private String selectedOption = null; // Option sélectionnée par l'utilisateur

    /**
     * Méthode appelée lors de la création de l'activité.
     * Elle initialise les vues, charge les questions et commence à afficher les questions du quiz.
     *
     * @param savedInstanceState L'état précédent de l'activité, utilisé pour restaurer les données si nécessaire.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_mode);

        // Initialisation des vues
        questionText = findViewById(R.id.questionText);
        timerText = findViewById(R.id.timerText);
        questionIndicator = findViewById(R.id.questionIndicator);
        optionsLayout = findViewById(R.id.optionsLayout);
        confirmButton = findViewById(R.id.confirmButton);

        // Charger les questions depuis un fichier JSON
        String jsonString = loadQuizData.readJsonFromRaw(this, R.raw.data);
        if (jsonString != null) {
            questions = JSONParserExam.parseQuestions(jsonString);
        }

        // Mélanger les questions pour l'ordre aléatoire
        Collections.shuffle(questions);

        // Sélectionner les premières 15 questions ou moins si moins de 15 questions sont disponibles
        selectedQuestions = new ArrayList<>();
        int questionsToSelect = Math.min(15, questions.size());
        for (int i = 0; i < questionsToSelect; i++) {
            selectedQuestions.add(questions.get(i));
        }

        // Enregistrer l'heure de début
        startTime = System.currentTimeMillis();

        // Afficher la première question
        displayQuestion();

        // Configurer l'action du bouton de confirmation
        confirmButton.setOnClickListener(v -> {
            if (selectedOption == null) return; // Si aucune option n'est sélectionnée, ne rien faire
            if (countDownTimer != null) countDownTimer.cancel(); // Arrêter le timer
            checkAnswer(selectedOption); // Vérifier la réponse
        });
    }

    /**
     * Affiche la question actuelle et ses options.
     * Si toutes les questions ont été répondues, affiche le score final.
     */
    private void displayQuestion() {
        // Si toutes les questions ont été traitées, afficher le score final
        if (currentQuestionIndex >= selectedQuestions.size()) {
            showFinalScore();
            return;
        }

        // Récupérer la question actuelle
        QuestionExam currentQuestion = selectedQuestions.get(currentQuestionIndex);
        questionText.setText(currentQuestion.getQuestionText());
        questionIndicator.setText(String.format("Question %d/%d", currentQuestionIndex + 1, selectedQuestions.size()));

        // Réinitialiser l'affichage des options et l'option sélectionnée
        optionsLayout.removeAllViews();
        selectedOption = null;

        // Mélanger les options avant de les afficher
        List<String> options = new ArrayList<>(Arrays.asList(currentQuestion.getOptions()));
        Collections.shuffle(options);

        // Créer et afficher les cartes pour chaque option
        for (String option : options) {
            View optionCard = createOptionCard(option);
            optionsLayout.addView(optionCard);
        }

        // Démarrer le timer pour la question
        startTimer(questionTimeLimit);
    }

    /**
     * Crée une carte pour afficher une option de réponse.
     *
     * @param option L'option de réponse à afficher sur la carte.
     * @return Une vue représentant la carte de l'option.
     */
    private View createOptionCard(String option) {
        // Créer une carte pour l'option
        androidx.cardview.widget.CardView optionCard = new androidx.cardview.widget.CardView(this);
        optionCard.setCardElevation(6);
        optionCard.setRadius(16);
        optionCard.setUseCompatPadding(true);
        optionCard.setCardBackgroundColor(getResources().getColor(android.R.color.white)); // Couleur de fond par défaut

        // Créer un TextView pour afficher l'option
        TextView optionText = new TextView(this);
        optionText.setText(option);
        optionText.setTextSize(18);
        optionText.setTextColor(getResources().getColor(android.R.color.black));
        optionText.setPadding(32, 32, 32, 32);

        // Ajouter le TextView à la carte
        optionCard.addView(optionText);

        // Définir les paramètres de mise en page pour la carte
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 16, 0, 16);
        optionCard.setLayoutParams(params);

        // Ajouter un écouteur de clic pour sélectionner l'option
        optionCard.setOnClickListener(v -> {
            // Désélectionner toutes les options
            for (int i = 0; i < optionsLayout.getChildCount(); i++) {
                optionsLayout.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
            }
            // Sélectionner la carte sur laquelle l'utilisateur a cliqué
            optionCard.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#dfa61a")));
            selectedOption = option; // Enregistrer l'option sélectionnée
        });

        return optionCard;
    }

    /**
     * Vérifie si la réponse sélectionnée par l'utilisateur est correcte.
     * Incrémente le score et passe à la question suivante.
     *
     * @param selectedText L'option sélectionnée par l'utilisateur.
     */
    private void checkAnswer(String selectedText) {
        QuestionExam currentQuestion = selectedQuestions.get(currentQuestionIndex);

        // Vérifier si la réponse est correcte
        if (selectedText.equals(currentQuestion.getCorrectAnswer())) {
            correctAnswers++;
            // Ajouter des points en fonction de la catégorie de la question
            score += currentQuestion.getCategory().equalsIgnoreCase("beginner") ? 1
                    : currentQuestion.getCategory().equalsIgnoreCase("intermediate") ? 3 : 5;
        }

        // Passer à la question suivante
        currentQuestionIndex++;
        displayQuestion();
    }

    /**
     * Affiche le score final à la fin du quiz.
     * Permet à l'utilisateur de retourner à l'écran d'accueil.
     */
    private void showFinalScore() {
        long timeTaken = (System.currentTimeMillis() - startTime) / 1000;
        new AlertDialog.Builder(this)
                .setTitle("Quiz Terminé")
                .setMessage(String.format("Score Final: %d\nRéponses Correctes: %d/%d\nTemps Pris: %d secondes",
                        score, correctAnswers, selectedQuestions.size(), timeTaken))
                .setPositiveButton("Retour à l'accueil", (dialog, which) -> {
                    Intent intent = new Intent(ExamModeActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();

        // Sauvegarder le résultat dans la base de données
        saveResultToDatabase("Exam", score, timeTaken, "General");
    }

    /**
     * Démarre le timer pour la question en cours.
     * Affiche le temps restant à l'utilisateur et vérifie la réponse quand le temps est écoulé.
     *
     * @param timeLimitInSeconds Le temps limite pour cette question en secondes.
     */
    private void startTimer(int timeLimitInSeconds) {
        countDownTimer = new CountDownTimer(timeLimitInSeconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(String.format("Temps Restant: %ds", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                // Vérifier la réponse lorsque le temps est écoulé
                if (selectedOption != null) {
                    checkAnswer(selectedOption);
                } else {
                    checkAnswer(""); // Passer une chaîne vide si aucune option n'est sélectionnée
                }
            }
        }.start();
    }

    /**
     * Sauvegarde les résultats du quiz dans la base de données.
     *
     * @param quizMode Le mode de quiz (par exemple, "Exam").
     * @param score Le score final du quiz.
     * @param timeTaken Le temps pris pour terminer le quiz.
     * @param category La catégorie du quiz.
     */
    private void saveResultToDatabase(String quizMode, int score, long timeTaken, String category) {
        QuizDatabaseHelper dbHelper = new QuizDatabaseHelper(this);
        dbHelper.saveQuizResult(quizMode, score, timeTaken, category);
    }
}
