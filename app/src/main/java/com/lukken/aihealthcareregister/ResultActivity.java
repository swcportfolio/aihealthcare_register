package com.lukken.aihealthcareregister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 결과 화면
 */
public class ResultActivity extends AppCompatActivity {

    private TextView uCodeTextView;
    private TextView checkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

       Intent intent = getIntent();
       String uCode = intent.getStringExtra("ucode");

        uCodeTextView = findViewById(R.id.ucode);
        uCodeTextView.setText(uCode);

        checkButton = findViewById(R.id.resultCheck);
        checkButton.setOnClickListener(view -> {
            setResult(RESULT_OK, intent);
            finish();
        });

    }
}