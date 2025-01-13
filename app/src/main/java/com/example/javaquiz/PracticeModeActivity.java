package com.example.javaquiz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.javaquiz.Models.Question;
import com.example.javaquiz.Utils.JSONParser;
import com.example.javaquiz.Utils.loadQuizData;
import java.util.List;

public class PracticeModeActivity extends AppCompatActivity {

    private TextView questionText, explanationText;
    private CardView explanationCard;
    private LinearLayout optionsLayout;
    private List<Question> questions;
    private int currentQuestionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_mode);

        // Initialize views
        questionText = findViewById(R.id.questionText);
        explanationText = findViewById(R.id.explanationText);
        explanationCard = findViewById(R.id.explanationCard);
        optionsLayout = findViewById(R.id.optionsLayout);
        Button btnReturnHome = findViewById(R.id.btnReturnHome);

        // Load questions
        String level = getIntent().getStringExtra("LEVEL");
        String jsonString = loadQuizData.readJsonFromRaw(this, R.raw.data);
        if (jsonString != null) {
            questions = JSONParser.parseQuestions(jsonString, level);
        }

        // Display the first question
        displayQuestion();

        // Handle return to home
        btnReturnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }

    private void displayQuestion() {
        if (questions == null || questions.isEmpty()) {
            questionText.setText("No questions available.");
            explanationCard.setVisibility(View.GONE);
            optionsLayout.removeAllViews();
            return;
        }

        // Update question indicator
        TextView questionIndicator = findViewById(R.id.questionIndicator);
        questionIndicator.setText(String.format("Question %d/%d", currentQuestionIndex + 1, questions.size()));

        // Get the current question
        Question currentQuestion = questions.get(currentQuestionIndex);
        questionText.setText(currentQuestion.getQuestion());

        // Clear previous options
        optionsLayout.removeAllViews();

        // Dynamically create options as cards
        for (String option : currentQuestion.getOptions()) {
            // CardView for option
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



    private void checkAnswer(String selectedOption, CardView selectedButton) {
        Question currentQuestion = questions.get(currentQuestionIndex);

        if (selectedOption.equals(currentQuestion.getAnswer())) {
            selectedButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));
        } else {
            selectedButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
        }

        explanationText.setText(currentQuestion.getExplanation());
        explanationCard.setVisibility(View.VISIBLE);

        // Move to the next question after a delay
        selectedButton.postDelayed(() -> {
            if (currentQuestionIndex < questions.size() - 1) {
                currentQuestionIndex++;
                displayQuestion();
            } else {
                // Quiz finished
                questionText.setText("Quiz completed!");
                optionsLayout.removeAllViews();
            }
        }, 3500); // Delay of 3 seconds
    }
}
