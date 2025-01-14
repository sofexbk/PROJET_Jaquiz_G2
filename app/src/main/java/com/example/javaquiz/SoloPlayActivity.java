package com.example.javaquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Cette activité représente l'écran de jeu solo, où l'utilisateur peut choisir entre le mode pratique
 * et le mode examen. Chaque mode redirige l'utilisateur vers une activité correspondante.
 */
public class SoloPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_play);

        // Gestion du clic sur le mode "Practice"
        findViewById(R.id.card_practice_mode).setOnClickListener(v -> {
            // Lancer l'activité LevelSelectionActivity lorsque l'utilisateur choisit le mode "Practice"
            Intent intent = new Intent(SoloPlayActivity.this, LevelSelectionActivity.class);
            startActivity(intent);
            // Transition visuelle entre les activités (effet fade-in et fade-out)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Gestion du clic sur le mode "Exam"
        findViewById(R.id.card_exam_mode).setOnClickListener(v -> {
            // Lancer l'activité ExamModeActivity lorsque l'utilisateur choisit le mode "Exam"
            Intent intent = new Intent(SoloPlayActivity.this, ExamModeActivity.class);
            startActivity(intent);
            // Transition visuelle entre les activités (effet fade-in et fade-out)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}
