package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class RewrapPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewrap_page);

        ImageView backBtn = findViewById(R.id.back);
        Button frontBtn = findViewById(R.id.forward);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RewrapPage.this, Homepage.class);
                startActivity(intent);
            }
        });

        frontBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Homepage.pastPage = 1;
                Intent intent = new Intent(RewrapPage.this, RewrapInfoPage.class);
                startActivity(intent);
            }
        });
    }
}