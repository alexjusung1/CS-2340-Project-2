package com.example.spotifywrapped.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotifywrapped.R;
import com.example.spotifywrapped.data.SpotifyUserData;
import com.example.spotifywrapped.utils.SpotifyAuth;
import com.example.spotifywrapped.utils.SpotifyDataHolder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.CompletableFuture;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link AppCompatActivity} subclass.
 * Use the {@link SettingsActivity} factory method to
 * create an instance of this fragment.
 */
public class SettingsActivity extends AppCompatActivity {
    FirebaseAuth fAuth;

    FirebaseFirestore fStore;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);

        ImageView settingsBack = findViewById(R.id.backSettings);

        TextView logOutFirebase = findViewById(R.id.logout_account_firebase);

        TextView deleteAccountFirebase = findViewById(R.id.remove_account_firebase);

        TextView username = findViewById(R.id.username);
        CircleImageView userImage = findViewById(R.id.user_image);

        TextView spotifyLogOut = findViewById(R.id.SpotifyLogOutButton);

        SpotifyDataHolder.getCurrentUserData()
                .thenApplyAsync(spotifyUserData -> {
                    runOnUiThread(() -> {
                        username.setText(spotifyUserData.getUsername());
                    });
                    return spotifyUserData.getProfileImageAsync();
                }).thenAccept(bitmap -> runOnUiThread(() -> {
                    userImage.setImageBitmap(bitmap);
                }));

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        logOutFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                finish();
            }
        });

        deleteAccountFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Delete from Database

                userID = fAuth.getUid();
                DocumentReference documentReferenceToDelete = fStore.collection("users").document(userID);
                documentReferenceToDelete.delete();

                // Delete from Auth
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                user.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to delete account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });

        settingsBack.setOnClickListener(v -> finish());
    }
}