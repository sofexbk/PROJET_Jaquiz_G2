package com.example.javaquiz;

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

    private TextView questionText, explanationText;
    private RadioGroup optionsGroup;

    private List<Question> questions;
    private int currentQuestionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_mode);

        // Get the selected level from the Intent
        String selectedLevel = getIntent().getStringExtra("LEVEL");
        if (selectedLevel == null) {
            selectedLevel = "beginner";  // Default to "beginner" if no level is passed
        }

        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        explanationText = findViewById(R.id.explanationText);

        // Read and parse the JSON file with the selected level
        String jsonString = loadQuizData.readJsonFromRaw(this, R.raw.data);  // Assuming your JSON file is located in raw/data.json
        if (jsonString != null) {
            questions = JSONParser.parseQuestions(jsonString, selectedLevel);  // Use the selected level to load questions
        }

        displayQuestion();

        // Add listener for changes in selected options
        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                RadioButton selectedOption = findViewById(checkedId);
                String selectedText = selectedOption.getText().toString();
                checkAnswer(selectedText);
            }
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
            radioButton.setId(View.generateViewId()); // Generate a unique ID for each option
            optionsGroup.addView(radioButton);
        }

        explanationText.setVisibility(View.GONE);
        optionsGroup.clearCheck();
    }

    private void checkAnswer(String selectedText) {
        Question currentQuestion = questions.get(currentQuestionIndex);

        if (selectedText.equals(currentQuestion.getAnswer())) {
            Toast.makeText(this, "Bonne réponse !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Mauvaise réponse.", Toast.LENGTH_SHORT).show();
        }

        explanationText.setText(currentQuestion.getExplanation());
        explanationText.setVisibility(View.VISIBLE);

        // Move to the next question after a delay
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            optionsGroup.postDelayed(this::displayQuestion, 2000); // Wait 2 seconds
        } else {
            Toast.makeText(this, "Quiz terminé", Toast.LENGTH_LONG).show();
        }
    }
}
