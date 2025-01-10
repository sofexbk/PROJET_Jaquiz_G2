package com.example.javaquiz;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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

public class ExamModeActivity extends AppCompatActivity {
    private TextView questionText, scoreText, timerText;
    private LinearLayout optionsLayout;
    private List<QuestionExam> questions;
    private List<QuestionExam> selectedQuestions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int correctAnswers = 0;
    private long startTime;
    private int questionTimeLimit = 10;
    private CountDownTimer countDownTimer;
    private boolean hasAnswered = false; // Track if the user has answered the current question

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_mode);

        questionText = findViewById(R.id.questionText);
        optionsLayout = findViewById(R.id.optionsLayout);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);

        scoreText.setVisibility(View.GONE);

        String jsonString = loadQuizData.readJsonFromRaw(this, R.raw.data);
        if (jsonString != null) {
            questions = JSONParserExam.parseQuestions(jsonString);
        }

        Collections.shuffle(questions);

        selectedQuestions = new ArrayList<>();
        int questionsToSelect = Math.min(15, questions.size());
        for (int i = 0; i < questionsToSelect; i++) {
            selectedQuestions.add(questions.get(i));
        }

        startTime = System.currentTimeMillis();
        displayQuestion();

        findViewById(R.id.btnReturnHome).setOnClickListener(v -> {
            Intent intent = new Intent(ExamModeActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= selectedQuestions.size()) {
            showFinalScore();
            return;
        }

        QuestionExam currentQuestion = selectedQuestions.get(currentQuestionIndex);
        questionText.setText(currentQuestion.getQuestionText());
        optionsLayout.removeAllViews();

        List<String> options = new ArrayList<>(Arrays.asList(currentQuestion.getOptions()));
        Collections.shuffle(options);

        for (String option : options) {
            Button optionButton = new Button(this);
            optionButton.setText(option);
            optionButton.setBackgroundColor(getResources().getColor(R.color.light_blue)); // Custom background color
            optionButton.setTextColor(getResources().getColor(android.R.color.white)); // White text
            optionButton.setTextSize(18);
            optionButton.setPadding(20, 20, 20, 20); // Increase padding for better spacing
            optionButton.setAllCaps(false); // Optionally disable text all caps

            // Add rounded corners to the button
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(20);
            drawable.setColor(getResources().getColor(R.color.light_blue)); // Set the background color
            optionButton.setBackground(drawable);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 16, 0, 16);
            optionButton.setLayoutParams(layoutParams);

            optionButton.setOnClickListener(v -> checkAnswer(option));
            optionsLayout.addView(optionButton);
        }

        startTimer(questionTimeLimit);
    }

    private void checkAnswer(final String selectedText) {
        if (hasAnswered) return; // Prevent multiple answers

        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to select this answer?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    hasAnswered = true; // Mark as answered

                    QuestionExam currentQuestion = selectedQuestions.get(currentQuestionIndex);
                    disableOptionButtons();

                    if (selectedText.equals(currentQuestion.getCorrectAnswer())) {
                        correctAnswers++;
                        // Attribuer les points selon la difficulté
                        switch (currentQuestion.getCategory().toLowerCase()) {
                            case "beginner":
                                score += 1;
                                break;
                            case "intermediate":
                                score += 3;
                                break;
                            case "advanced":
                                score += 5;
                                break;
                        }
                    }

                    if (countDownTimer != null) {
                        countDownTimer.cancel(); // Stop the timer when the user answers
                    }

                    new Handler().postDelayed(ExamModeActivity.this::moveToNextQuestion, 1000);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel(); // Do nothing, just close the dialog
                })
                .show();
    }

    private void moveToNextQuestion() {
        currentQuestionIndex++;
        hasAnswered = false; // Reset the answered flag
        displayQuestion();
    }

    private void showFinalScore() {
        long timeTaken = (System.currentTimeMillis() - startTime) / 1000;

        // Create a dialog to display the final score and a "Back to Home" button
        new AlertDialog.Builder(this)
                .setTitle("Quiz Finished")
                .setMessage(String.format("Score final: %d\nQuestions totales: %d\nRéponses correctes: %d",
                        score, selectedQuestions.size(), correctAnswers))
                .setPositiveButton("Retour à l'accueil", (dialog, which) -> {
                    Intent intent = new Intent(ExamModeActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                })
                .show();

        saveResultToDatabase("Exam", score, timeTaken, "General");
    }

    private void disableOptionButtons() {
        for (int i = 0; i < optionsLayout.getChildCount(); i++) {
            optionsLayout.getChildAt(i).setEnabled(false);
        }
    }

    private void startTimer(int timeLimitInSeconds) {
        countDownTimer = new CountDownTimer(timeLimitInSeconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Temps restant : " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                if (!hasAnswered) {
                    moveToNextQuestion(); // Only move to the next question if the user hasn't answered yet
                }
            }
        }.start();
    }

    private void saveResultToDatabase(String quizMode, int score, long timeTaken, String category) {
        QuizDatabaseHelper dbHelper = new QuizDatabaseHelper(this);
        dbHelper.saveQuizResult(quizMode, score, timeTaken, category);
    }
}
