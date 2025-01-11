package com.example.javaquiz;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.*;
import android.graphics.Color;
import com.example.javaquiz.Models.QuizQuestion;

public class GameActivity extends AppCompatActivity {
    private ArrayList<String> playerNames;
    private HashMap<String, Integer> playerScores;
    private int numPlayers;
    private int currentPlayerIndex = 0;
    private int currentTour = 0;
    private ArrayList<ArrayList<QuizQuestion>> playerQuestions;

    private TextView playerTurnText;
    private TextView questionText;
    private Button[] optionButtons;
    private TextView scoreText;
    private TextView tourNumberText;
    private TextView difficultyText;
    private TextView currentRoundInfo;

    private static final int TOURS_PER_PLAYER = 10;
    private static final int BEGINNER_POINTS = 1;
    private static final int INTERMEDIATE_POINTS = 3;
    private static final int ADVANCED_POINTS = 5;

    // Array to store the difficulty level for each tour
    private String[] tourDifficulties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initializeViews();
        retrieveGameData();
        initializeGame();
    }

    private void initializeViews() {
        playerTurnText = findViewById(R.id.player_turn_text);
        questionText = findViewById(R.id.question_text);
        scoreText = findViewById(R.id.score_text);
        tourNumberText = findViewById(R.id.tour_number_text);
        difficultyText = findViewById(R.id.difficulty_text);
        currentRoundInfo = findViewById(R.id.current_round_info);

        optionButtons = new Button[]{
                findViewById(R.id.option1_button),
                findViewById(R.id.option2_button),
                findViewById(R.id.option3_button),
                findViewById(R.id.option4_button)
        };

        for (Button button : optionButtons) {
            button.setOnClickListener(this::handleAnswerClick);
        }
    }

    private void retrieveGameData() {
        playerNames = getIntent().getStringArrayListExtra("PLAYER_NAMES");
        if (playerNames == null || playerNames.isEmpty()) {
            showError("Erreur: aucun joueur n'a été spécifié");
            return;
        }
        numPlayers = playerNames.size();

        playerScores = new HashMap<>();
        for (String player : playerNames) {
            playerScores.put(player, 0);
        }
    }

    private int getDifficultyPoints(String difficulty) {
        switch (difficulty) {
            case "beginner":
                return BEGINNER_POINTS;
            case "intermediate":
                return INTERMEDIATE_POINTS;
            case "advanced":
                return ADVANCED_POINTS;
            default:
                return 0;
        }
    }

    private JSONObject loadJsonData() {
        JSONObject jsonData = null;
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.data);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            reader.close();
            inputStream.close();

            jsonData = new JSONObject(stringBuilder.toString());

        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur de lecture du fichier JSON");
        } catch (JSONException e) {
            e.printStackTrace();
            showError("Erreur de format JSON");
        }

        return jsonData;
    }

    private void initializeGame() {
        if (playerNames == null || playerNames.isEmpty()) {
            return;
        }
        playerQuestions = new ArrayList<>();
        tourDifficulties = new String[TOURS_PER_PLAYER];
        loadQuestions();
        updateCurrentPlayerDisplay();
        displayCurrentQuestion();
    }

    private void loadQuestions() {
        try {
            JSONObject jsonData = loadJsonData();
            if (jsonData == null) {
                showError("Erreur: impossible de charger les questions");
                return;
            }

            String[] difficulties = {"beginner", "intermediate", "advanced"};
            Map<String, ArrayList<QuizQuestion>> questionsByDifficulty = new HashMap<>();

            // Initialize lists for each difficulty level
            for (String difficulty : difficulties) {
                questionsByDifficulty.put(difficulty, new ArrayList<>());
            }

            // Load and sort questions by difficulty
            for (String difficulty : difficulties) {
                JSONArray categoryQuestions = jsonData.getJSONObject("categories")
                        .getJSONArray(difficulty);

                int difficultyPoints = getDifficultyPoints(difficulty);

                for (int i = 0; i < categoryQuestions.length(); i++) {
                    JSONObject q = categoryQuestions.getJSONObject(i);
                    QuizQuestion question = new QuizQuestion(
                            q.getString("question"),
                            q.getJSONArray("options").toString(),
                            q.getString("answer"),
                            q.getString("explanation"),
                            difficultyPoints
                    );
                    questionsByDifficulty.get(difficulty).add(question);
                }
            }

            // Shuffle questions within each difficulty
            for (ArrayList<QuizQuestion> questions : questionsByDifficulty.values()) {
                Collections.shuffle(questions);
            }

            // Determine difficulty for each tour (same for all players)
            determineTourDifficulties();

            // Distribute questions to players ensuring same difficulty per tour
            for (int i = 0; i < numPlayers; i++) {
                ArrayList<QuizQuestion> playerQuestionSet = new ArrayList<>();

                for (int tour = 0; tour < TOURS_PER_PLAYER; tour++) {
                    String tourDifficulty = tourDifficulties[tour];
                    ArrayList<QuizQuestion> difficultyQuestions = questionsByDifficulty.get(tourDifficulty);

                    // Get question for this player from the appropriate difficulty pool
                    int questionIndex = i + (tour * numPlayers);
                    if (questionIndex < difficultyQuestions.size()) {
                        playerQuestionSet.add(difficultyQuestions.get(questionIndex));
                    } else {
                        // If we run out of questions, cycle back to the beginning
                        playerQuestionSet.add(difficultyQuestions.get(questionIndex % difficultyQuestions.size()));
                    }
                }

                playerQuestions.add(playerQuestionSet);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des questions: " + e.getMessage());
        }
    }

    private void determineTourDifficulties() {
        // Define distribution of difficulties across tours
        int beginnerTours = 4;  // 4 tours for beginner
        int intermediateTours = 3;  // 3 tours for intermediate
        int advancedTours = 3;  // 3 tours for advanced

        ArrayList<String> difficulties = new ArrayList<>();

        // Add difficulties according to distribution
        for (int i = 0; i < beginnerTours; i++) difficulties.add("beginner");
        for (int i = 0; i < intermediateTours; i++) difficulties.add("intermediate");
        for (int i = 0; i < advancedTours; i++) difficulties.add("advanced");

        // Shuffle the difficulties
        Collections.shuffle(difficulties);

        // Assign to tours
        for (int i = 0; i < TOURS_PER_PLAYER; i++) {
            tourDifficulties[i] = difficulties.get(i);
        }
    }

    private void displayCurrentQuestion() {
        if (currentTour < TOURS_PER_PLAYER) {
            QuizQuestion currentQuestion = playerQuestions.get(currentPlayerIndex).get(currentTour);

            // Update tour information
            tourNumberText.setText(String.format("Question %d/%d", currentTour + 1, TOURS_PER_PLAYER));
            currentRoundInfo.setText(String.format("Manche %d/%d",
                    (currentPlayerIndex + 1), numPlayers));

            // Display difficulty level
            String difficulty = "";
            switch (currentQuestion.getPoints()) {
                case BEGINNER_POINTS:
                    difficulty = "Débutant";
                    difficultyText.setTextColor(Color.GREEN);
                    break;
                case INTERMEDIATE_POINTS:
                    difficulty = "Intermédiaire";
                    difficultyText.setTextColor(Color.BLUE);
                    break;
                case ADVANCED_POINTS:
                    difficulty = "Avancé";
                    difficultyText.setTextColor(Color.RED);
                    break;
            }
            difficultyText.setText("Niveau: " + difficulty);

            // Display question and options
            questionText.setText(currentQuestion.getQuestion());
            List<String> options = currentQuestion.getOptions();
            for (int i = 0; i < optionButtons.length; i++) {
                optionButtons[i].setText(options.get(i));
                optionButtons[i].setEnabled(true);
                optionButtons[i].setBackgroundColor(getResources().getColor(android.R.color.background_light));
            }

            updateScoreDisplay();
        } else {
            showGameOver();
        }
    }

    private void handleAnswerClick(View view) {
        // Disable all buttons
        for (Button button : optionButtons) {
            button.setEnabled(false);
        }

        Button clickedButton = (Button) view;
        QuizQuestion currentQuestion = playerQuestions.get(currentPlayerIndex).get(currentTour);

        String selectedAnswer = clickedButton.getText().toString();
        boolean isCorrect = selectedAnswer.equals(currentQuestion.getAnswer());

        // Show answer
        if (isCorrect) {
            clickedButton.setBackgroundColor(Color.GREEN);
            String currentPlayer = playerNames.get(currentPlayerIndex);
            int currentScore = playerScores.get(currentPlayer);
            playerScores.put(currentPlayer, currentScore + currentQuestion.getPoints());
        } else {
            clickedButton.setBackgroundColor(Color.RED);
            for (Button button : optionButtons) {
                if (button.getText().toString().equals(currentQuestion.getAnswer())) {
                    button.setBackgroundColor(Color.GREEN);
                    break;
                }
            }
        }

        // Show explanation
        showExplanationDialog(currentQuestion.getExplanation(), isCorrect, () -> {
            if (currentPlayerIndex == numPlayers - 1) {
                currentTour++; // Move to next tour when all players have played
            }
            nextPlayer();

            if (currentTour < TOURS_PER_PLAYER) {
                displayCurrentQuestion();
            } else {
                showGameOver();
            }
        });
    }

    private void showExplanationDialog(String explanation, boolean isCorrect, Runnable onDismiss) {
        new AlertDialog.Builder(this)
                .setTitle(isCorrect ? "Correct !" : "Incorrect")
                .setMessage(explanation)
                .setPositiveButton("Suivant", (dialog, which) -> onDismiss.run())
                .setCancelable(false)
                .show();
    }

    private void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % numPlayers;
        updateCurrentPlayerDisplay();
    }

    private void updateCurrentPlayerDisplay() {
        String currentPlayer = playerNames.get(currentPlayerIndex);
        playerTurnText.setText("Tour de " + currentPlayer);
        updateScoreDisplay();
    }

    private void updateScoreDisplay() {
        StringBuilder scores = new StringBuilder("Scores:\n");
        for (String player : playerNames) {
            scores.append(player).append(": ")
                    .append(playerScores.get(player))
                    .append(" points\n");
        }
        scoreText.setText(scores.toString());
    }

    private void showGameOver() {
        List<Map.Entry<String, Integer>> sortedScores = new ArrayList<>(playerScores.entrySet());
        sortedScores.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        StringBuilder ranking = new StringBuilder("Classement final:\n\n");
        int rank = 1;
        for (Map.Entry<String, Integer> entry : sortedScores) {
            ranking.append(rank).append(". ")
                    .append(entry.getKey()).append(": ")
                    .append(entry.getValue()).append(" points\n");
            rank++;
        }

        showGameOverDialog(ranking.toString());
    }

    private void showGameOverDialog(String rankingMessage) {
        new AlertDialog.Builder(this)
                .setTitle("Fin de la partie")
                .setMessage(rankingMessage)
                .setPositiveButton("Nouvelle partie", (dialog, which) -> {
                    currentTour = 0;
                    currentPlayerIndex = 0;
                    for (String player : playerNames) {
                        playerScores.put(player, 0);
                    }
                    initializeGame();
                })
                .setNegativeButton("Quitter", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            new AlertDialog.Builder(this)
                    .setTitle("Erreur")
                    .setMessage(message)
                    .setPositiveButton("OK", (dialog, which) -> finish())
                    .setCancelable(false)
                    .show();
        });
    }
}