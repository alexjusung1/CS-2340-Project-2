package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RewrapInfoPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewrap_info_page);

        Button btnBack = findViewById(R.id.back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Homepage.pastPage == 0) {
                    startActivity(new Intent(RewrapInfoPage.this, Homepage.class));
                } else if (Homepage.pastPage == 1) {
                    startActivity(new Intent(RewrapInfoPage.this, RewrapPage.class));
                }
            }
        });
    }

}
