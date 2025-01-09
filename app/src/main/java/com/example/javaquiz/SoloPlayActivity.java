package com.example.javaquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SoloPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_play);


        findViewById(R.id.card_practice_mode).setOnClickListener(v -> {
            Intent intent = new Intent(SoloPlayActivity.this, LevelSelectionActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        findViewById(R.id.card_exam_mode).setOnClickListener(v -> {
//            Intent intent = new Intent(SoloPlayActivity.this, ExamModeActivity.class);
//            startActivity(intent);
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}


