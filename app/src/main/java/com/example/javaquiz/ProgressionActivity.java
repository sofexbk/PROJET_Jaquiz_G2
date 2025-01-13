package com.example.javaquiz;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.javaquiz.Utils.QuizDatabaseHelper;


public class ProgressionActivity extends AppCompatActivity {
    private QuizDatabaseHelper dbHelper;
    private LinearLayout categoriesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progression);

        dbHelper = new QuizDatabaseHelper(this);
        categoriesContainer = findViewById(R.id.categoriesContainer);

        loadUserStats();

    }

    @SuppressLint("Range")
    private void loadUserStats() {
        Cursor cursor = dbHelper.getCategoryStatsForPracticeMode();

        if (cursor != null && cursor.moveToFirst()) {
            // Beginner Section
            addCategorySection("Beginner",
                    cursor.getInt(cursor.getColumnIndex("beginner_last_score")),
                    cursor.getInt(cursor.getColumnIndex("beginner_best_score")));

            // Intermediate Section
            addCategorySection("Intermediate",
                    cursor.getInt(cursor.getColumnIndex("intermediate_last_score")),
                    cursor.getInt(cursor.getColumnIndex("intermediate_best_score")));

            // Advanced Section
            addCategorySection("Advanced",
                    cursor.getInt(cursor.getColumnIndex("advanced_last_score")),
                    cursor.getInt(cursor.getColumnIndex("advanced_best_score")));
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    private void addCategorySection(String category, int lastScore, int bestScore) {
        View sectionView = getLayoutInflater().inflate(R.layout.item_category_stats, categoriesContainer, false);

        TextView categoryTitle = sectionView.findViewById(R.id.categoryTitle);
        TextView lastScoreText = sectionView.findViewById(R.id.lastScoreText);
        TextView bestScoreText = sectionView.findViewById(R.id.bestScoreText);

        categoryTitle.setText(category);
        lastScoreText.setText(String.format("Last Score: %d", lastScore));
        bestScoreText.setText(String.format("Best Score: %d", bestScore));

        categoriesContainer.addView(sectionView);
    }
}
