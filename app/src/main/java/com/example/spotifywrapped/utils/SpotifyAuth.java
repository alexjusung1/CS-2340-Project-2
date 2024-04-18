package com.example.spotifywrapped.utils;

import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SpotifyAuth {
    private static volatile String codeVerifier;
    private static volatile String accessToken;
    private static volatile String refreshToken;

    private static final String redirectURI = "https://spotifywrappedapp-819f6.firebaseapp.com/app-data";
    private static final String clientID = "5f164b1b815e411298a2df84bae6ddbb";
    private static final MediaType urlEncoded = MediaType.get("application/x-www-form-urlencoded");

    private static final OkHttpClient authClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

    private static final String authCodeURI = "https://accounts.spotify.com/authorize";
    private static final String tokenURI = "https://accounts.spotify.com/api/token";

    private static final String codeChallengeMethod = "S256";
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~";

    // Whether the access token is valid; used to wait until available
    private static volatile boolean accessTokenValid;
    private static volatile boolean isLoggedIn;
    private static final Lock changeTokenLock = new ReentrantLock();
    private static final Condition tokenValidCondition = changeTokenLock.newCondition();

    private static volatile Instant refreshTime;

    private static final String TAG = "SpotifyAuth";

    @NonNull
    public static Intent getAuthorizationIntent() {
        final String responseType = "code";
        String scope = "user-top-read";

        codeVerifier = genCodeVerifier();
        Log.d(TAG, "Generated code verifier: " + codeVerifier);

        final String codeChallenge = getCodeChallenge(codeVerifier);
        Log.d(TAG, "Code challenge: " + codeChallenge);

        String authCodeEncodedURL = authCodeURI
                .concat("?response_type=" + responseType)
                .concat("&client_id=" + clientID)
                .concat("&redirect_uri=" + redirectURI)
                .concat("&scope=" + scope)
                .concat("&code_challenge_method=" + codeChallengeMethod)
                .concat("&code_challenge=" + codeChallenge);

        return new Intent(Intent.ACTION_VIEW, Uri.parse(authCodeEncodedURL));
    }

    public static void parseAuthorizationResponse(Uri response) {
        String authorizationCode = response.getQueryParameter("code");
        Log.d(TAG, "Authorization Code: " + authorizationCode);

        CompletableFuture.runAsync(() -> getAccessTokenAsync(authorizationCode));
        CompletableFuture.runAsync(SpotifyDataHolder::updateUserDataAsync);
    }

    public static String returnAccessTokenAsync() {
        changeTokenLock.lock();
        try {
            if (!accessTokenValid) {
                tokenValidCondition.await();
                if (isLoggedOut()) return null;
            }

            if (Instant.now().compareTo(refreshTime) >= 0) {
                refreshAccessTokenAsync();
            }
            return accessToken;
        } catch (InterruptedException e) {
            Log.e(TAG, "Error while waiting for refresh");
            throw new RuntimeException(e);
        } finally {
            changeTokenLock.unlock();
        }
    }

    public static boolean isLoggedOut() {
        return !isLoggedIn;
    }

    public static void logoutAsync() {
        refreshTime = null;
        isLoggedIn = false;
        accessTokenValid = false;

        changeTokenLock.lock();
        try {
            tokenValidCondition.signalAll();
        } finally {
            changeTokenLock.unlock();
        }
        // TODO: invalidate SpotifyDataHolder values
    }

    public static void initializeLoginAsync(FirestoreUpdate firestoreUpdate) {
        changeTokenLock.lock();
        try {
            Pair<String, String> savedLoginInfo = firestoreUpdate.retrieveSpotifyAuthAsync();
            codeVerifier = savedLoginInfo.first;
            refreshToken = savedLoginInfo.second;
            if (codeVerifier != null && refreshToken != null) {
                refreshAccessTokenAsync();
                CompletableFuture.runAsync(SpotifyDataHolder::updateUserDataAsync);
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.e("SpotifyAuth", "Error while loading past login info");
        } finally {
            changeTokenLock.unlock();
        }
    }

    private static void getAccessTokenAsync(String authorizationCode) {
        changeTokenLock.lock();
        try {
            String formData = "grant_type=authorization_code"
                    .concat("&code=" + authorizationCode)
                    .concat("&redirect_uri=" + redirectURI)
                    .concat("&client_id=" + clientID)
                    .concat("&code_verifier=" + codeVerifier);

            RequestBody body = RequestBody.create(formData, urlEncoded);

            Request request = new Request.Builder()
                    .url(tokenURI)
                    .post(body)
                    .build();

            try (Response response = authClient.newCall(request).execute()) {
                parseTokenResponse(response.body().charStream());
                isLoggedIn = true;
                accessTokenValid = true;
                tokenValidCondition.signalAll();
            } catch (IOException e) {
                Log.e(TAG, "Error while getting access token");
                e.printStackTrace();
            }
        } finally {
            changeTokenLock.unlock();
        }
    }

    private static void refreshAccessTokenAsync() {
        changeTokenLock.lock();
        try {
            Log.d(TAG, "Refreshing Access Token");
            RequestBody formBody = new FormBody.Builder()
                    .addEncoded("grant_type", "refresh_token")
                    .addEncoded("refresh_token", refreshToken)
                    .addEncoded("client_id", clientID)
                    .build();

            Request request = new Request.Builder()
                    .url(tokenURI)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(formBody)
                    .build();

            try (Response response = authClient.newCall(request).execute()) {
                parseTokenResponse(response.body().charStream());
                isLoggedIn = true;
                accessTokenValid = true;
                tokenValidCondition.signalAll();
            } catch (IOException e) {
                Log.e(TAG, "Error while refreshing access token");
                e.printStackTrace();
            }
        } finally {
            changeTokenLock.unlock();
        }
    }

    private static void parseTokenResponse(Reader jsonReader) {
        JsonObject tokenBody = JsonParser.parseReader(jsonReader)
                .getAsJsonObject();

        accessToken = tokenBody.get("access_token").getAsString();
        Log.d(TAG, "Access Token: " + accessToken);

        refreshToken = tokenBody.get("refresh_token").getAsString();
        int timeout = tokenBody.get("expires_in").getAsInt();

        refreshTime = Instant.now().plusSeconds(timeout);

        FirestoreUpdate firestoreUpdate = new FirestoreUpdate(FirebaseFirestore.getInstance(),
                FirebaseAuth.getInstance().getUid());
        CompletableFuture.runAsync(() ->
                firestoreUpdate.updateRefreshTokenAsync(codeVerifier, refreshToken));
    }

    private static String genCodeVerifier() {
        SecureRandom rng = new SecureRandom();

        return rng.ints(0, ALLOWED_CHARS.length())
                .limit(64)
                .mapToObj(ALLOWED_CHARS::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private static String getCodeChallenge(String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return Base64.encodeToString(hash, Base64.URL_SAFE | Base64.NO_PADDING);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SpotifyAuth -- SHA-256 not available");
        }
    }
}
