package com.example.javaquiz;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.javaquiz.Models.Question;
import com.example.javaquiz.Utils.JSONParser;
import com.example.javaquiz.Utils.QuizDatabaseHelper;
import com.example.javaquiz.Utils.loadQuizData;

import java.util.List;

/**
 * Cette activité gère le mode "Practice" du quiz, permettant à l'utilisateur de répondre
 * à des questions de quiz, de voir des explications et de suivre son score.
 */
public class PracticeModeActivity extends AppCompatActivity {

    private TextView questionText, explanationText, questionIndicator;
    private CardView explanationCard;
    private LinearLayout optionsLayout;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0; // Suivi du score
    private long startTime; // Suivi du temps de début

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_mode);

        // Initialisation des vues
        questionText = findViewById(R.id.questionText);
        explanationText = findViewById(R.id.explanationText);
        explanationCard = findViewById(R.id.explanationCard);
        optionsLayout = findViewById(R.id.optionsLayout);
        questionIndicator = findViewById(R.id.questionIndicator);
        startTime = SystemClock.elapsedRealtime(); // Temps de début du quiz

        // Chargement des questions
        String level = getIntent().getStringExtra("LEVEL");
        String jsonString = loadQuizData.readJsonFromRaw(this, R.raw.data);
        if (jsonString != null) {
            questions = JSONParser.parseQuestions(jsonString, level); // Parse les questions selon le niveau
        }

        // Affichage de la première question
        displayQuestion();
    }

    /**
     * Affiche la question actuelle et ses options.
     * Si toutes les questions ont été traitées, elle affichera un message de fin.
     */
    private void displayQuestion() {
        if (questions == null || questions.isEmpty()) {
            questionText.setText("No questions available.");
            explanationCard.setVisibility(View.GONE);
            optionsLayout.removeAllViews();
            return;
        }

        // Mise à jour de l'indicateur de question
        questionIndicator.setText(String.format("Question %d/%d", currentQuestionIndex + 1, questions.size()));

        // Récupération de la question actuelle
        Question currentQuestion = questions.get(currentQuestionIndex);
        questionText.setText(currentQuestion.getQuestion());

        // Réinitialiser les options précédentes
        optionsLayout.removeAllViews();

        // Création dynamique des options sous forme de cartes
        for (String option : currentQuestion.getOptions()) {
            CardView optionCard = new CardView(this);
            optionCard.setCardElevation(6);
            optionCard.setRadius(16);
            optionCard.setUseCompatPadding(true);
            optionCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));

            // Création du TextView pour l'option
            TextView optionText = new TextView(this);
            optionText.setText(option);
            optionText.setTextSize(18);
            optionText.setTextColor(getResources().getColor(android.R.color.black));
            optionText.setPadding(32, 32, 32, 32);

            // Ajouter le TextView à la CardView
            optionCard.addView(optionText);

            // Ajouter des marges à la CardView
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 16, 0, 0);
            optionCard.setLayoutParams(params);

            // Gérer le clic sur l'option
            optionCard.setOnClickListener(v -> checkAnswer(option, optionCard));

            // Ajouter la CardView à la disposition des options
            optionsLayout.addView(optionCard);
        }

        // Cacher l'explication par défaut
        explanationCard.setVisibility(View.GONE);
    }

    /**
     * Vérifie la réponse sélectionnée et met à jour le score et l'affichage.
     * @param selectedOption L'option sélectionnée par l'utilisateur.
     * @param selectedCard La CardView associée à l'option sélectionnée.
     */
    private void checkAnswer(String selectedOption, CardView selectedCard) {
        Question currentQuestion = questions.get(currentQuestionIndex);

        // Désactive les boutons d'option pendant que l'explication est affichée
        disableOptionButtons();

        // Vérifie si la réponse est correcte
        if (selectedOption.equals(currentQuestion.getAnswer())) {
            selectedCard.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));
            score++; // Incrémente le score
        } else {
            selectedCard.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));

            // Met en surbrillance la réponse correcte
            for (int i = 0; i < optionsLayout.getChildCount(); i++) {
                CardView card = (CardView) optionsLayout.getChildAt(i);
                TextView optionText = (TextView) card.getChildAt(0);
                if (optionText.getText().toString().equals(currentQuestion.getAnswer())) {
                    card.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));
                }
            }
        }

        explanationText.setText(currentQuestion.getExplanation());
        explanationCard.setVisibility(View.VISIBLE);

        // Délai et passage à la question suivante ou fin du quiz
        selectedCard.postDelayed(() -> {
            enableOptionButtons();
            if (currentQuestionIndex < questions.size() - 1) {
                currentQuestionIndex++;
                displayQuestion();
            } else {
                showFinalScore(); // Affiche le score final
            }
        }, 3000);
    }

    /**
     * Affiche un dialogue contenant le score final et le temps écoulé.
     * Permet à l'utilisateur de retourner à l'écran principal ou de redémarrer le quiz.
     */
    private void showFinalScore() {
        // Calcul du temps écoulé
        long elapsedTime = (SystemClock.elapsedRealtime() - startTime) / 1000; // En secondes

        // Affiche le score final dans une popup
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String selectedLevel = getIntent().getStringExtra("LEVEL");
        if (selectedLevel == null) {
            selectedLevel = "beginner"; // Valeur par défaut
        }
        builder.setTitle("Quiz Completed!")
                .setMessage(String.format("You scored %d/%d in %d seconds.", score, questions.size(), elapsedTime))
                .setPositiveButton("Return to Home", (dialog, which) -> {
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Restart Quiz", (dialog, which) -> {
                    currentQuestionIndex = 0;
                    score = 0;
                    startTime = SystemClock.elapsedRealtime();
                    displayQuestion();
                })
                .setCancelable(false)
                .show();

        // Sauvegarde du résultat dans la base de données
        saveResultToDatabase("Practice", score, elapsedTime, selectedLevel);
    }

    /**
     * Sauvegarde les résultats du quiz dans la base de données.
     * @param quizMode Le mode du quiz ("Practice").
     * @param score Le score obtenu dans le quiz.
     * @param timeTaken Le temps pris pour compléter le quiz, en secondes.
     * @param category La catégorie du quiz (par exemple, "beginner").
     */
    private void saveResultToDatabase(String quizMode, int score, long timeTaken, String category) {
        QuizDatabaseHelper dbHelper = new QuizDatabaseHelper(this);
        long result = dbHelper.saveQuizResult(quizMode, score, timeTaken, category);

        if (result != -1) {
            Log.d("PracticeModeActivity", "Score saved successfully - Category: " + category + ", Score: " + score);
        } else {
            Log.e("PracticeModeActivity", "Error saving score for category: " + category);
        }
    }

    /**
     * Désactive les boutons d'option pour éviter de cliquer pendant l'affichage de l'explication.
     */
    private void disableOptionButtons() {
        for (int i = 0; i < optionsLayout.getChildCount(); i++) {
            CardView card = (CardView) optionsLayout.getChildAt(i);
            card.setClickable(false); // Désactive la possibilité de cliquer sur les options
        }
    }

    /**
     * Réactive les boutons d'option après l'affichage de l'explication.
     */
    private void enableOptionButtons() {
        for (int i = 0; i < optionsLayout.getChildCount(); i++) {
            CardView card = (CardView) optionsLayout.getChildAt(i);
            card.setClickable(true); // Réactive la possibilité de cliquer sur les options
        }
    }
}
