package com.example.javaquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

/**
 * Activité permettant à l'utilisateur de saisir les noms des joueurs avant de commencer le jeu.
 * Le nombre de joueurs est dynamique, basé sur la configuration passée dans l'activité précédente.
 * Cette activité permet de récupérer les noms des joueurs et de les passer à l'activité suivante.
 */
public class PlayerNamesActivity extends AppCompatActivity {

    /**
     * Nombre de joueurs sélectionnés par l'utilisateur dans l'activité précédente.
     */
    private int numPlayers;

    /**
     * Méthode appelée lors de la création de l'activité. Elle initialise les vues et
     * gère l'affichage dynamique des champs de saisie pour les joueurs.
     * Elle gère également la logique du bouton "Start Game".
     *
     * @param savedInstanceState L'état de l'activité, si elle a été précédemment enregistrée.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_names);

        // Récupérer le nombre de joueurs à partir de l'activité précédente (par défaut 2 joueurs)
        numPlayers = getIntent().getIntExtra("NUM_PLAYERS", 2);

        // Récupérer les mises en page pour les champs de saisie des joueurs 3 et 4
        View player3Layout = findViewById(R.id.player3_input_layout);
        View player4Layout = findViewById(R.id.player4_input_layout);

        // Afficher ou masquer les champs en fonction du nombre de joueurs sélectionnés
        if (numPlayers < 3) {
            player3Layout.setVisibility(View.GONE);  // Masquer le champ de saisie du joueur 3
        } else {
            player3Layout.setVisibility(View.VISIBLE);  // Afficher le champ de saisie du joueur 3
        }

        if (numPlayers < 4) {
            player4Layout.setVisibility(View.GONE);  // Masquer le champ de saisie du joueur 4
        } else {
            player4Layout.setVisibility(View.VISIBLE);  // Afficher le champ de saisie du joueur 4
        }

        // Gérer le clic sur le bouton "Start Game"
        findViewById(R.id.btn_start_game).setOnClickListener(v -> {
            ArrayList<String> playerNames = new ArrayList<>();

            // Récupérer les noms des joueurs à partir des champs de saisie
            playerNames.add(((TextInputEditText) findViewById(R.id.player1_input)).getText().toString().trim());
            playerNames.add(((TextInputEditText) findViewById(R.id.player2_input)).getText().toString().trim());

            // Ajouter les joueurs 3 et 4 si applicable
            if (numPlayers >= 3) {
                playerNames.add(((TextInputEditText) findViewById(R.id.player3_input)).getText().toString().trim());
            }

            if (numPlayers >= 4) {
                playerNames.add(((TextInputEditText) findViewById(R.id.player4_input)).getText().toString().trim());
            }

            // Vérifier que tous les champs de saisie sont remplis
            for (String name : playerNames) {
                if (name.isEmpty()) {
                    // Afficher un message d'erreur si un nom est manquant
                    Toast.makeText(this, "Veuillez entrer tous les noms des joueurs", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Passer la liste des noms des joueurs à l'activité suivante
            Intent intent = new Intent(PlayerNamesActivity.this, GameActivity.class);
            intent.putStringArrayListExtra("PLAYER_NAMES", playerNames);  // Ajouter la liste des noms à l'intent
            startActivity(intent);  // Démarrer l'activité suivante
        });
    }
}
