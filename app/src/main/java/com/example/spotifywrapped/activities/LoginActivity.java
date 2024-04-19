package com.example.spotifywrapped.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifywrapped.R;
import com.example.spotifywrapped.utils.FirestoreDataHolder;
import com.example.spotifywrapped.utils.FirestoreUpdate;
import com.example.spotifywrapped.utils.SpotifyAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView moveButton;
    FirebaseAuth fAuth;

    FirebaseFirestore fStore;
    TextView forgotPasswordFirebase;
    String userID;

    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        login = findViewById(R.id.buttonLogin);
        moveButton = findViewById(R.id.moveSignUp);

        forgotPasswordFirebase = findViewById(R.id.forgotPassword_firebase);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();

                // Checking for email/password empty
                if (TextUtils.isEmpty(emailText)) {
                    email.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(passwordText)) {
                    password.setError("Password is required");
                    return;
                }

                // Checking for password constraints
                if (password.length() < 8) {
                    password.setError("Password must be at least 8 characters long");
                }

                // Moving activities if the task is successful
                fAuth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirestoreUpdate firestoreUpdate = new FirestoreUpdate(FirebaseFirestore.getInstance(),
                                    FirebaseAuth.getInstance().getUid());
                            CompletableFuture.runAsync(() -> FirestoreDataHolder.initializeListAsync(firestoreUpdate));
                            CompletableFuture.runAsync(() -> SpotifyAuth.initializeLoginAsync(firestoreUpdate));
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, Homepage.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        forgotPasswordFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the user's email address
                String emailAddress = email.getText().toString().trim();
                // Check if email address is empty
                if (TextUtils.isEmpty(emailAddress)) {
                    Toast.makeText(LoginActivity.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                fAuth.sendPasswordResetEmail(emailAddress)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Error! Please fill out the email section ", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        TextView moveButton = findViewById(R.id.moveSignUp);
        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}
