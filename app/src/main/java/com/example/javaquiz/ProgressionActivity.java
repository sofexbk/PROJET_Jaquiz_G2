package com.example.javaquiz;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javaquiz.Utils.QuizDatabaseHelper;

/**
 * Cette activité affiche les statistiques de progression de l'utilisateur dans chaque catégorie de quiz
 * (beginner, intermediate, advanced) pour le mode "Practice".
 */
public class ProgressionActivity extends AppCompatActivity {
    private QuizDatabaseHelper dbHelper;
    private LinearLayout categoriesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progression);

        // Initialisation du helper pour accéder à la base de données
        dbHelper = new QuizDatabaseHelper(this);

        // Conteneur pour afficher les sections des catégories
        categoriesContainer = findViewById(R.id.categoriesContainer);

        // Chargement des statistiques de l'utilisateur
        loadUserStats();
    }

    /**
     * Charge les statistiques de l'utilisateur depuis la base de données et affiche
     * les scores pour chaque catégorie (Beginner, Intermediate, Advanced).
     */
    @SuppressLint("Range")
    private void loadUserStats() {
        // Récupère les statistiques des catégories via une requête SQL
        Cursor cursor = dbHelper.getCategoryStatsForPracticeMode();

        if (cursor != null && cursor.moveToFirst()) {
            // Ajouter la section "Beginner"
            addCategorySection("Beginner",
                    cursor.getInt(cursor.getColumnIndex("beginner_last_score")),
                    cursor.getInt(cursor.getColumnIndex("beginner_best_score")));

            // Ajouter la section "Intermediate"
            addCategorySection("Intermediate",
                    cursor.getInt(cursor.getColumnIndex("intermediate_last_score")),
                    cursor.getInt(cursor.getColumnIndex("intermediate_best_score")));

            // Ajouter la section "Advanced"
            addCategorySection("Advanced",
                    cursor.getInt(cursor.getColumnIndex("advanced_last_score")),
                    cursor.getInt(cursor.getColumnIndex("advanced_best_score")));
        }

        // Fermer le curseur après utilisation
        if (cursor != null) {
            cursor.close();
        }
    }

    /**
     * Crée et ajoute une section de catégorie (avec les scores derniers et meilleurs)
     * dans la vue de l'activité.
     *
     * @param category Le nom de la catégorie (Beginner, Intermediate, Advanced)
     * @param lastScore Le récent score obtenu dans cette catégorie
     * @param bestScore Le meilleur score obtenu dans cette catégorie
     */
    private void addCategorySection(String category, int lastScore, int bestScore) {
        // Gonfler la vue pour la section de statistiques de catégorie
        View sectionView = getLayoutInflater().inflate(R.layout.item_category_stats, categoriesContainer, false);

        // Initialiser les vues pour afficher les titres et les scores
        TextView categoryTitle = sectionView.findViewById(R.id.categoryTitle);
        TextView lastScoreText = sectionView.findViewById(R.id.lastScoreText);
        TextView bestScoreText = sectionView.findViewById(R.id.bestScoreText);

        // Mettre à jour le texte avec les informations de catégorie
        categoryTitle.setText(category);
        lastScoreText.setText(String.format("Last Score: %d", lastScore));
        bestScoreText.setText(String.format("Best Score: %d", bestScore));

        // Ajouter la vue de la section à la vue principale
        categoriesContainer.addView(sectionView);
    }
}
