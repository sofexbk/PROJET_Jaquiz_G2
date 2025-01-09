package com.example.javaquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MultiplayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);


        findViewById(R.id.card_2players).setOnClickListener(v -> navigateToPlayerNames(2));
        findViewById(R.id.card_3players).setOnClickListener(v -> navigateToPlayerNames(3));
        findViewById(R.id.card_4players).setOnClickListener(v -> navigateToPlayerNames(4));
    }

    private void navigateToPlayerNames(int numPlayers) {
        Intent intent = new Intent(MultiplayerActivity.this, PlayerNamesActivity.class);
        intent.putExtra("NUM_PLAYERS", numPlayers);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}
