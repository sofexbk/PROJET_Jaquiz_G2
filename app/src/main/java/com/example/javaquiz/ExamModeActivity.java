package com.example.javaquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javaquiz.Models.QuestionExam;
import com.example.javaquiz.Utils.JSONParser;
import com.example.javaquiz.Utils.JSONParserExam;
import com.example.javaquiz.Utils.QuizDatabaseHelper;
import com.example.javaquiz.Utils.loadQuizData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExamModeActivity extends AppCompatActivity {

    private TextView questionText, scoreText, timerText;
    private LinearLayout optionsLayout;

    private List<QuestionExam> questions;
    private List<QuestionExam> selectedQuestions; // Liste des questions sélectionnées aléatoirement
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int correctAnswers = 0; // Nombre de réponses correctes
    private long startTime; // Pour mesurer le temps total
    private int questionTimeLimit = 10; // Temps en secondes pour chaque question

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_mode);

        // Initialisation des vues
        questionText = findViewById(R.id.questionText);
        optionsLayout = findViewById(R.id.optionsLayout);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);  // TextView pour afficher le timer

        // Masquer le score au début
        scoreText.setVisibility(View.GONE);

        // Charger les questions
        String jsonString = loadQuizData.readJsonFromRaw(this, R.raw.data);
        if (jsonString != null) {
            questions = JSONParserExam.parseQuestions(jsonString, "beginner"); // Utilisez le niveau de difficulté ici si besoin
        }

        // Mélanger les questions de manière aléatoire
        Collections.shuffle(questions);

        // Sélectionner uniquement les 15 premières questions après avoir mélangé
        selectedQuestions = questions.subList(0, Math.min(15, questions.size()));

        // Démarrer le chronométrage
        startTime = System.currentTimeMillis();

        // Afficher la première question
        displayQuestion();

        // Gestion du bouton de retour à l'accueil
        findViewById(R.id.btnReturnHome).setOnClickListener(v -> {
            Intent intent = new Intent(ExamModeActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void displayQuestion() {
        if (selectedQuestions == null || selectedQuestions.isEmpty()) {
            Toast.makeText(this, "Aucune question disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        QuestionExam currentQuestion = selectedQuestions.get(currentQuestionIndex);
        questionText.setText(currentQuestion.getQuestionText());
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

        // Démarrer un compte à rebours pour cette question
        startTimer(questionTimeLimit);
    }

    private void startTimer(int timeLimitInSeconds) {
        final int finalTimeLimit = timeLimitInSeconds;

        new android.os.CountDownTimer(finalTimeLimit * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Temps restant : " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                timerText.setText("Temps écoulé!");
                moveToNextQuestion();
            }
        }.start();
    }

    private void checkAnswer(String selectedText, Button selectedButton) {
        QuestionExam currentQuestion = selectedQuestions.get(currentQuestionIndex);

        // Désactiver les boutons pendant que la réponse est vérifiée
        disableOptionButtons();

        // Vérification de la réponse
        if (selectedText.equals(currentQuestion.getCorrectAnswer())) {
            score += currentQuestion.getPoints(); // Ajouter les points selon la difficulté
            correctAnswers++; // Incrémenter le nombre de réponses correctes
            selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark)); // Réponse correcte
        } else {
            selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark)); // Réponse incorrecte
        }

        // Passer à la question suivante après un délai (5 secondes)
        selectedButton.postDelayed(() -> {
            moveToNextQuestion();
        }, 5000); // Délai de 5 secondes avant de passer à la question suivante
    }

    private void moveToNextQuestion() {
        if (currentQuestionIndex < selectedQuestions.size() - 1) {
            currentQuestionIndex++;
            displayQuestion();
        } else {
            showFinalScore();
        }
    }

    private void showFinalScore() {
        // Calculer le temps pris
        long endTime = System.currentTimeMillis();
        long timeTaken = (endTime - startTime) / 1000; // Temps en secondes

        scoreText.setText("Score final: " + score + "\nNombre de questions: " + selectedQuestions.size() +
                "\nRéponses correctes: " + correctAnswers);
        scoreText.setVisibility(View.VISIBLE);

        // Enregistrer le résultat dans la base de données
        saveResultToDatabase("Exam", score, timeTaken, "General");

        // Afficher un message
        Toast.makeText(this, "Quiz terminé. Résultats enregistrés.", Toast.LENGTH_LONG).show();
    }

    private void saveResultToDatabase(String quizMode, int score, long timeTaken, String category) {
        QuizDatabaseHelper dbHelper = new QuizDatabaseHelper(this);
        dbHelper.saveQuizResult(quizMode, score, timeTaken, category);
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
}
