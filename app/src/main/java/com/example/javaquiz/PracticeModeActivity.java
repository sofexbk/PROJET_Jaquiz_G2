package com.example.javaquiz;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
 * Activité gérant le mode de pratique du quiz où les utilisateurs peuvent répondre aux questions.
 * Suivi du score, du temps et affichage des explications pour chaque réponse.
 */
public class PracticeModeActivity extends AppCompatActivity {

    private TextView questionText, explanationText, questionIndicator;
    private CardView explanationCard;
    private Button btnReturnHome;
    private LinearLayout optionsLayout;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0; // Suivi du score
    private long startTime; // Suivi du temps de départ

    /**
     * Appelée lors de la création de l'activité.
     * Initialise les vues et démarre le quiz en mode pratique.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_mode);

        // Initialisation des vues
        btnReturnHome = findViewById(R.id.btnReturnHome);
        questionText = findViewById(R.id.questionText);
        explanationText = findViewById(R.id.explanationText);
        explanationCard = findViewById(R.id.explanationCard);
        optionsLayout = findViewById(R.id.optionsLayout);
        questionIndicator = findViewById(R.id.questionIndicator);
        startTime = SystemClock.elapsedRealtime(); // Temps de départ

        // Chargement des questions en fonction du niveau sélectionné
        String level = getIntent().getStringExtra("LEVEL");
        String jsonString = loadQuizData.readJsonFromRaw(this, R.raw.data);
        if (jsonString != null) {
            questions = JSONParser.parseQuestions(jsonString, level);
        }

        // Afficher la première question
        displayQuestion();
    }

    /**
     * Affiche la question actuelle et les options.
     * Met à jour l'indicateur de question et crée dynamiquement des cartes d'options.
     */
    private void displayQuestion() {
        if (questions == null || questions.isEmpty()) {
            questionText.setText("Aucune question disponible.");
            explanationCard.setVisibility(View.GONE);
            optionsLayout.removeAllViews();
            return;
        }

        // Mise à jour de l'indicateur de question
        questionIndicator.setText(String.format("Question %d/%d", currentQuestionIndex + 1, questions.size()));

        // Récupération de la question actuelle
        Question currentQuestion = questions.get(currentQuestionIndex);
        questionText.setText(currentQuestion.getQuestion());

        // Suppression des anciennes options
        optionsLayout.removeAllViews();

        // Création dynamique des options sous forme de cartes
        for (String option : currentQuestion.getOptions()) {
            androidx.cardview.widget.CardView optionCard = new androidx.cardview.widget.CardView(this);
            optionCard.setCardElevation(6);
            optionCard.setRadius(16);
            optionCard.setUseCompatPadding(true);
            optionCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));

            // TextView pour le texte de l'option
            TextView optionText = new TextView(this);
            optionText.setText(option);
            optionText.setTextSize(18);
            optionText.setTextColor(getResources().getColor(android.R.color.black));
            optionText.setPadding(32, 32, 32, 32);

            // Ajout de TextView à CardView
            optionCard.addView(optionText);

            // Ajout des marges à CardView
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 16, 0, 0);
            optionCard.setLayoutParams(params);

            // Gestion du clic sur l'option
            optionCard.setOnClickListener(v -> checkAnswer(option, optionCard));

            // Ajout de CardView à la disposition des options
            optionsLayout.addView(optionCard);
        }

        // Masquer l'explication par défaut
        explanationCard.setVisibility(View.GONE);
    }

    /**
     * Vérifie si l'option sélectionnée est correcte et met à jour l'interface utilisateur.
     * Affiche également l'explication après la sélection de la réponse.
     *
     * @param selectedOption L'option sélectionnée par l'utilisateur
     * @param selectedCard   La carte correspondant à l'option sélectionnée
     */
    private void checkAnswer(String selectedOption, CardView selectedCard) {
        Question currentQuestion = questions.get(currentQuestionIndex);

        // Désactiver les boutons d'option pendant l'affichage de l'explication
        disableOptionButtons();

        // Mettre en surbrillance la réponse correcte ou incorrecte
        if (selectedOption.equals(currentQuestion.getAnswer())) {
            selectedCard.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));
            score++; // Incrémenter le score
        } else {
            selectedCard.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));

            // Mettre en surbrillance la réponse correcte
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
                showFinalScore(); // Afficher la popup du score
            }
        }, 3000);
    }

    /**
     * Affiche le score final après la fin du quiz.
     */
    private void showFinalScore() {
        // Calcul du temps écoulé
        long elapsedTime = (SystemClock.elapsedRealtime() - startTime) / 1000; // En secondes

        // Afficher le score final dans une popup
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String selectedLevel = getIntent().getStringExtra("LEVEL");
        if (selectedLevel == null) {
            selectedLevel = "beginner"; // Valeur par défaut
        }
        builder.setTitle("Quiz terminé !")
                .setMessage(String.format("Vous avez obtenu %d/%d en %d secondes.", score, questions.size(), elapsedTime))
                .setPositiveButton("Retour à l'accueil", (dialog, which) -> {
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Recommencer le quiz", (dialog, which) -> {
                    currentQuestionIndex = 0;
                    score = 0;
                    startTime = SystemClock.elapsedRealtime();
                    displayQuestion();
                })
                .setCancelable(false)
                .show();
        saveResultToDatabase("Practice", score, elapsedTime, selectedLevel);
    }

    /**
     * Sauvegarde le résultat du quiz dans la base de données.
     *
     * @param quizMode   Le mode du quiz (par exemple, "Practice")
     * @param score      Le score obtenu par l'utilisateur
     * @param timeTaken  Le temps pris pour compléter le quiz
     * @param category   La catégorie du quiz
     */
    private void saveResultToDatabase(String quizMode, int score, long timeTaken, String category) {
        QuizDatabaseHelper dbHelper = new QuizDatabaseHelper(this);
        long result = dbHelper.saveQuizResult(quizMode, score, timeTaken, category);

        if (result != -1) {
            // Log pour débogage
            Log.d("PracticeModeActivity", "Score enregistré avec succès - Catégorie: " + category + ", Score: " + score);
        } else {
            Log.e("PracticeModeActivity", "Erreur lors de l'enregistrement du score pour la catégorie: " + category);
        }
    }

    /**
     * Désactive tous les boutons d'option (les CardViews).
     */
    private void disableOptionButtons() {
        for (int i = 0; i < optionsLayout.getChildCount(); i++) {
            CardView card = (CardView) optionsLayout.getChildAt(i);
            card.setClickable(false);  // Désactiver la cliquabilité des cartes d'option
        }
    }

    /**
     * Active tous les boutons d'option (les CardViews).
     */
    private void enableOptionButtons() {
        for (int i = 0; i < optionsLayout.getChildCount(); i++) {
            CardView card = (CardView) optionsLayout.getChildAt(i);
            card.setClickable(true);  // Réactiver la cliquabilité des cartes d'option
        }
    }

    /**
     * Navigue vers l'écran d'accueil lorsque le bouton de retour est cliqué.
     */
    public void returnHome(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
