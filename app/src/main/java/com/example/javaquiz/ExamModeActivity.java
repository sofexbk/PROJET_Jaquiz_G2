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
    private int questionTimeLimit = 10;
    private CountDownTimer countDownTimer;
    private String selectedOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_mode);

        // Initialize views
        questionText = findViewById(R.id.questionText);
        timerText = findViewById(R.id.timerText);
        questionIndicator = findViewById(R.id.questionIndicator);
        optionsLayout = findViewById(R.id.optionsLayout);
        confirmButton = findViewById(R.id.confirmButton);

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

        confirmButton.setOnClickListener(v -> {
            if (selectedOption == null) return; // Do nothing if no option is selected
            if (countDownTimer != null) countDownTimer.cancel(); // Stop the timer
            checkAnswer(selectedOption);
        });
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= selectedQuestions.size()) {
            showFinalScore();
            return;
        }

        QuestionExam currentQuestion = selectedQuestions.get(currentQuestionIndex);
        questionText.setText(currentQuestion.getQuestionText());
        questionIndicator.setText(String.format("Question %d/%d", currentQuestionIndex + 1, selectedQuestions.size()));
        optionsLayout.removeAllViews();
        selectedOption = null; // Reset selected option

        List<String> options = new ArrayList<>(Arrays.asList(currentQuestion.getOptions()));
        Collections.shuffle(options);

        for (String option : options) {
            View optionCard = createOptionCard(option);
            optionsLayout.addView(optionCard);
        }

        startTimer(questionTimeLimit);
    }

    private View createOptionCard(String option) {
        // Create a CardView
        androidx.cardview.widget.CardView optionCard = new androidx.cardview.widget.CardView(this);
        optionCard.setCardElevation(6);
        optionCard.setRadius(16);
        optionCard.setUseCompatPadding(true);
        optionCard.setCardBackgroundColor(getResources().getColor(android.R.color.white)); // Default color

        // Create a TextView for the option text
        TextView optionText = new TextView(this);
        optionText.setText(option);
        optionText.setTextSize(18);
        optionText.setTextColor(getResources().getColor(android.R.color.black));
        optionText.setPadding(32, 32, 32, 32);

        // Add the TextView to the CardView
        optionCard.addView(optionText);

        // Set layout parameters for the CardView
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 16, 0, 16);
        optionCard.setLayoutParams(params);

        // Set click listener to make the card selectable
        optionCard.setOnClickListener(v -> {
            // Deselect all options
            for (int i = 0; i < optionsLayout.getChildCount(); i++) {
                optionsLayout.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
            }
            // Select the clicked card
            optionCard.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#dfa61a")));
            selectedOption = option; // Save the selected option
        });

        return optionCard;
    }


    private void checkAnswer(String selectedText) {
        QuestionExam currentQuestion = selectedQuestions.get(currentQuestionIndex);

        if (selectedText.equals(currentQuestion.getCorrectAnswer())) {
            correctAnswers++;
            score += currentQuestion.getCategory().equalsIgnoreCase("beginner") ? 1
                    : currentQuestion.getCategory().equalsIgnoreCase("intermediate") ? 3 : 5;
        }

        currentQuestionIndex++;
        displayQuestion();
    }

    private void showFinalScore() {
        long timeTaken = (System.currentTimeMillis() - startTime) / 1000;
        new AlertDialog.Builder(this)
                .setTitle("Quiz Finished")
                .setMessage(String.format("Final Score: %d\nCorrect Answers: %d/%d\nTime Taken: %d seconds",
                        score, correctAnswers, selectedQuestions.size(), timeTaken))
                .setPositiveButton("Return to Home", (dialog, which) -> {
                    Intent intent = new Intent(ExamModeActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();

        saveResultToDatabase("Exam", score, timeTaken, "General");
    }

    private void startTimer(int timeLimitInSeconds) {
        countDownTimer = new CountDownTimer(timeLimitInSeconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(String.format("Time Left: %ds", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                if (selectedOption != null) {
                    checkAnswer(selectedOption);
                } else {
                    checkAnswer(""); // Pass empty if no option was selected
                }
            }
        }.start();
    }

    private void saveResultToDatabase(String quizMode, int score, long timeTaken, String category) {
        QuizDatabaseHelper dbHelper = new QuizDatabaseHelper(this);
        dbHelper.saveQuizResult(quizMode, score, timeTaken, category);
    }
}