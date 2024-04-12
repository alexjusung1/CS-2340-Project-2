package com.example.spotifywrapped;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.spotifywrapped.data.RewrappedSummary;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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
        DocumentReference documentReference = fStore.collection("users").document(userID);
        Map<String, Object> topSpotifyInfo = new HashMap<>();
        topSpotifyInfo.put("topInfo", summary);
        documentReference.update(topSpotifyInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: Top Spotify Info successfully updated for " + userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: Error updating Top Spotify info for " + userID, e);
            }
        });
    }
}
