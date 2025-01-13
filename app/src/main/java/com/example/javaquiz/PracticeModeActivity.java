package com.example.javaquiz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.javaquiz.Models.Question;
import com.example.javaquiz.Utils.JSONParser;
import com.example.javaquiz.Utils.loadQuizData;

import java.util.List;

public class PracticeModeActivity extends AppCompatActivity {

    private TextView questionText, explanationText, questionIndicator;
    private CardView explanationCard;
    private LinearLayout optionsLayout;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0; // Track the score
    private long startTime; // Track the start time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_mode);

        // Initialize views
        questionText = findViewById(R.id.questionText);
        explanationText = findViewById(R.id.explanationText);
        explanationCard = findViewById(R.id.explanationCard);
        optionsLayout = findViewById(R.id.optionsLayout);
        questionIndicator = findViewById(R.id.questionIndicator);
        startTime = SystemClock.elapsedRealtime(); // Start time

        // Load questions
        String level = getIntent().getStringExtra("LEVEL");
        String jsonString = loadQuizData.readJsonFromRaw(this, R.raw.data);
        if (jsonString != null) {
            questions = JSONParser.parseQuestions(jsonString, level);
        }

        // Display the first question
        displayQuestion();
    }

    private void displayQuestion() {
        if (questions == null || questions.isEmpty()) {
            questionText.setText("No questions available.");
            explanationCard.setVisibility(View.GONE);
            optionsLayout.removeAllViews();
            return;
        }

        // Update question indicator
        questionIndicator.setText(String.format("Question %d/%d", currentQuestionIndex + 1, questions.size()));

        // Get the current question
        Question currentQuestion = questions.get(currentQuestionIndex);
        questionText.setText(currentQuestion.getQuestion());

        // Clear previous options
        optionsLayout.removeAllViews();

        // Dynamically create options as cards
        for (String option : currentQuestion.getOptions()) {
            androidx.cardview.widget.CardView optionCard = new androidx.cardview.widget.CardView(this);
            optionCard.setCardElevation(6);
            optionCard.setRadius(16);
            optionCard.setUseCompatPadding(true);
            optionCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));

            // TextView for option text
            TextView optionText = new TextView(this);
            optionText.setText(option);
            optionText.setTextSize(18);
            optionText.setTextColor(getResources().getColor(android.R.color.black));
            optionText.setPadding(32, 32, 32, 32);

            // Add TextView to CardView
            optionCard.addView(optionText);

            // Add margins to CardView
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 16, 0, 0);
            optionCard.setLayoutParams(params);

            // Handle option click
            optionCard.setOnClickListener(v -> checkAnswer(option, optionCard));

            // Add CardView to options layout
            optionsLayout.addView(optionCard);
        }

        // Hide explanation by default
        explanationCard.setVisibility(View.GONE);
    }

    private void checkAnswer(String selectedOption, CardView selectedCard) {
        Question currentQuestion = questions.get(currentQuestionIndex);

        // Highlight correct or incorrect answer
        if (selectedOption.equals(currentQuestion.getAnswer())) {
            selectedCard.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));
            score++; // Increment score
        } else {
            selectedCard.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));

            // Highlight correct answer
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

        // Delay and move to next question or finish quiz
        selectedCard.postDelayed(() -> {
            if (currentQuestionIndex < questions.size() - 1) {
                currentQuestionIndex++;
                displayQuestion();
            } else {
                showFinalScore(); // Show score popup
            }
        }, 3000);
    }

    private void showFinalScore() {
        // Calculate elapsed time
        long elapsedTime = (SystemClock.elapsedRealtime() - startTime) / 1000; // Seconds

        // Show final score in a popup
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
    }
}
