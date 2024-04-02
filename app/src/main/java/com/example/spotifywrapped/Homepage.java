package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Homepage extends AppCompatActivity {
    static int pastPage = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        Button rewrap_info = findViewById(R.id.rewrapinfo);
        Button past_rewrap = findViewById(R.id.past_rewrap);
//        change later

        rewrap_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pastPage = 0;
                Intent intent = new Intent(Homepage.this, RewrapInfoPage.class);
                startActivity(intent);
            }
        });

        past_rewrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pastPage = 0;
                Intent intent = new Intent(Homepage.this, RewrapPage.class);
                startActivity(intent);
            }
        });
    }
}