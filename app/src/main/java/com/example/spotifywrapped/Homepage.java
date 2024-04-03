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

        Button rewrapInfo = findViewById(R.id.rewrapinfo);
        Button pastRewrap = findViewById(R.id.past_rewrap);
//        change later

        rewrapInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pastPage = 0;
                startActivity(new Intent(Homepage.this, RewrapInfoPage.class));
            }
        });

        pastRewrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pastPage = 0;
                startActivity(new Intent(Homepage.this, RewrapPage.class));
            }
        });
    }
}