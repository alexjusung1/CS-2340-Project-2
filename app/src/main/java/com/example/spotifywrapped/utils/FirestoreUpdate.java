package com.example.spotifywrapped.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.spotifywrapped.data.ArtistData;
import com.example.spotifywrapped.data.RewrappedSummary;
import com.example.spotifywrapped.data.TimeRange;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FirestoreUpdate {

    // FireStore
    private final FirebaseFirestore fStore;
    private final String userID;

    public FirestoreUpdate(FirebaseFirestore fStore, String userID) {
        // Firebase
        this.fStore = fStore;
        this.userID = userID;
    }
    private static final String TAG = "FirestoreUpdate";
    public void updateFireStore(String codeVerifier, String authorizationCode) {
        DocumentReference documentReference = fStore.collection("users").document(userID);
        Map<String, Object> userSpotifyInfo = new HashMap<>();
        userSpotifyInfo.put("codeVerifier", codeVerifier);
        userSpotifyInfo.put("authorizationCode", authorizationCode);
        documentReference.update(userSpotifyInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: User Spotify Info successfully updated for " + userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: Error updating user Spotify info for " + userID, e);
            }
        });
    }
    public void updateSpotifyFireStore(RewrappedSummary summary) {
        String summaryID = "ID" + UUID.randomUUID().toString();
        DocumentReference userDocumentRef = fStore.collection("users").document(userID).collection(summaryID).document();
        userDocumentRef.set(summary)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Top Spotify Info successfully updated for " + userID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: Error updating Top Spotify info for " + userID, e);
                    }
                });
    }
}
