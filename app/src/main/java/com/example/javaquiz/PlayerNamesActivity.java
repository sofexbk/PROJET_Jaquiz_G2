package com.example.javaquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class PlayerNamesActivity extends AppCompatActivity {

    private int numPlayers;
    private ArrayList<EditText> playerInputs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_names);

        int numPlayers = getIntent().getIntExtra("NUM_PLAYERS", 2);

        // Get references to input layouts
        View player3Layout = findViewById(R.id.player3_input_layout);
        View player4Layout = findViewById(R.id.player4_input_layout);

        // Show only the necessary input fields
        if (numPlayers < 3) {
            player3Layout.setVisibility(View.GONE);
        } else {
            player3Layout.setVisibility(View.VISIBLE);
        }

        if (numPlayers < 4) {
            player4Layout.setVisibility(View.GONE);
        } else {
            player4Layout.setVisibility(View.VISIBLE);
        }

        // Handle Start Game button
        findViewById(R.id.btn_start_game).setOnClickListener(v -> {
            ArrayList<String> playerNames = new ArrayList<>();
            playerNames.add(((TextInputEditText) findViewById(R.id.player1_input)).getText().toString().trim());
            playerNames.add(((TextInputEditText) findViewById(R.id.player2_input)).getText().toString().trim());

            if (numPlayers >= 3) {
                playerNames.add(((TextInputEditText) findViewById(R.id.player3_input)).getText().toString().trim());
            }

            if (numPlayers >= 4) {
                playerNames.add(((TextInputEditText) findViewById(R.id.player4_input)).getText().toString().trim());
            }

            // Ensure all names are entered
            for (String name : playerNames) {
                if (name.isEmpty()) {
                    Toast.makeText(this, "Veuillez entrer tous les noms des joueurs", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Pass player names to the next activity
            Intent intent = new Intent(PlayerNamesActivity.this, GameActivity.class);
            intent.putStringArrayListExtra("PLAYER_NAMES", playerNames);
            startActivity(intent);
        });
    }

}