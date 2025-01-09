package com.example.javaquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javaquiz.Models.Question;
import com.example.javaquiz.Utils.JSONParser;
import com.example.javaquiz.Utils.loadQuizData;

import java.util.List;

public class PracticeModeActivity extends AppCompatActivity {

    private TextView questionText, explanationText, scoreText;
    private RadioGroup optionsGroup;

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0; // Variable pour suivre le score de l'utilisateur
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_mode);

        // Initialisation des vues
        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        explanationText = findViewById(R.id.explanationText);
        scoreText = findViewById(R.id.scoreText); // TextView pour afficher le score

        // Initialiser la visibilité du score à GONE pour qu'il ne soit pas visible pendant le quiz
        scoreText.setVisibility(View.GONE);

        // Lire et analyser le fichier JSON avec le niveau sélectionné
        String selectedLevel = getIntent().getStringExtra("LEVEL");
        if (selectedLevel == null) {
            selectedLevel = "beginner";  // Défaut à "beginner" si aucun niveau n'est passé
        }

        String jsonString = loadQuizData.readJsonFromRaw(this, R.raw.data);
        if (jsonString != null) {
            questions = JSONParser.parseQuestions(jsonString, selectedLevel);  // Utiliser le niveau sélectionné pour charger les questions
        }

        displayQuestion();

        // Ajouter le gestionnaire d'événements pour les options sélectionnées
        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                RadioButton selectedOption = findViewById(checkedId);
                String selectedText = selectedOption.getText().toString();
                checkAnswer(selectedText);
            }
        });

        // Ajouter le gestionnaire d'événements pour le bouton de retour à l'accueil
        findViewById(R.id.btnReturnHome).setOnClickListener(v -> {
            // Revenir à la page d'accueil
            Intent intent = new Intent(PracticeModeActivity.this, SoloPlayActivity.class);
            startActivity(intent);
            finish(); // Fermer l'activité en cours pour éviter de revenir en arrière
        });
    }


    private void displayQuestion() {
        if (questions == null || questions.isEmpty()) {
            Toast.makeText(this, "Aucune question disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        Question currentQuestion = questions.get(currentQuestionIndex);
        questionText.setText(currentQuestion.getQuestion());
        optionsGroup.removeAllViews();

        for (String option : currentQuestion.getOptions()) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            radioButton.setId(View.generateViewId()); // Générer un ID unique pour chaque option
            optionsGroup.addView(radioButton);
        }

        explanationText.setVisibility(View.GONE);
        optionsGroup.clearCheck();
    }

    private void checkAnswer(String selectedText) {
        Question currentQuestion = questions.get(currentQuestionIndex);

        if (selectedText.equals(currentQuestion.getAnswer())) {
            score++; // Incrémenter le score pour chaque bonne réponse
            Toast.makeText(this, "Bonne réponse !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Mauvaise réponse.", Toast.LENGTH_SHORT).show();
        }

        explanationText.setText(currentQuestion.getExplanation());
        explanationText.setVisibility(View.VISIBLE);

        // Passer à la question suivante
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            optionsGroup.postDelayed(this::displayQuestion, 5000); // Attendre 2 secondes
        } else {
            // Fin du quiz
            Toast.makeText(this, "Quiz terminé", Toast.LENGTH_LONG).show();
            showFinalScore(); // Afficher le score final
        }
    }

    private void showFinalScore() {
        // Afficher le score final uniquement après la fin du quiz
        scoreText.setText("Score final: " + score);
        scoreText.setVisibility(View.VISIBLE);  // Rendre visible le score final uniquement après la fin du quiz
    }

}
