package com.example.javaquiz;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activité principale du jeu qui sert d'écran d'accueil.
 * Elle permet à l'utilisateur de naviguer vers différentes sections du jeu.
 */
public class HomeActivity extends AppCompatActivity {

    /**
     * Méthode appelée lors de la création de l'activité.
     * Elle initialise les éléments de l'interface et définit les actions à effectuer lors du clic sur chaque carte.
     *
     * @param savedInstanceState L'état précédent de l'activité, utilisé pour restaurer les données si nécessaire.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Clic sur la carte "Solo Play" pour démarrer une partie solo
        findViewById(R.id.card_solo_play).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SoloPlayActivity.class);
            startActivity(intent);
            // Transition d'animation entre les écrans
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Clic sur la carte "Multiplayer" pour démarrer une partie multijoueur
        findViewById(R.id.card_multiplayer).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MultiplayerActivity.class);
            startActivity(intent);
            // Transition d'animation entre les écrans
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Clic sur la carte "Progression" pour afficher la progression de l'utilisateur
        findViewById(R.id.card_progression).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProgressionActivity.class);
            startActivity(intent);
            // Transition d'animation entre les écrans
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}
