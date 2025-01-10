package com.example.javaquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javaquiz.Models.Question;
import com.example.javaquiz.Utils.JSONParser;
import com.example.javaquiz.Utils.QuizDatabaseHelper;
import com.example.javaquiz.Utils.loadQuizData;

import java.util.List;

public class PracticeModeActivity extends AppCompatActivity {

    private TextView questionText, explanationText, scoreText;
    private LinearLayout optionsLayout;

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private long startTime; // Pour mesurer le temps total

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_mode);

        // Initialisation des vues
        questionText = findViewById(R.id.questionText);
        optionsLayout = findViewById(R.id.optionsLayout);
        explanationText = findViewById(R.id.explanationText);
        scoreText = findViewById(R.id.scoreText);

        // Masquer le score au début
        scoreText.setVisibility(View.GONE);

        // Charger les questions
        String selectedLevel = getIntent().getStringExtra("LEVEL");
        if (selectedLevel == null) {
            selectedLevel = "beginner";
        }

        String jsonString = loadQuizData.readJsonFromRaw(this, R.raw.data);
        if (jsonString != null) {
            questions = JSONParser.parseQuestions(jsonString, selectedLevel);
        }

        // Début du chronométrage
        startTime = System.currentTimeMillis();

        // Afficher la première question
        displayQuestion();

        // Gestion du bouton de retour à l'accueil
        findViewById(R.id.btnReturnHome).setOnClickListener(v -> {
            Intent intent = new Intent(PracticeModeActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void displayQuestion() {
        if (questions == null || questions.isEmpty()) {
            Toast.makeText(this, "Aucune question disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        Question currentQuestion = questions.get(currentQuestionIndex);
        questionText.setText(currentQuestion.getQuestion());
        optionsLayout.removeAllViews(); // Nettoyer les options précédentes

        // Ajouter les options comme boutons larges avec un fond blanc cassé
        for (String option : currentQuestion.getOptions()) {
            Button optionButton = new Button(this);
            optionButton.setText(option);

            // Appliquer un fond blanc cassé
            optionButton.setBackgroundColor(getResources().getColor(R.color.off_white));
            optionButton.setTextColor(getResources().getColor(android.R.color.black)); // Texte en noir
            optionButton.setTextSize(18);
            optionButton.setPadding(16, 16, 16, 16);

            // Ajouter un espacement entre les boutons
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 16, 0, 16); // Marges : haut et bas (16dp)
            optionButton.setLayoutParams(layoutParams);

            // Gérer le clic sur une option
            optionButton.setOnClickListener(v -> checkAnswer(option, optionButton));
            optionsLayout.addView(optionButton);
        }

        explanationText.setVisibility(View.GONE);
    }

    private void checkAnswer(String selectedText, Button selectedButton) {
        Question currentQuestion = questions.get(currentQuestionIndex);

        // Désactiver les boutons pendant que l'explication est affichée
        disableOptionButtons();

        // Vérification de la réponse
        if (selectedText.equals(currentQuestion.getAnswer())) {
            score++;
            selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark)); // Réponse correcte
        } else {
            selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark)); // Réponse incorrecte

            // Identifier la bonne réponse
            for (int i = 0; i < optionsLayout.getChildCount(); i++) {
                Button button = (Button) optionsLayout.getChildAt(i);
                if (button.getText().toString().equals(currentQuestion.getAnswer())) {
                    button.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                }
            }
        }

        explanationText.setText(currentQuestion.getExplanation());
        explanationText.setVisibility(View.VISIBLE);

        // Passer à la question suivante après un délai (5 secondes)
        selectedButton.postDelayed(() -> {
            // Réactiver les boutons avant d'afficher la prochaine question
            enableOptionButtons();
            if (currentQuestionIndex < questions.size() - 1) {
                currentQuestionIndex++;
                displayQuestion();
            } else {
                Toast.makeText(this, "Quiz terminé", Toast.LENGTH_LONG).show();
                showFinalScore();
            }
        }, 5000); // Délai de 5 secondes avant de passer à la question suivante
    }

    private void disableOptionButtons() {
        // Désactiver tous les boutons d'option
        for (int i = 0; i < optionsLayout.getChildCount(); i++) {
            Button button = (Button) optionsLayout.getChildAt(i);
            button.setEnabled(false);
        }
    }

    private void enableOptionButtons() {
        // Réactiver tous les boutons d'option
        for (int i = 0; i < optionsLayout.getChildCount(); i++) {
            Button button = (Button) optionsLayout.getChildAt(i);
            button.setEnabled(true);
        }
    }


    private void showFinalScore() {
        // Calculer le temps pris
        long endTime = System.currentTimeMillis();
        long timeTaken = (endTime - startTime) / 1000; // Temps en secondes

        scoreText.setText("Score final: " + score);
        scoreText.setVisibility(View.VISIBLE);

        // Enregistrer le résultat dans la base de données
        saveResultToDatabase("Practice", score, timeTaken, "General");

        // Afficher un message
      //  Toast.makeText(this, "Résultats enregistrés dans la base de données", Toast.LENGTH_SHORT).show();
    }

    private void saveResultToDatabase(String quizMode, int score, long timeTaken, String category) {
        QuizDatabaseHelper dbHelper = new QuizDatabaseHelper(this);
        dbHelper.saveQuizResult(quizMode, score, timeTaken, category);
    }
}
