package com.example.spotifywrapped.utils;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.example.spotifywrapped.data.RewrappedSummary;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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

    public Task<QuerySnapshot> retrieveDatabaseInfo() {
        // Get a reference to the collection
        CollectionReference collectionRef = fStore.collection("users")
                .document(userID).collection("summaries");

        // Retrieve the documents in the collection
        return collectionRef.get();
    }

    public Task<Void> updateRewrappedSummary(RewrappedSummary summary, int position) {
        DocumentReference userDocumentRef = fStore.collection("users").document(userID)
                .collection("summaries").document(String.valueOf(position));
        return userDocumentRef.set(summary);
    }

    public Pair<String, String> retrieveSpotifyAuthAsync() throws ExecutionException, InterruptedException {
        DocumentReference documentRef = fStore.collection("users").document(userID);
        DocumentSnapshot snapshot = Tasks.await(documentRef.get());

        String codeVerifier = snapshot.getString("codeVerifier");
        String refreshToken = snapshot.getString("refreshToken");

        return new Pair<>(codeVerifier, refreshToken);
    }

    public void updateRefreshTokenAsync(String codeVerifier, String refreshToken) {
        DocumentReference documentRef = fStore.collection("users").document(userID);
        Map<String, String> newContents = new HashMap<>();
        newContents.put("codeVerifier", codeVerifier);
        newContents.put("refreshToken", refreshToken);
        try {
            Tasks.await(documentRef.set(newContents, SetOptions.merge()));
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Error while updating refresh token");
            throw new RuntimeException(e);
        }
    }

    public void removeSpotifyAccountAsync() {
        DocumentReference documentRef = fStore.collection("users").document(userID);
        Map<String, Object> updates = new HashMap<>();
        updates.put("codeVerifier", FieldValue.delete());
        updates.put("refreshToken", FieldValue.delete());
        try {
            Tasks.await(documentRef.update(updates));
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Error while removing Spotify account");
            throw new RuntimeException(e);
        }
    }
}