package com.example.javaquiz;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activité pour le mode multijoueur du quiz.
 * Permet à l'utilisateur de sélectionner le nombre de joueurs et de passer à l'écran de saisie des noms des joueurs.
 */
public class MultiplayerActivity extends AppCompatActivity {

    /**
     * Appelée lors de la création de l'activité.
     * Initialise les vues et définit les écouteurs de clic pour chaque carte de sélection du nombre de joueurs.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        // Définir les écouteurs de clic pour chaque option de nombre de joueurs
        findViewById(R.id.card_2players).setOnClickListener(v -> navigateToPlayerNames(2));
        findViewById(R.id.card_3players).setOnClickListener(v -> navigateToPlayerNames(3));
        findViewById(R.id.card_4players).setOnClickListener(v -> navigateToPlayerNames(4));
    }

    /**
     * Navigue vers l'écran des noms des joueurs en fonction du nombre de joueurs sélectionné.
     *
     * @param numPlayers Le nombre de joueurs (2, 3 ou 4)
     */
    private void navigateToPlayerNames(int numPlayers) {
        // Crée une nouvelle intention pour passer à l'activité des noms des joueurs
        Intent intent = new Intent(MultiplayerActivity.this, PlayerNamesActivity.class);
        intent.putExtra("NUM_PLAYERS", numPlayers); // Passe le nombre de joueurs comme extra
        startActivity(intent); // Démarre l'activité
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // Animation de transition
    }

}
