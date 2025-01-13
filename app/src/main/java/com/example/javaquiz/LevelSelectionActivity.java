package com.example.javaquiz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LevelSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);

        findViewById(R.id.btnBeginner).setOnClickListener(v -> {
            Intent intent = new Intent(LevelSelectionActivity.this, PracticeModeActivity.class);
            String level = "beginner";
            intent.putExtra("LEVEL", level);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        findViewById(R.id.btnIntermediate).setOnClickListener(v -> {
            Intent intent = new Intent(LevelSelectionActivity.this, PracticeModeActivity.class);
            String level = "intermediate";
            intent.putExtra("LEVEL", level);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        findViewById(R.id.btnAdvanced).setOnClickListener(v -> {
            Intent intent = new Intent(LevelSelectionActivity.this, PracticeModeActivity.class);
            String level = "advanced";
            intent.putExtra("LEVEL", level);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}
