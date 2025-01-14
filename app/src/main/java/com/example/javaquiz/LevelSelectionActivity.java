package com.example.javaquiz;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activité permettant à l'utilisateur de sélectionner le niveau de difficulté pour le mode pratique.
 * L'utilisateur peut choisir entre trois niveaux : débutant, intermédiaire, et avancé.
 * Chaque sélection mène à l'activité de mode pratique avec le niveau correspondant.
 */
public class LevelSelectionActivity extends AppCompatActivity {

    /**
     * Méthode appelée lors de la création de l'activité.
     * Elle configure les boutons pour chaque niveau de difficulté et gère les clics.
     *
     * @param savedInstanceState L'état de l'activité, si elle a été précédemment enregistrée.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);

        // Configurer le bouton pour le niveau débutant
        findViewById(R.id.btnBeginner).setOnClickListener(v -> {
            // Lancer l'activité PracticeModeActivity avec le niveau "beginner"
            startPracticeMode("beginner");
        });

        // Configurer le bouton pour le niveau intermédiaire
        findViewById(R.id.btnIntermediate).setOnClickListener(v -> {
            // Lancer l'activité PracticeModeActivity avec le niveau "intermediate"
            startPracticeMode("intermediate");
        });

        // Configurer le bouton pour le niveau avancé
        findViewById(R.id.btnAdvanced).setOnClickListener(v -> {
            // Lancer l'activité PracticeModeActivity avec le niveau "advanced"
            startPracticeMode("advanced");
        });
    }

    /**
     * Méthode pour lancer l'activité PracticeModeActivity avec le niveau spécifié.
     *
     * @param level Le niveau de difficulté à envoyer à l'activité suivante.
     */
    private void startPracticeMode(String level) {
        // Créer un intent pour démarrer l'activité PracticeModeActivity
        Intent intent = new Intent(LevelSelectionActivity.this, PracticeModeActivity.class);

        // Ajouter le niveau sélectionné à l'intent
        intent.putExtra("LEVEL", level);

        // Démarrer l'activité et appliquer une transition d'animation
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
