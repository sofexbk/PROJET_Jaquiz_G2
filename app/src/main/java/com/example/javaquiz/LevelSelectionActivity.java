package com.example.javaquiz;

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

        Button btnBeginner = findViewById(R.id.btnBeginner);
        Button btnIntermediate = findViewById(R.id.btnIntermediate);
        Button btnAdvanced = findViewById(R.id.btnAdvanced);

        // Define the OnClickListener for all buttons
        View.OnClickListener levelClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String level = "beginner"; // Default level

                // Use if-else logic to identify which button was clicked
                if (view.getId() == R.id.btnIntermediate) {
                    level = "intermediate";
                } else if (view.getId() == R.id.btnAdvanced) {
                    level = "advanced";
                }

                // Pass the selected level to PracticeModeActivity
                Intent intent = new Intent(LevelSelectionActivity.this, PracticeModeActivity.class);
                intent.putExtra("LEVEL", level);
                startActivity(intent);
                finish();  // Optional: Finish LevelSelectionActivity so user can't go back
            }
        };

        // Set OnClickListener for the buttons
        btnBeginner.setOnClickListener(levelClickListener);
        btnIntermediate.setOnClickListener(levelClickListener);
        btnAdvanced.setOnClickListener(levelClickListener);
    }
}
