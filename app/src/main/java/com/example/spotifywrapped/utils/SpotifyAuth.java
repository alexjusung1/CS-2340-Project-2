package com.example.spotifywrapped.utils;

import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.spotifywrapped.FirestoreUpdate;
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
    private static volatile String authorizationCode;
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

    private static volatile boolean accessTokenValid;
    private static final Lock changeTokenLock = new ReentrantLock();
    private static final Condition tokenValid = changeTokenLock.newCondition();

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
        authorizationCode = response.getQueryParameter("code");
        Log.d(TAG, "Authorization Code: " + authorizationCode);

        CompletableFuture.runAsync(SpotifyAuth::getAccessTokenAsync);
    }

    public static String returnAccessTokenAsync() {
        if (isLoggedOut()) return null;
        changeTokenLock.lock();
        try {
            if (!accessTokenValid) tokenValid.await();

            if (Instant.now().compareTo(refreshTime) >= 0) {
                SpotifyAuth.refreshAccessTokenAsync();
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
        return authorizationCode == null;
    }

    public static void logout() {
        authorizationCode = null;
        refreshTime = null;
    }

    private static void getAccessTokenAsync() {
        if (isLoggedOut()) return;
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
                accessTokenValid = true;
                tokenValid.signalAll();
            } catch (IOException e) {
                Log.e(TAG, "Error while getting access token");
                e.printStackTrace();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        } finally {
            changeTokenLock.unlock();
        }
    }

    private static void refreshAccessTokenAsync() {
        if (isLoggedOut()) return;
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
                accessTokenValid = true;
                tokenValid.signalAll();
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

        // Firebase Stuff
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userID = fAuth.getUid();
        FirestoreUpdate firestoreUpdate = new FirestoreUpdate(fStore, userID);
        firestoreUpdate.updateFireStore(codeVerifier, authorizationCode);
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
