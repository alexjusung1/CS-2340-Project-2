package com.example.spotifywrapped.utils;

import android.util.Log;

import com.example.spotifywrapped.data.RewrappedSummary;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FirestoreDataHolder {
    private static List<RewrappedSummary> summaryList;
    private static final Lock summaryListLock = new ReentrantLock();
    private static final Condition isReady = summaryListLock.newCondition();

    public static void initializeListAsync(FirestoreUpdate firestoreUpdate) {
        summaryListLock.lock();
        try {
            QuerySnapshot snapshot = Tasks.await(firestoreUpdate.retrieveDatabaseInfo());
            summaryList = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot: snapshot) {
                summaryList.add(documentSnapshot.toObject(RewrappedSummary.class));
            }
            isReady.signalAll();
        } catch (RuntimeException e) {
            Log.e("FirestoreDataHolder", "error while parsing");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            summaryListLock.unlock();
        }
    }

    public static CompletableFuture<List<RewrappedSummary>> getPastSummaries() {
        return CompletableFuture.supplyAsync(() -> {
            summaryListLock.lock();
            try {
                if (summaryList == null) isReady.await();
                return summaryList;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                summaryListLock.unlock();
            }
        });
    }

    public static void addNewRewrappedSummaryAsync(FirestoreUpdate firestoreUpdate, RewrappedSummary summary) {
        summaryListLock.lock();
        try {
            summaryList.add(summary);
            Tasks.await(firestoreUpdate.updateSpotifyFireStore(summary, summaryList.size()));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            summaryListLock.unlock();
        }
    }
}
