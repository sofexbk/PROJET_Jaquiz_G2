package com.example.javaquiz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javaquiz.Models.CategoryStats;
import com.example.javaquiz.Utils.CategoryStatsAdapter;
import com.example.javaquiz.Utils.QuizDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ProgressionActivity extends AppCompatActivity {

    private TextView scoreText, bestScoreText, categoryProgressText;
    private QuizDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progression);

        dbHelper = new QuizDatabaseHelper(this);

        // Charger les résultats du quiz
        loadUserStats();

        // Configuration du bouton "Retour"
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Naviguer vers HomeActivity
            Intent intent = new Intent(ProgressionActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
    private void loadUserStats() {
        Cursor cursor = dbHelper.getCategoryStats(); // Assurez-vous que cette méthode renvoie les résultats corrects
        List<CategoryStats> categoryStatsList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
                @SuppressLint("Range") int bestScore = cursor.getInt(cursor.getColumnIndex("best_score"));
                @SuppressLint("Range") int lastScore = cursor.getInt(cursor.getColumnIndex("last_score"));

                // Ajout des statistiques à la liste
                categoryStatsList.add(new CategoryStats(category, lastScore, bestScore));
            } while (cursor.moveToNext());
        }

        // Configure le RecyclerView
        RecyclerView recyclerView = findViewById(R.id.statisticsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CategoryStatsAdapter(categoryStatsList));
    }

//    private void loadUserStats() {
//        Cursor cursor = dbHelper.getQuizResults();
//        if (cursor != null && cursor.moveToFirst()) {
//            int totalScore = 0;
//            int bestScore = 0;
//            int categoryCount = 0; // Compter le nombre de catégories
//            int completedCategories = 0; // Nombre de catégories complétées
//
//            // Calculer le score total, le meilleur score et la progression par catégorie
//            do {
//                @SuppressLint("Range") int score = cursor.getInt(cursor.getColumnIndex("score"));
//                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
//                totalScore += score;
//                if (score > bestScore) {
//                    bestScore = score;
//                }
//
//                // Calculer les catégories complétées
//                if (score > 0) {
//                    completedCategories++;
//                }
//                categoryCount++;
//            } while (cursor.moveToNext());
//
//            // Afficher les informations dans les TextView
//            scoreText.setText("Total Score: " + totalScore);
//            bestScoreText.setText("Best Score: " + bestScore);
//            int progress = (int) ((completedCategories / (float) categoryCount) * 100);
//            categoryProgressText.setText("Category Progress: " + progress + "%");
//        }
//    }

}
